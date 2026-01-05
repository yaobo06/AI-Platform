package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 帖子标签关联对象 forum_post_tag
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public class ForumPostTag
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 帖子ID */
    private Long postId;

    /** 标签ID */
    private Long tagId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setPostId(Long postId) 
    {
        this.postId = postId;
    }

    public Long getPostId() 
    {
        return postId;
    }
    public void setTagId(Long tagId) 
    {
        this.tagId = tagId;
    }

    public Long getTagId() 
    {
        return tagId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("postId", getPostId())
            .append("tagId", getTagId())
            .toString();
    }
}
