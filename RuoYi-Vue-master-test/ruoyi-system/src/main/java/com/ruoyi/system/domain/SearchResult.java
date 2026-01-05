package com.ruoyi.system.domain;

/**
 * 向量搜索结果
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public class SearchResult 
{
    private Long postId;
    private Float score;  // 相似度分数（0-1）
    
    public SearchResult(Long postId, Float score) 
    {
        this.postId = postId;
        this.score = score;
    }

    public Long getPostId() 
    {
        return postId;
    }

    public void setPostId(Long postId) 
    {
        this.postId = postId;
    }

    public Float getScore() 
    {
        return score;
    }

    public void setScore(Float score) 
    {
        this.score = score;
    }
}

