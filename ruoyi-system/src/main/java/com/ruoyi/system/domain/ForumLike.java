package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 论坛点赞对象 forum_like
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public class ForumLike extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 点赞ID */
    private Long likeId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 目标类型（1帖子 2回复） */
    @Excel(name = "目标类型", readConverterExp = "1=帖子,2=回复")
    private String targetType;

    /** 目标ID */
    @Excel(name = "目标ID")
    private Long targetId;

    public void setLikeId(Long likeId) 
    {
        this.likeId = likeId;
    }

    public Long getLikeId() 
    {
        return likeId;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setTargetType(String targetType) 
    {
        this.targetType = targetType;
    }

    public String getTargetType() 
    {
        return targetType;
    }
    public void setTargetId(Long targetId) 
    {
        this.targetId = targetId;
    }

    public Long getTargetId() 
    {
        return targetId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("likeId", getLikeId())
            .append("userId", getUserId())
            .append("targetType", getTargetType())
            .append("targetId", getTargetId())
            .append("createTime", getCreateTime())
            .toString();
    }
}
