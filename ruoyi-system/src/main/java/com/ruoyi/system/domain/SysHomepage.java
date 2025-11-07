package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 主页信息对象 sys_homepage
 * 
 * @author ruoyi
 * @date 2025-06-05
 */
public class SysHomepage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /**  */
    @Excel(name = "主页标题")
    private String homepageTitle;

    /**  */
    @Excel(name = "主页通告")
    private String homepageNotice;

    /**  */
    @Excel(name = "图像url")
    private String homepageImg;

    /**  */
    @Excel(name = "状态")
    private String status;

    /**  */
    private String delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setHomepageTitle(String homepageTitle) 
    {
        this.homepageTitle = homepageTitle;
    }

    public String getHomepageTitle() 
    {
        return homepageTitle;
    }

    public void setHomepageNotice(String homepageNotice) 
    {
        this.homepageNotice = homepageNotice;
    }

    public String getHomepageNotice() 
    {
        return homepageNotice;
    }

    public void setHomepageImg(String homepageImg) 
    {
        this.homepageImg = homepageImg;
    }

    public String getHomepageImg() 
    {
        return homepageImg;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("homepageTitle", getHomepageTitle())
            .append("homepageNotice", getHomepageNotice())
            .append("homepageImg", getHomepageImg())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
