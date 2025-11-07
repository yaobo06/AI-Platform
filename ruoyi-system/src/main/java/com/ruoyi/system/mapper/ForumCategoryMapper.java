package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.ForumCategory;

/**
 * 论坛分类Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public interface ForumCategoryMapper 
{
    /**
     * 查询论坛分类
     * 
     * @param categoryId 论坛分类主键
     * @return 论坛分类
     */
    public ForumCategory selectForumCategoryByCategoryId(Long categoryId);

    /**
     * 查询论坛分类列表
     * 
     * @param forumCategory 论坛分类
     * @return 论坛分类集合
     */
    public List<ForumCategory> selectForumCategoryList(ForumCategory forumCategory);

    /**
     * 新增论坛分类
     * 
     * @param forumCategory 论坛分类
     * @return 结果
     */
    public int insertForumCategory(ForumCategory forumCategory);

    /**
     * 修改论坛分类
     * 
     * @param forumCategory 论坛分类
     * @return 结果
     */
    public int updateForumCategory(ForumCategory forumCategory);

    /**
     * 删除论坛分类
     * 
     * @param categoryId 论坛分类主键
     * @return 结果
     */
    public int deleteForumCategoryByCategoryId(Long categoryId);

    /**
     * 批量删除论坛分类
     * 
     * @param categoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteForumCategoryByCategoryIds(Long[] categoryIds);
}
