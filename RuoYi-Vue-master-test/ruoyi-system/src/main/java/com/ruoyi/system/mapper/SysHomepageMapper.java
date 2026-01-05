package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysHomepage;

/**
 * 主页信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-05
 */
public interface SysHomepageMapper 
{
    /**
     * 查询主页信息
     * 
     * @param id 主页信息主键
     * @return 主页信息
     */
    public SysHomepage selectSysHomepageById(Long id);

    /**
     * 查询主页信息列表
     * 
     * @param sysHomepage 主页信息
     * @return 主页信息集合
     */
    public List<SysHomepage> selectSysHomepageList(SysHomepage sysHomepage);

    /**
     * 新增主页信息
     * 
     * @param sysHomepage 主页信息
     * @return 结果
     */
    public int insertSysHomepage(SysHomepage sysHomepage);

    /**
     * 修改主页信息
     * 
     * @param sysHomepage 主页信息
     * @return 结果
     */
    public int updateSysHomepage(SysHomepage sysHomepage);

    /**
     * 删除主页信息
     * 
     * @param id 主页信息主键
     * @return 结果
     */
    public int deleteSysHomepageById(Long id);

    /**
     * 批量删除主页信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysHomepageByIds(Long[] ids);
}
