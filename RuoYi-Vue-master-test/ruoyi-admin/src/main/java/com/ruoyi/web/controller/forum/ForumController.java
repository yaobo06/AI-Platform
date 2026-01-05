package com.ruoyi.web.controller.forum;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.code.kaptcha.Producer;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.utils.sign.Base64;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.domain.Post;
import com.ruoyi.system.mapper.CommentMapper;
import com.ruoyi.system.mapper.PostMapper;
import com.ruoyi.system.service.ISysConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 论坛通用Controller (来自 Mobile Terminal，提供分类和统计功能)
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@RestController
@RequestMapping("/forum")
public class ForumController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(ForumController.class);
    
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private com.ruoyi.system.service.IPostService postService;

    @Autowired
    private com.ruoyi.system.service.ISearchService searchService;

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private RedisCache redisCache;
    
    @Autowired
    private ISysConfigService configService;

    /**
     * 获取论坛分类列表（从帖子中提取）
     */
    @Anonymous
    @GetMapping("/categories")
    public AjaxResult getCategories() 
    {
        // 从所有帖子中提取分类
        List<Post> allPosts = postMapper.selectPostList(new Post());
        Set<String> categorySet = new HashSet<String>();
        
        for (Post post : allPosts) {
            if (post.getCategory() != null && !post.getCategory().trim().isEmpty()) {
                categorySet.add(post.getCategory());
            }
        }
        
        // 转换为列表格式（兼容前端期望的格式）
        List<Map<String, Object>> categories = new ArrayList<Map<String, Object>>();
        for (String category : categorySet) {
            Map<String, Object> cat = new HashMap<String, Object>();
            cat.put("categoryId", category);
            cat.put("categoryName", category);
            cat.put("id", category);
            cat.put("name", category);
            cat.put("status", "0"); // 默认启用
            cat.put("description", "");
            categories.add(cat);
        }
        
        return success().put("data", categories);
    }

    /**
     * 获取论坛统计信息
     */
    @Anonymous
    @GetMapping("/stats")
    public AjaxResult getStats() 
    {
        Map<String, Object> stats = new HashMap<String, Object>();
        
        // 使用 SQL COUNT(*) 查询帖子总数
        long totalPosts = postMapper.countPosts();
        
        // 使用 SQL COUNT(*) 查询评论总数
        long totalComments = commentMapper.countAllComments();
        
        // 使用 SQL COUNT(DISTINCT user_id) 查询发帖用户数（从 posts 表）
        long totalUsers = postMapper.countDistinctUsers();
        
        stats.put("totalPosts", (int) totalPosts);
        stats.put("totalComments", (int) totalComments);
        stats.put("totalUsers", (int) totalUsers);
        
        return success().put("data", stats);
    }

    /**
     * 获取大家都在看的帖子（热门帖子，按点赞和评论加权计算）
     */
    @Anonymous
    @GetMapping("/trending")
    public AjaxResult getTrending(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "5") int size) 
    {
        try 
        {
            PageInfo<Post> pageInfo = postService.listTrending(page, size);
            
            // 转换为前端需要的格式（只返回作者和帖子名称，处理标题格式）
            // 优化：使用批量 Redis 查询（MGET）减少网络往返
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            
            // 1. 收集所有标题
            List<String> titles = new ArrayList<String>();
            for (Post post : pageInfo.getList()) 
            {
                if (post != null && post.getTitle() != null) 
                {
                    titles.add(post.getTitle());
                }
            }
            
            // 2. 批量查询缓存（一次 Redis MGET 查询）
            Map<String, Map<String, String>> titleParseResults = new HashMap<String, Map<String, String>>();
            if (!titles.isEmpty()) 
            {
                try 
                {
                    titleParseResults = searchService.batchParseTitleWithDeepSeek(titles);
                } 
                catch (Exception e) 
                {
                    logger.error("批量解析标题失败: {}", e.getMessage(), e);
                    // 如果批量解析失败，继续使用原始标题
                }
            }
            
            // 3. 构建返回结果
            for (Post post : pageInfo.getList()) 
            {
                if (post == null) 
                {
                    continue;
                }
                
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("id", post.getId());
                
                String title = post.getTitle() != null ? post.getTitle() : "";
                String mainTitle = title;
                String subTitle = null;
                
                // 从批量查询结果中获取
                if (titleParseResults != null) 
                {
                    Map<String, String> parseResult = titleParseResults.get(title);
                    if (parseResult != null && parseResult.containsKey("mainTitle")) 
                    {
                        mainTitle = parseResult.get("mainTitle");
                        subTitle = parseResult.get("subTitle");
                    }
                }
                
                item.put("title", title);
                item.put("mainTitle", mainTitle);
                item.put("subTitle", subTitle);
                item.put("authorName", post.getAuthorName() != null ? post.getAuthorName() : "匿名用户");
                result.add(item);
            }
            
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("records", result);
            data.put("total", pageInfo.getTotal());
            data.put("current", pageInfo.getPageNum());
            data.put("size", pageInfo.getPageSize());
            data.put("pages", pageInfo.getPages());
            
            return success().put("data", data);
        } 
        catch (Exception e) 
        {
            logger.error("获取热门帖子失败: {}", e.getMessage(), e);
            return error("获取热门帖子失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门标签
     */
    @Anonymous
    @GetMapping("/hot-tags")
    public AjaxResult getHotTags(@RequestParam(defaultValue = "10") int limit) 
    {
        List<Map<String, Object>> hotTags = postMapper.selectHotTags(limit);
        
        // 转换为前端需要的格式
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        String[] tagTypes = {"", "success", "warning", "info", "danger"};
        int typeIndex = 0;
        
        for (Map<String, Object> tag : hotTags) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("name", tag.get("name"));
            item.put("count", tag.get("count"));
            item.put("type", tagTypes[typeIndex % tagTypes.length]);
            result.add(item);
            typeIndex++;
        }
        
        return success().put("data", result);
    }

    /**
     * 生成验证码（论坛专用）
     */
    @Anonymous
    @GetMapping("/captchaImage")
    public AjaxResult getCaptchaImage() throws IOException
    {
        AjaxResult ajax = AjaxResult.success();
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        ajax.put("captchaEnabled", captchaEnabled);
        if (!captchaEnabled)
        {
            return ajax;
        }

        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;

        String capStr = null, code = null;
        BufferedImage image = null;

        // 生成验证码
        String captchaType = RuoYiConfig.getCaptchaType();
        if ("math".equals(captchaType))
        {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        }
        else if ("char".equals(captchaType))
        {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try
        {
            ImageIO.write(image, "jpg", os);
        }
        catch (IOException e)
        {
            return AjaxResult.error(e.getMessage());
        }

        ajax.put("uuid", uuid);
        ajax.put("img", Base64.encode(os.toByteArray()));
        return ajax;
    }
}

