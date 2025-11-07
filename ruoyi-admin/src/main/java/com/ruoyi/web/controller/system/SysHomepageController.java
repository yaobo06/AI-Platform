package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysHomepage;
import com.ruoyi.system.service.ISysHomepageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 主页信息Controller
 * 
 * @author ruoyi
 * @date 2025-06-05
 */
@RestController
@RequestMapping("/system/homepage")
public class SysHomepageController extends BaseController
{
    @Autowired
    private ISysHomepageService sysHomepageService;

    /**
     * 查询主页信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:homepage:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysHomepage sysHomepage)
    {
        startPage();
        List<SysHomepage> list = sysHomepageService.selectSysHomepageList(sysHomepage);
        return getDataTable(list);
    }

    /**
     * 导出主页信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:homepage:export')")
    @Log(title = "主页信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysHomepage sysHomepage)
    {
        List<SysHomepage> list = sysHomepageService.selectSysHomepageList(sysHomepage);
        ExcelUtil<SysHomepage> util = new ExcelUtil<SysHomepage>(SysHomepage.class);
        util.exportExcel(response, list, "主页信息数据");
    }

    /**
     * 获取主页信息详细信息
     */
    /*@PreAuthorize("@ss.hasPermi('system:homepage:query')")*/
    @GetMapping(value = "/getIntroductionInfo")
    public AjaxResult getInfo(SysHomepage syshomepage)
    {
        return success(sysHomepageService.selectSysHomepageById(syshomepage.getId()));
    }

    /**
     * 新增主页信息
     */
    @PreAuthorize("@ss.hasPermi('system:homepage:add')")
    @Log(title = "主页信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysHomepage sysHomepage)
    {
        return toAjax(sysHomepageService.insertSysHomepage(sysHomepage));
    }

    /**
     * 修改主页信息
     */
    @PreAuthorize("@ss.hasPermi('system:homepage:edit')")
    @Log(title = "主页信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysHomepage sysHomepage)
    {
        return toAjax(sysHomepageService.updateSysHomepage(sysHomepage));
    }

    /**
     * 删除主页信息
     */
    @PreAuthorize("@ss.hasPermi('system:homepage:remove')")
    @Log(title = "主页信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysHomepageService.deleteSysHomepageByIds(ids));
    }
}
