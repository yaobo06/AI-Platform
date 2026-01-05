package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysHomepageMapper;
import com.ruoyi.system.domain.SysHomepage;
import com.ruoyi.system.service.ISysHomepageService;

/**
 * 主页信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-05
 */
@Service
public class SysHomepageServiceImpl implements ISysHomepageService 
{
    @Autowired
    private SysHomepageMapper sysHomepageMapper;

    /**
     * 查询主页信息
     * 
     * @param id 主页信息主键
     * @return 主页信息
     */
    @Override
    public SysHomepage selectSysHomepageById(Long id)
    {
        return sysHomepageMapper.selectSysHomepageById(id);
    }

    /**
     * 查询主页信息列表
     * 
     * @param sysHomepage 主页信息
     * @return 主页信息
     */
    @Override
    public List<SysHomepage> selectSysHomepageList(SysHomepage sysHomepage)
    {
        return sysHomepageMapper.selectSysHomepageList(sysHomepage);
    }

    /**
     * 新增主页信息
     * 
     * @param sysHomepage 主页信息
     * @return 结果
     */
    @Override
    public int insertSysHomepage(SysHomepage sysHomepage)
    {
        sysHomepage.setCreateTime(DateUtils.getNowDate());
        sysHomepage.setDelFlag("0");
        sysHomepage.setStatus("1");
        return sysHomepageMapper.insertSysHomepage(sysHomepage);
    }

    /**
     * 修改主页信息
     * 
     * @param sysHomepage 主页信息
     * @return 结果
     */
    @Override
    public int updateSysHomepage(SysHomepage sysHomepage)
    {
        sysHomepage.setUpdateTime(DateUtils.getNowDate());
        return sysHomepageMapper.updateSysHomepage(sysHomepage);
    }

    /**
     * 批量删除主页信息
     * 
     * @param ids 需要删除的主页信息主键
     * @return 结果
     */
    @Override
    public int deleteSysHomepageByIds(Long[] ids)
    {
        return sysHomepageMapper.deleteSysHomepageByIds(ids);
    }

    /**
     * 删除主页信息信息
     * 
     * @param id 主页信息主键
     * @return 结果
     */
    @Override
    public int deleteSysHomepageById(Long id)
    {
        return sysHomepageMapper.deleteSysHomepageById(id);
    }
}
