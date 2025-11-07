package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 论坛回复对象 forum_reply
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public class ForumReply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 回复ID */
    private Long replyId;

    /** 帖子ID */
    @Excel(name = "帖子ID")
    private Long postId;

    /** 回复用户ID */
    @Excel(name = "回复用户ID")
    private Long userId;

    /** 父回复ID（0表示直接回复帖子） */
    @Excel(name = "父回复ID")
    private Long parentId;

    /** 回复内容 */
    private String content;

    /** 点赞次数 */
    @Excel(name = "点赞次数")
    private Long likeCount;

    /** 状态（0正常 1审核中 2已删除） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=审核中,2=已删除")
    private String status;

    /** 回复用户名称（非数据库字段） */
    private String userName;

    /** 是否已点赞（非数据库字段） */
    private Boolean isLiked;

    /** 子回复列表（非数据库字段） */
    private List<ForumReply> children;

    public void setReplyId(Long replyId) 
    {
        this.replyId = replyId;
    }

    public Long getReplyId() 
    {
        return replyId;
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
    public void setLikeCount(Long likeCount) 
    {
        this.likeCount = likeCount;
    }

    public Long getLikeCount() 
    {
        return likeCount;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }

    public List<ForumReply> getChildren() {
        return children;
    }

    public void setChildren(List<ForumReply> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("replyId", getReplyId())
            .append("postId", getPostId())
            .append("userId", getUserId())
            .append("parentId", getParentId())
            .append("content", getContent())
            .append("likeCount", getLikeCount())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
