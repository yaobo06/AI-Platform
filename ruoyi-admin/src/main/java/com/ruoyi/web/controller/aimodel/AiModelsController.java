package com.ruoyi.web.controller.aimodel;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Excel;
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
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.AiModels;
import com.ruoyi.system.service.IAiModelsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * AIModelController
 * 
 * @author ruoyi
 * @date 2025-06-11
 */
@RestController//声明接口
/*接口注释*/
@RequestMapping("/ai/models")
public class AiModelsController extends BaseController
{
    @Resource
    /*注入Java bean*/
    private IAiModelsService aiModelsService;//service 类的对象

    /**
     * 查询列表（分页） SQL语句
     */
/*    @PreAuthorize("@ss.hasPermi('ai:models:list')")*/
    @GetMapping("/list")
    public TableDataInfo list(AiModels aiModels)
    {
        startPage();// 分页
        List<AiModels> list = aiModelsService.selectAiModelsList(aiModels);
        return getDataTable(list);
    }

    /**
     * 查询所有AI模型列表（前端展示用）
     */
    @Anonymous//匿名访问注释
    @GetMapping("/lists")
    public AjaxResult lists(AiModels aiModels)
    {
        List<AiModels> list = aiModelsService.selectAiModelsList(aiModels);
        return success(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
   /* @PreAuthorize("@ss.hasPermi('ai:models:export')")*/
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiModels aiModels)
    {
        List<AiModels> list = aiModelsService.selectAiModelsList(aiModels);
        ExcelUtil<AiModels> util = new ExcelUtil<AiModels>(AiModels.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取AI模型详细信息
     */
/*    @PreAuthorize("@ss.hasPermi('system:models:query')")*/
    @Anonymous
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aiModelsService.selectAiModelsById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
 /*   @PreAuthorize("@ss.hasPermi('system:models:add')")*/
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiModels aiModels)
    {
        return toAjax(aiModelsService.insertAiModels(aiModels));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:models:edit')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiModels aiModels)
    {
        return toAjax(aiModelsService.updateAiModels(aiModels));
    }

    /**
     * 删除【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:models:remove')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aiModelsService.deleteAiModelsByIds(ids));
    }
}
