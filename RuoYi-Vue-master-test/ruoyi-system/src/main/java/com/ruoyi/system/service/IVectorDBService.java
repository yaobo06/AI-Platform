package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SearchResult;

/**
 * 向量数据库服务接口
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public interface IVectorDBService 
{
    /**
     * 检查是否可用
     */
    public boolean isAvailable();

    /**
     * 插入向量
     */
    public void insertVector(Long postId, float[] vector);

    /**
     * 更新向量（先删除再插入）
     */
    public void updateVector(Long postId, float[] vector);

    /**
     * 删除向量
     */
    public void deleteVector(Long postId);

    /**
     * 向量搜索
     * 
     * @param queryVector 查询向量
     * @param topK 返回数量
     * @return 搜索结果（Post ID + 相似度分数），失败时返回空列表
     */
    public List<SearchResult> search(float[] queryVector, int topK);
}

