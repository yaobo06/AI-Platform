package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.Post;

/**
 * 帖子Mapper接口 (来自 Mobile Terminal)
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public interface PostMapper 
{
    /**
     * 查询帖子
     * 
     * @param id 帖子主键
     * @return 帖子
     */
    public Post selectPostById(Long id);

    /**
     * 查询帖子列表
     * 
     * @param post 帖子
     * @return 帖子集合
     */
    public List<Post> selectPostList(Post post);

    /**
     * 新增帖子
     * 
     * @param post 帖子
     * @return 结果
     */
    public int insertPost(Post post);

    /**
     * 修改帖子
     * 
     * @param post 帖子
     * @return 结果
     */
    public int updatePost(Post post);

    /**
     * 删除帖子
     * 
     * @param id 帖子主键
     * @return 结果
     */
    public int deletePostById(Long id);

    /**
     * 批量删除帖子
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePostByIds(Long[] ids);

    /**
     * 批量查询帖子
     * 
     * @param ids 帖子ID集合
     * @return 帖子集合
     */
    public List<Post> selectPostByIds(List<Long> ids);

    /**
     * 增加评论数
     * 
     * @param id 帖子ID
     * @param delta 增量
     * @return 结果
     */
    public int increaseCommentCount(@Param("id") Long id, @Param("delta") Integer delta);

    /**
     * 根据关键词搜索帖子（标题或内容包含关键词）
     * 
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 帖子集合
     */
    public List<Post> selectPostListByKeyword(@Param("keyword") String keyword, @Param("limit") int limit);

    /**
     * 统计帖子总数
     * 
     * @return 帖子总数
     */
    public long countPosts();

    /**
     * 统计发帖用户数（去重）
     * 
     * @return 发帖用户数
     */
    public long countDistinctUsers();

    /**
     * 统计热门标签（从帖子标题和内容中提取关键词，按使用次数排序）
     * 
     * @param limit 限制数量
     * @return 标签列表，包含标签名称和使用次数
     */
    public List<Map<String, Object>> selectHotTags(@Param("limit") int limit);
}

