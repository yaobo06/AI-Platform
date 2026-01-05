package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.springframework.stereotype.Component;

/*
 * 对象 ai_models
 * 
 * @author ruoyi
 * @date 2025-06-11
 */

public class AiModels extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String name;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String description;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String imageUrl;

    /** text/image/audio/video/code */
    @Excel(name = "text/image/audio/video/code")
    private String categoryType;

    /** 1:热门 2:新品 3:推荐 */
    @Excel(name = "1:热门 2:新品 3:推荐")
    private Long statusCode;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String version;

    /** 逗号分隔的标签 */
    @Excel(name = "逗号分隔的标签")
    private String tags;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String experienceUrl;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setImageUrl(String imageUrl) 
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() 
    {
        return imageUrl;
    }

    public void setCategoryType(String categoryType) 
    {
        this.categoryType = categoryType;
    }

    public String getCategoryType() 
    {
        return categoryType;
    }

    public void setStatusCode(Long statusCode) 
    {
        this.statusCode = statusCode;
    }

    public Long getStatusCode() 
    {
        return statusCode;
    }

    public void setVersion(String version) 
    {
        this.version = version;
    }

    public String getVersion() 
    {
        return version;
    }

    public void setTags(String tags) 
    {
        this.tags = tags;
    }

    public String getTags() 
    {
        return tags;
    }

    public void setExperienceUrl(String experienceUrl) 
    {
        this.experienceUrl = experienceUrl;
    }

    public String getExperienceUrl() 
    {
        return experienceUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("description", getDescription())
            .append("imageUrl", getImageUrl())
            .append("categoryType", getCategoryType())
            .append("statusCode", getStatusCode())
            .append("updateTime", getUpdateTime())
            .append("version", getVersion())
            .append("tags", getTags())
            .append("experienceUrl", getExperienceUrl())
            .toString();
    }
}
