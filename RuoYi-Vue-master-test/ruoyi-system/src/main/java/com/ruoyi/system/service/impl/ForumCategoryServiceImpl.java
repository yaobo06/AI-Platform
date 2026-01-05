package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ForumCategoryMapper;
import com.ruoyi.system.domain.ForumCategory;
import com.ruoyi.system.service.IForumCategoryService;

/**
 * 论坛分类Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@Service
public class ForumCategoryServiceImpl implements IForumCategoryService 
{
    @Autowired
    private ForumCategoryMapper forumCategoryMapper;

    /**
     * 查询论坛分类
     * 
     * @param categoryId 论坛分类主键
     * @return 论坛分类
     */
    @Override
    public ForumCategory selectForumCategoryByCategoryId(Long categoryId)
    {
        return forumCategoryMapper.selectForumCategoryByCategoryId(categoryId);
    }

    /**
     * 查询论坛分类列表
     * 
     * @param forumCategory 论坛分类
     * @return 论坛分类
     */
    @Override
    public List<ForumCategory> selectForumCategoryList(ForumCategory forumCategory)
    {
        return forumCategoryMapper.selectForumCategoryList(forumCategory);
    }

    /**
     * 新增论坛分类
     * 
     * @param forumCategory 论坛分类
     * @return 结果
     */
    @Override
    public int insertForumCategory(ForumCategory forumCategory)
    {
        return forumCategoryMapper.insertForumCategory(forumCategory);
    }

    /**
     * 修改论坛分类
     * 
     * @param forumCategory 论坛分类
     * @return 结果
     */
    @Override
    public int updateForumCategory(ForumCategory forumCategory)
    {
        return forumCategoryMapper.updateForumCategory(forumCategory);
    }

    /**
     * 批量删除论坛分类
     * 
     * @param categoryIds 需要删除的论坛分类主键
     * @return 结果
     */
    @Override
    public int deleteForumCategoryByCategoryIds(Long[] categoryIds)
    {
        return forumCategoryMapper.deleteForumCategoryByCategoryIds(categoryIds);
    }

    /**
     * 删除论坛分类信息
     * 
     * @param categoryId 论坛分类主键
     * @return 结果
     */
    @Override
    public int deleteForumCategoryByCategoryId(Long categoryId)
    {
        return forumCategoryMapper.deleteForumCategoryByCategoryId(categoryId);
    }
}
