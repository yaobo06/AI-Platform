package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.Post;

/**
 * 搜索服务接口
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public interface ISearchService 
{
    /**
     * 搜索帖子
     * 
     * @param query 搜索关键词
     * @param mode 搜索模式: 
     *             "db" - 数据库搜索
     *             "deepseek" - DeepSeek API智能搜索（提取关键词后数据库搜索）
     *             "rag" - RAG向量检索
     *             "hybrid" - 混合模式（优先DeepSeek，然后RAG，最后合并结果）
     * @param limit 返回数量
     * @return 帖子列表
     */
    public List<Post> search(String query, String mode, int limit);

    /**
     * 调用 DeepSeek API 识别标题的主标题和副标题
     * 
     * @param title 原始标题
     * @return 包含 mainTitle 和 subTitle 的 Map，如果识别失败则返回 null
     */
    public Map<String, String> parseTitleWithDeepSeek(String title);

    /**
     * 批量解析标题（使用批量 Redis 查询优化性能）
     * 
     * @param titles 标题列表
     * @return 标题到解析结果的映射（key: 原始标题, value: 包含 mainTitle 和 subTitle 的 Map）
     */
    public Map<String, Map<String, String>> batchParseTitleWithDeepSeek(List<String> titles);
}

