package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.AiModelsMapper;
import com.ruoyi.system.domain.AiModels;
import com.ruoyi.system.service.IAiModelsService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-11
 */
@Service
public class AiModelsServiceImpl implements IAiModelsService
{
    @Autowired
    private AiModelsMapper aiModelsMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public AiModels selectAiModelsById(Long id)
    {
        return aiModelsMapper.selectAiModelsById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param aiModels 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<AiModels> selectAiModelsList(AiModels aiModels)
    {
        return aiModelsMapper.selectAiModelsList(aiModels);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param aiModels 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertAiModels(AiModels aiModels)
    {
        return aiModelsMapper.insertAiModels(aiModels);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param aiModels 【请填写功能名称】
     * @author yb
     * @return 结果
     */
    @Override
    public int updateAiModels(AiModels aiModels)
    {
        aiModels.setUpdateTime(DateUtils.getNowDate());
        return aiModelsMapper.updateAiModels(aiModels);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteAiModelsByIds(Long[] ids)
    {
        return aiModelsMapper.deleteAiModelsByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteAiModelsById(Long id)
    {
        return aiModelsMapper.deleteAiModelsById(id);
    }
}
