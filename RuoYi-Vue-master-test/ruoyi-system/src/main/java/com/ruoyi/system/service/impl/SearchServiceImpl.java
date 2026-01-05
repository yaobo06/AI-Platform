package com.ruoyi.system.service.impl;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.Post;
import com.ruoyi.system.domain.SearchResult;
import com.ruoyi.system.mapper.PostMapper;
import com.ruoyi.system.service.IEmbeddingService;
import com.ruoyi.system.service.ISearchService;
import com.ruoyi.system.service.IVectorDBService;

/**
 * 搜索服务实现
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@Service
public class SearchServiceImpl implements ISearchService 
{
    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private IEmbeddingService embeddingService;

    @Autowired
    private IVectorDBService vectorDBService;

    @Value("${deepseek.api.key:}")
    private String deepseekApiKey;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String deepseekApiUrl;

    @Value("${deepseek.prompt.deepseek:用户搜索关键词：%s\n\n请分析用户的搜索意图，并提取3-5个相关的搜索关键词（用逗号分隔）。关键词应该与论坛帖子相关，包括技术、设计、生活等话题。只返回关键词，不要其他解释。}")
    private String deepseekPromptTemplate;

    @Value("${deepseek.prompt.rag:用户搜索：%s\n\n请理解用户的搜索意图，并生成3-5个语义相关的搜索关键词（用逗号分隔）。这些关键词应该能够匹配论坛中相关的帖子内容。只返回关键词，不要其他解释。}")
    private String ragPromptTemplate;

    @Value("${deepseek.prompt.title-parse:请分析以下帖子标题，识别出主标题和副标题。\n\n标题：%s\n\n如果标题可以分成主标题和副标题，请按以下格式返回：\n主标题：xxx\n副标题：xxx\n\n如果标题没有明显的副标题，只返回：\n主标题：xxx\n\n只返回主标题和副标题的内容，不要其他解释。}")
    private String titleParsePromptTemplate;

    @Value("${search.ranking.like-weight:0.6}")
    private double likeWeight;

    @Value("${search.ranking.comment-weight:0.4}")
    private double commentWeight;

    @Value("${embeddings.rag.enabled:true}")
    private boolean ragEnabled;

    @Autowired
    private com.ruoyi.common.core.redis.RedisCache redisCache;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 标题解析缓存前缀
    private static final String TITLE_PARSE_CACHE_KEY = "title:parse:";
    // 缓存过期时间：7天
    private static final int TITLE_PARSE_CACHE_EXPIRE_DAYS = 7;
    
    // 搜索结果缓存前缀
    private static final String SEARCH_CACHE_KEY = "search:";
    // 搜索结果缓存过期时间：10分钟
    private static final int SEARCH_CACHE_EXPIRE_MINUTES = 10;

    @Override
    public List<Post> search(String query, String mode, int limit) 
    {
        if (StringUtils.isEmpty(query)) 
        {
            return Collections.emptyList();
        }

        // 1. 构建缓存键
        String cacheKey = buildSearchCacheKey(query, mode, limit);
        
        // 2. 先查 Redis 缓存
        try 
        {
            @SuppressWarnings("unchecked")
            List<Post> cachedResults = (List<Post>) redisCache.getCacheObject(cacheKey);
            if (cachedResults != null && !cachedResults.isEmpty()) 
            {
                log.debug("搜索结果缓存命中 - 查询: \"{}\", 模式: {}, 数量: {}", query, mode, cachedResults.size());
                return cachedResults;
            }
        } 
        catch (Exception e) 
        {
            log.warn("读取搜索结果缓存失败: {}", e.getMessage());
        }

        // 3. 缓存未命中，执行搜索
        List<Post> results;
        String modeLower = mode != null ? mode.toLowerCase() : "";
        if ("deepseek".equals(modeLower)) 
        {
            results = searchWithDeepSeek(query, limit);
        } 
        else if ("rag".equals(modeLower)) 
        {
            results = searchWithRAG(query, limit);
        } 
        else if ("db".equals(modeLower)) 
        {
            results = searchWithDatabase(query, limit);
        } 
        else 
        {
            results = searchWithHybrid(query, limit);
        }

        // 4. 将结果缓存到 Redis（异步更新，不阻塞）
        if (results != null && !results.isEmpty()) 
        {
            try 
            {
                redisCache.setCacheObject(cacheKey, results, SEARCH_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
                log.debug("搜索结果已缓存 - 查询: \"{}\", 模式: {}, 数量: {}", query, mode, results.size());
            } 
            catch (Exception e) 
            {
                log.warn("缓存搜索结果失败: {}", e.getMessage());
            }
        }

        return results;
    }

    /**
     * 智能混合搜索
     */
    private List<Post> searchWithHybrid(String query, int limit) 
    {
        // 1) 先直接数据库搜索
        List<Post> directResults = searchWithDatabase(query, limit);
        
        if (directResults.size() >= limit) 
        {
            log.debug("直接搜索结果充足（{}条），跳过后续扩展", directResults.size());
            return directResults;
        }

        // 2) 结果不足，尝试 DeepSeek 扩展查询（智能补充）
        List<Post> expandedResults = new ArrayList<Post>();
        
        if (StringUtils.isNotEmpty(deepseekApiKey)) 
        {
            try 
            {
                String expandedQuery = generateExpandedQuery(query, ragPromptTemplate, 0.4, 120);
                if (StringUtils.isNotEmpty(expandedQuery) && !expandedQuery.equals(query)) 
                {
                    log.info("DeepSeek 扩展查询: \"{}\" -> \"{}\"", query, expandedQuery);
                    
                    // 将扩展后的查询拆分成关键词数组进行搜索
                    String[] rawKeywords = expandedQuery.split("[,，、\\s]+");
                    List<String> processedKeywords = new ArrayList<String>();
                    
                    for (String raw : rawKeywords) 
                    {
                        String trimmed = raw.trim();
                        if (trimmed.length() > 1) 
                        {
                            // 提取核心关键词（去除常见后缀）
                            String core = extractCoreKeyword(trimmed);
                            if (StringUtils.isNotEmpty(core)) 
                            {
                                processedKeywords.add(core);
                            }
                            // 也保留原始关键词（可能包含重要信息）
                            if (!core.equals(trimmed)) 
                            {
                                processedKeywords.add(trimmed);
                            }
                        }
                    }
                    
                    String[] keywords = processedKeywords.toArray(new String[0]);
                    log.debug("处理后的关键词: {}", Arrays.toString(keywords));
                    
                    // 使用多关键词搜索
                    expandedResults = searchWithKeywords(keywords, limit);
                    log.debug("扩展查询找到 {} 条结果", expandedResults.size());
                }
            } 
            catch (Exception e) 
            {
                log.warn("DeepSeek 扩展查询失败，使用原始查询: {}", e.getMessage());
            }
        }

        // 3) 向量检索（如果启用且可用）
        List<Post> vectorResults = new ArrayList<Post>();
        if (ragEnabled && vectorDBService.isAvailable()) 
        {
            float[] vector = embeddingService.generateEmbedding(query, false);
            if (vector != null) 
            {
                vectorResults = searchWithVector(vector, limit);
                if (!vectorResults.isEmpty()) 
                {
                    log.debug("向量搜索找到 {} 条结果", vectorResults.size());
                }
            }
        }

        // 4) 合并所有结果，去重并按分数排序
        return mergeResults(directResults, expandedResults, vectorResults, limit);
    }

    /**
     * 数据库模糊搜索（使用 LIKE 查询）
     */
    private List<Post> searchWithDatabase(String query, int limit) 
    {
        log.debug("数据库搜索 - 查询: \"{}\"", query);
        // 使用 MyBatis 的 LIKE 查询，搜索标题和内容
        List<Post> list = postMapper.selectPostListByKeyword(query, limit * 2);
        
        return rankAndLimitPosts(list, limit);
    }

    /**
     * 使用关键词数组进行数据库搜索（支持多关键词 OR 查询）
     */
    private List<Post> searchWithKeywords(String[] keywords, int limit) 
    {
        if (keywords == null || keywords.length == 0) 
        {
            return Collections.emptyList();
        }
        
        Map<Long, Post> postMap = new HashMap<Long, Post>();
        for (String keyword : keywords) 
        {
            String trimmed = keyword.trim();
            if (StringUtils.isNotEmpty(trimmed)) 
            {
                // 搜索标题
                Post searchTitle = new Post();
                searchTitle.setTitle(trimmed);
                List<Post> titleList = postMapper.selectPostList(searchTitle);
                for (Post post : titleList) 
                {
                    postMap.putIfAbsent(post.getId(), post);
                }
                
                // 搜索内容
                Post searchContent = new Post();
                searchContent.setContent(trimmed);
                List<Post> contentList = postMapper.selectPostList(searchContent);
                for (Post post : contentList) 
                {
                    postMap.putIfAbsent(post.getId(), post);
                }
            }
        }
        
        List<Post> posts = new ArrayList<Post>(postMap.values());
        return rankAndLimitPosts(posts, limit);
    }

    /**
     * DeepSeek API智能搜索
     */
    private List<Post> searchWithDeepSeek(String query, int limit) 
    {
        return searchWithDeepSeekAPI(query, limit, deepseekPromptTemplate, 0.3, 100);
    }

    /**
     * 提取核心关键词（去除常见后缀）
     */
    private String extractCoreKeyword(String keyword) 
    {
        String[] suffixes = {"教程", "指南", "入门", "实战", "项目", "配置", "问题", "学习路线", 
                             "详解", "实践", "技巧", "方法", "经验", "推荐", "汇总"};
        String result = keyword;
        for (String suffix : suffixes) 
        {
            if (result.endsWith(suffix) && result.length() > suffix.length()) 
            {
                result = result.substring(0, result.length() - suffix.length());
                break;
            }
        }
        return result.trim();
    }

    /**
     * RAG 向量检索
     */
    private List<Post> searchWithRAG(String query, int limit) 
    {
        if (StringUtils.isEmpty(deepseekApiKey)) 
        {
            log.warn("DeepSeek API Key 未配置，RAG 降级到关键词搜索");
            return searchWithDeepSeekAPI(query, limit, ragPromptTemplate, 0.5, 150);
        }

        if (!vectorDBService.isAvailable()) 
        {
            log.warn("Milvus 不可用，RAG 降级到关键词搜索");
            return searchWithDeepSeekAPI(query, limit, ragPromptTemplate, 0.5, 150);
        }

        try 
        {
            float[] queryVector = embeddingService.generateEmbedding(query, false);
            
            if (queryVector == null) 
            {
                log.warn("向量生成失败，RAG 降级到关键词搜索");
                return searchWithDeepSeekAPI(query, limit, ragPromptTemplate, 0.5, 150);
            }

            List<Post> rankedPosts = searchWithVector(queryVector, limit);
            if (rankedPosts.isEmpty()) 
            {
                log.debug("向量搜索无结果，RAG 降级到数据库搜索");
                return searchWithDatabase(query, limit);
            }
            return rankedPosts;
        } 
        catch (Exception e) 
        {
            log.error("RAG 向量检索失败: {}", e.getMessage(), e);
            return searchWithDatabase(query, limit);
        }
    }

    /**
     * 调用 DeepSeek API 提取关键词并进行数据库搜索
     */
    private List<Post> searchWithDeepSeekAPI(String query, int limit, String promptTemplate, 
                                             double temperature, int maxTokens) 
    {
        if (StringUtils.isEmpty(deepseekApiKey)) 
        {
            return searchWithDatabase(query, limit);
        }

        String content = callDeepSeekAPI(query, promptTemplate, temperature, maxTokens);
        
        if (content != null && !content.isEmpty()) 
        {
            try 
            {
                String[] keywords = content.split("[,，、\\s]+");
                return searchWithKeywords(keywords, limit);
            } 
            catch (Exception e) 
            {
                log.error("处理 DeepSeek 响应失败: {}", e.getMessage(), e);
            }
        }
        
        return searchWithDatabase(query, limit);
    }

    /**
     * 调用 DeepSeek API
     */
    private String callDeepSeekAPI(String query, String promptTemplate, double temperature, int maxTokens) 
    {
        if (StringUtils.isEmpty(deepseekApiKey)) 
        {
            return null;
        }

        try 
        {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + deepseekApiKey);

            Map<String, Object> requestBody = new HashMap<String, Object>();
            requestBody.put("model", "deepseek-chat");
            String prompt = String.format(promptTemplate, query);

            List<Map<String, String>> messages = new ArrayList<Map<String, String>>();
            Map<String, String> message = new HashMap<String, String>();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);

            requestBody.put("messages", messages);
            requestBody.put("temperature", temperature);
            requestBody.put("max_tokens", maxTokens);

            HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(deepseekApiUrl, request, String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            if (jsonNode.has("choices") && jsonNode.get("choices").isArray() && jsonNode.get("choices").size() > 0) 
            {
                return jsonNode.get("choices").get(0).get("message").get("content").asText().trim();
            }
        } 
        catch (Exception e) 
        {
            log.error("DeepSeek API调用失败: {}", e.getMessage(), e);
        }
        
        return null;
    }

    /**
     * 调用 DeepSeek 扩展查询文本
     */
    private String generateExpandedQuery(String query, String promptTemplate, double temperature, int maxTokens) 
    {
        if (StringUtils.isEmpty(deepseekApiKey)) 
        {
            return query;
        }
        
        String content = callDeepSeekAPI(query, promptTemplate, temperature, maxTokens);
        
        if (content != null && !content.isEmpty()) 
        {
            try 
            {
                String[] keywords = content.split("[,，、\\s]+");
                String joined = Arrays.stream(keywords)
                        .map(String::trim)
                        .filter(s -> StringUtils.isNotEmpty(s))
                        .collect(Collectors.joining(" "));
                return StringUtils.isNotEmpty(joined) ? joined : query;
            } 
            catch (Exception e) 
            {
                log.error("处理 DeepSeek 响应失败: {}", e.getMessage(), e);
            }
        }
        
        return query;
    }

    /**
     * 纯向量检索并结合热度打分
     */
    private List<Post> searchWithVector(float[] queryVector, int limit) 
    {
        if (queryVector == null || queryVector.length == 0) 
        {
            return Collections.emptyList();
        }

        List<SearchResult> vectorResults = vectorDBService.search(queryVector, Math.max(limit * 2, 20));
        if (vectorResults.isEmpty()) 
        {
            return Collections.emptyList();
        }

        List<Long> postIds = vectorResults.stream()
            .map(SearchResult::getPostId)
            .collect(Collectors.toList());

        List<Post> posts = new ArrayList<Post>();
        for (Long postId : postIds) 
        {
            Post post = postMapper.selectPostById(postId);
            if (post != null) 
            {
                posts.add(post);
            }
        }

        if (posts.isEmpty()) 
        {
            return Collections.emptyList();
        }

        Map<Long, Float> similarityMap = new HashMap<Long, Float>();
        for (SearchResult result : vectorResults) 
        {
            similarityMap.put(result.getPostId(), result.getScore());
        }

        return posts.stream()
            .map(post -> {
                double popularityScore = calculatePopularityScore(post);
                Float similarity = similarityMap.get(post.getId());
                double finalScore = (similarity != null ? similarity : 0.0f) * 0.7 + popularityScore * 0.3;
                post.setScore(finalScore);
                return post;
            })
            .sorted((a, b) -> Double.compare(
                b.getScore() != null ? b.getScore() : 0.0,
                a.getScore() != null ? a.getScore() : 0.0
            ))
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * 合并多个搜索结果，去重并按分数排序
     */
    private List<Post> mergeResults(List<Post> directResults, List<Post> expandedResults, 
                                    List<Post> vectorResults, int limit) 
    {
        Map<Long, Post> postMap = new HashMap<Long, Post>();
        
        for (Post post : directResults) 
        {
            postMap.put(post.getId(), post);
        }
        
        for (Post post : expandedResults) 
        {
            postMap.putIfAbsent(post.getId(), post);
        }
        
        for (Post post : vectorResults) 
        {
            postMap.putIfAbsent(post.getId(), post);
        }
        
        return postMap.values().stream()
            .sorted((a, b) -> Double.compare(
                b.getScore() != null ? b.getScore() : 0.0,
                a.getScore() != null ? a.getScore() : 0.0
            ))
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * 计算帖子热度分数
     */
    private double calculatePopularityScore(Post post) 
    {
        int likes = post.getLikeCount() != null ? post.getLikeCount() : 0;
        int comments = post.getCommentCount() != null ? post.getCommentCount() : 0;
        double normalizedLikes = Math.log1p(likes) / Math.log(100);
        double normalizedComments = Math.log1p(comments) / Math.log(50);
        return normalizedLikes * likeWeight + normalizedComments * commentWeight;
    }

    /**
     * 对帖子列表进行评分、排序和限制
     */
    private List<Post> rankAndLimitPosts(List<Post> posts, int limit) 
    {
        if (posts == null || posts.isEmpty()) 
        {
            return Collections.emptyList();
        }
        
        return posts.stream()
            .peek(post -> {
                double score = calculatePopularityScore(post);
                post.setScore(score);
            })
            .sorted((a, b) -> Double.compare(
                b.getScore() != null ? b.getScore() : 0.0,
                a.getScore() != null ? a.getScore() : 0.0
            ))
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> parseTitleWithDeepSeek(String title) 
    {
        if (StringUtils.isEmpty(title)) 
        {
            return null;
        }

        // 1. 先查 Redis 缓存
        String cacheKey = TITLE_PARSE_CACHE_KEY + title;
        try 
        {
            Map<String, String> cachedResult = redisCache.getCacheObject(cacheKey);
            if (cachedResult != null && !cachedResult.isEmpty()) 
            {
                log.debug("标题解析缓存命中: {}", title);
                return cachedResult;
            }
        } 
        catch (Exception e) 
        {
            log.warn("读取标题解析缓存失败: {}", e.getMessage());
        }

        // 2. 缓存未命中，如果 DeepSeek API Key 未配置，直接返回 null
        if (StringUtils.isEmpty(deepseekApiKey)) 
        {
            return null;
        }

        // 3. 调用 DeepSeek API
        Map<String, String> result = null;
        try 
        {
            String response = callDeepSeekAPI(title, titleParsePromptTemplate, 0.3, 100);
            
            if (StringUtils.isEmpty(response)) 
            {
                return null;
            }

            // 解析 DeepSeek 返回的结果
            result = new HashMap<String, String>();
            String[] lines = response.split("\n");
            
            for (String line : lines) 
            {
                line = line.trim();
                if (line.startsWith("主标题：")) 
                {
                    String mainTitle = line.substring("主标题：".length()).trim();
                    if (StringUtils.isNotEmpty(mainTitle)) 
                    {
                        result.put("mainTitle", mainTitle);
                    }
                } 
                else if (line.startsWith("主标题:")) 
                {
                    String mainTitle = line.substring("主标题:".length()).trim();
                    if (StringUtils.isNotEmpty(mainTitle)) 
                    {
                        result.put("mainTitle", mainTitle);
                    }
                }
                else if (line.startsWith("副标题：")) 
                {
                    String subTitle = line.substring("副标题：".length()).trim();
                    if (StringUtils.isNotEmpty(subTitle)) 
                    {
                        result.put("subTitle", subTitle);
                    }
                }
                else if (line.startsWith("副标题:")) 
                {
                    String subTitle = line.substring("副标题:".length()).trim();
                    if (StringUtils.isNotEmpty(subTitle)) 
                    {
                        result.put("subTitle", subTitle);
                    }
                }
            }

            // 如果识别到了主标题，缓存结果并返回
            if (result.containsKey("mainTitle")) 
            {
                try 
                {
                    // 将结果缓存到 Redis，过期时间 7 天
                    redisCache.setCacheObject(cacheKey, result, TITLE_PARSE_CACHE_EXPIRE_DAYS, TimeUnit.DAYS);
                    log.debug("标题解析结果已缓存: {}", title);
                } 
                catch (Exception e) 
                {
                    log.warn("缓存标题解析结果失败: {}", e.getMessage());
                }
                return result;
            }
        } 
        catch (Exception e) 
        {
            log.warn("DeepSeek 标题解析失败: {}", e.getMessage());
        }
        
        return null;
    }

    @Override
    public Map<String, Map<String, String>> batchParseTitleWithDeepSeek(List<String> titles) 
    {
        Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
        
        if (titles == null || titles.isEmpty()) 
        {
            return result;
        }
        
        // 如果 Redis 不可用，直接返回空结果，避免异常
        if (redisCache == null) 
        {
            log.warn("RedisCache 未初始化，跳过批量查询");
            return result;
        }

        // 1. 批量构建缓存 key
        List<String> cacheKeys = new ArrayList<String>();
        Map<String, String> titleToKeyMap = new HashMap<String, String>();
        for (String title : titles) 
        {
            if (StringUtils.isEmpty(title)) 
            {
                continue;
            }
            String cacheKey = TITLE_PARSE_CACHE_KEY + title;
            cacheKeys.add(cacheKey);
            titleToKeyMap.put(cacheKey, title);
        }

        if (cacheKeys.isEmpty()) 
        {
            return result;
        }

        // 2. 批量查询 Redis 缓存（使用 MGET 优化）
        Map<String, Map<String, String>> cachedResults = new HashMap<String, Map<String, String>>();
        List<String> missingTitles = new ArrayList<String>();
        
        try 
        {
            List<Map<String, String>> cachedValues = redisCache.getMultiCacheObject(cacheKeys);
            
            // 处理批量查询结果
            if (cachedValues != null) 
            {
                for (int i = 0; i < cacheKeys.size(); i++) 
                {
                    String cacheKey = cacheKeys.get(i);
                    String title = titleToKeyMap.get(cacheKey);
                    
                    if (i < cachedValues.size()) 
                    {
                        Map<String, String> cachedValue = cachedValues.get(i);
                        
                        if (cachedValue != null && !cachedValue.isEmpty() && cachedValue.containsKey("mainTitle")) 
                        {
                            // 缓存命中
                            cachedResults.put(title, cachedValue);
                        } 
                        else 
                        {
                            // 缓存未命中，需要调用 DeepSeek
                            missingTitles.add(title);
                        }
                    } 
                    else 
                    {
                        // 索引超出范围，缓存未命中
                        missingTitles.add(title);
                    }
                }
            } 
            else 
            {
                // 批量查询返回 null，所有标题都未命中
                missingTitles.addAll(titles);
            }
        } 
        catch (Exception e) 
        {
            log.warn("批量读取标题解析缓存失败: {}", e.getMessage(), e);
            // 如果批量查询失败，将所有标题标记为未命中
            missingTitles.addAll(titles);
        }

        // 3. 将缓存命中的结果加入返回结果
        result.putAll(cachedResults);

        // 4. 对于缓存未命中的标题，使用冒号检测快速返回，并异步调用 DeepSeek 更新缓存
        for (String title : missingTitles) 
        {
            // 先使用冒号检测作为快速降级方案
            Map<String, String> fallbackResult = parseTitleWithColon(title);
            if (fallbackResult != null) 
            {
                result.put(title, fallbackResult);
            }

            // 异步调用 DeepSeek 更新缓存（不阻塞）
            if (StringUtils.isNotEmpty(deepseekApiKey)) 
            {
                final String finalTitle = title;
                new Thread(() -> {
                    try 
                    {
                        parseTitleWithDeepSeek(finalTitle);
                    } 
                    catch (Exception e) 
                    {
                        log.debug("异步 DeepSeek 标题解析失败: {}", finalTitle);
                    }
                }).start();
            }
        }

        return result;
    }

    /**
     * 使用冒号检测快速解析标题（降级方案）
     * 
     * @param title 原始标题
     * @return 包含 mainTitle 和 subTitle 的 Map
     */
    private Map<String, String> parseTitleWithColon(String title) 
    {
        if (StringUtils.isEmpty(title)) 
        {
            return null;
        }

        Map<String, String> result = new HashMap<String, String>();
        int colonIndex = -1;
        
        if (title.contains("：")) 
        {
            colonIndex = title.indexOf("：");
        } 
        else if (title.contains(":")) 
        {
            colonIndex = title.indexOf(":");
        }
        
        if (colonIndex > 0 && colonIndex < title.length() - 1) 
        {
            result.put("mainTitle", title.substring(0, colonIndex).trim());
            result.put("subTitle", title.substring(colonIndex + 1).trim());
        } 
        else 
        {
            result.put("mainTitle", title);
        }
        
        return result;
    }

    /**
     * 构建搜索缓存键
     * 格式：search:{mode}:{queryHash}:{limit}
     * 使用 MD5 哈希处理查询词，避免键过长
     * 
     * @param query 搜索查询词
     * @param mode 搜索模式
     * @param limit 返回数量限制
     * @return 缓存键
     */
    private String buildSearchCacheKey(String query, String mode, int limit) 
    {
        String modeLower = mode != null ? mode.toLowerCase() : "hybrid";
        String queryHash = md5Hash(query);
        return SEARCH_CACHE_KEY + modeLower + ":" + queryHash + ":" + limit;
    }

    /**
     * 计算字符串的 MD5 哈希值
     * 
     * @param input 输入字符串
     * @return MD5 哈希值（32位十六进制字符串）
     */
    private String md5Hash(String input) 
    {
        if (StringUtils.isEmpty(input)) 
        {
            return "empty";
        }

        try 
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes("UTF-8"));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) 
            {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } 
        catch (Exception e) 
        {
            log.warn("MD5 哈希计算失败，使用原始字符串: {}", e.getMessage());
            // 降级方案：如果 MD5 失败，使用原始字符串（截断到前50个字符）
            return input.length() > 50 ? input.substring(0, 50) : input;
        }
    }
}

