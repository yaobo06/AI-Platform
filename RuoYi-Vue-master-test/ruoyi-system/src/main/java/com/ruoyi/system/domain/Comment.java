package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

/**
 * 评论对象 comments (来自 Mobile Terminal)
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public class Comment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评论ID */
    private Long id;

    /** 帖子ID */
    @Excel(name = "帖子ID")
    private Long postId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 父评论ID */
    @Excel(name = "父评论ID")
    private Long parentId;

    /** 内容 */
    private String content;

    /** 附件URL */
    private String attachmentUrl;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /** 作者名称（非数据库字段） */
    private String authorName;

    /** 作者头像URL（非数据库字段） */
    private String authorAvatarUrl;

    /** 父评论信息（非数据库字段） */
    private Comment parentComment;

    /** 是否有回复（非数据库字段） */
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Boolean hasReplies;

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

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setAttachmentUrl(String attachmentUrl) 
    {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentUrl() 
    {
        return attachmentUrl;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    public String getAuthorName() 
    {
        return authorName;
    }

    public void setAuthorName(String authorName) 
    {
        this.authorName = authorName;
    }

    public String getAuthorAvatarUrl() 
    {
        return authorAvatarUrl;
    }

    public void setAuthorAvatarUrl(String authorAvatarUrl) 
    {
        this.authorAvatarUrl = authorAvatarUrl;
    }

    public Comment getParentComment() 
    {
        return parentComment;
    }

    public void setParentComment(Comment parentComment) 
    {
        this.parentComment = parentComment;
    }

    public Boolean getHasReplies() 
    {
        return hasReplies;
    }

    public void setHasReplies(Boolean hasReplies) 
    {
        this.hasReplies = hasReplies;
    }

    @Override
    public String toString() 
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("postId", getPostId())
            .append("userId", getUserId())
            .append("parentId", getParentId())
            .append("content", getContent())
            .append("attachmentUrl", getAttachmentUrl())
            .append("createdAt", getCreatedAt())
            .toString();
    }
}

