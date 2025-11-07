package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * 论坛帖子对象 forum_post
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public class ForumPost extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 帖子ID */
    private Long postId;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 发帖用户ID */
    @Excel(name = "发帖用户ID")
    private Long userId;

    /** 帖子标题 */
    @Excel(name = "帖子标题")
    private String title;

    /** 帖子内容 */
    private String content;

    /** 内容类型（1文本 2富文本 3Markdown） */
    @Excel(name = "内容类型", readConverterExp = "1=文本,2=富文本,3=Markdown")
    private String contentType;

    /** 浏览次数 */
    @Excel(name = "浏览次数")
    private Long viewCount;

    /** 回复次数 */
    @Excel(name = "回复次数")
    private Long replyCount;

    /** 点赞次数 */
    @Excel(name = "点赞次数")
    private Long likeCount;

    /** 是否置顶（0否 1是） */
    @Excel(name = "是否置顶", readConverterExp = "0=否,1=是")
    private String isTop;

    /** 是否精华（0否 1是） */
    @Excel(name = "是否精华", readConverterExp = "0=否,1=是")
    private String isEssence;

    /** 状态（0正常 1审核中 2已删除） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=审核中,2=已删除")
    private String status;

    /** 最后回复时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后回复时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastReplyTime;

    /** 最后回复用户ID */
    @Excel(name = "最后回复用户ID")
    private Long lastReplyUserId;

    /** 分类名称（非数据库字段） */
    private String categoryName;

    /** 发帖用户名称（非数据库字段） */
    private String userName;

    /** 最后回复用户名称（非数据库字段） */
    private String lastReplyUserName;

    /** 标签列表（非数据库字段） */
    private List<ForumTag> tags;

    /** 是否已点赞（非数据库字段） */
    private Boolean isLiked;

    /** 是否已收藏（非数据库字段） */
    private Boolean isFavorited;

    public void setPostId(Long postId) 
    {
        this.postId = postId;
    }

    public Long getPostId() 
    {
        return postId;
    }
    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    public void setContentType(String contentType) 
    {
        this.contentType = contentType;
    }

    public String getContentType() 
    {
        return contentType;
    }
    public void setViewCount(Long viewCount) 
    {
        this.viewCount = viewCount;
    }

    public Long getViewCount() 
    {
        return viewCount;
    }
    public void setReplyCount(Long replyCount) 
    {
        this.replyCount = replyCount;
    }

    public Long getReplyCount() 
    {
        return replyCount;
    }
    public void setLikeCount(Long likeCount) 
    {
        this.likeCount = likeCount;
    }

    public Long getLikeCount() 
    {
        return likeCount;
    }
    public void setIsTop(String isTop) 
    {
        this.isTop = isTop;
    }

    public String getIsTop() 
    {
        return isTop;
    }
    public void setIsEssence(String isEssence) 
    {
        this.isEssence = isEssence;
    }

    public String getIsEssence() 
    {
        return isEssence;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setLastReplyTime(Date lastReplyTime) 
    {
        this.lastReplyTime = lastReplyTime;
    }

    public Date getLastReplyTime() 
    {
        return lastReplyTime;
    }
    public void setLastReplyUserId(Long lastReplyUserId) 
    {
        this.lastReplyUserId = lastReplyUserId;
    }

    public Long getLastReplyUserId() 
    {
        return lastReplyUserId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastReplyUserName() {
        return lastReplyUserName;
    }

    public void setLastReplyUserName(String lastReplyUserName) {
        this.lastReplyUserName = lastReplyUserName;
    }

    public List<ForumTag> getTags() {
        return tags;
    }

    public void setTags(List<ForumTag> tags) {
        this.tags = tags;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }

    public Boolean getIsFavorited() {
        return isFavorited;
    }

    public void setIsFavorited(Boolean isFavorited) {
        this.isFavorited = isFavorited;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("postId", getPostId())
            .append("categoryId", getCategoryId())
            .append("userId", getUserId())
            .append("title", getTitle())
            .append("content", getContent())
            .append("contentType", getContentType())
            .append("viewCount", getViewCount())
            .append("replyCount", getReplyCount())
            .append("likeCount", getLikeCount())
            .append("isTop", getIsTop())
            .append("isEssence", getIsEssence())
            .append("status", getStatus())
            .append("lastReplyTime", getLastReplyTime())
            .append("lastReplyUserId", getLastReplyUserId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
