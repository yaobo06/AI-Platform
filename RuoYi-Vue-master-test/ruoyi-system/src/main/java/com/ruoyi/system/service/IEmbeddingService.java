package com.ruoyi.system.service;

/**
 * 向量生成服务接口
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public interface IEmbeddingService 
{
    /**
     * 生成文本向量
     * 
     * @param text 输入文本
     * @return 向量数组（float[]），失败时返回 null
     */
    public float[] generateEmbedding(String text);

    /**
     * 生成文本向量（带异常控制）
     * 
     * @param text 输入文本
     * @param throwException 是否抛出异常（false 时失败返回 null）
     * @return 向量数组（float[]），失败时返回 null 或抛出异常
     */
    public float[] generateEmbedding(String text, boolean throwException);

    /**
     * 获取向量维度
     */
    public int getDimension();
}

