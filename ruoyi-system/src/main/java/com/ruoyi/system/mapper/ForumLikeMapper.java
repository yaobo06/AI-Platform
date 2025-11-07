package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.ForumLike;

/**
 * 论坛点赞Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public interface ForumLikeMapper 
{
    /**
     * 查询论坛点赞
     * 
     * @param likeId 论坛点赞主键
     * @return 论坛点赞
     */
    public ForumLike selectForumLikeByLikeId(Long likeId);

    /**
     * 查询论坛点赞列表
     * 
     * @param forumLike 论坛点赞
     * @return 论坛点赞集合
     */
    public List<ForumLike> selectForumLikeList(ForumLike forumLike);

    /**
     * 查询用户是否点赞
     * 
     * @param userId 用户ID
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 论坛点赞
     */
    public ForumLike selectForumLikeByUserAndTarget(Long userId, String targetType, Long targetId);

    /**
     * 新增论坛点赞
     * 
     * @param forumLike 论坛点赞
     * @return 结果
     */
    public int insertForumLike(ForumLike forumLike);

    /**
     * 修改论坛点赞
     * 
     * @param forumLike 论坛点赞
     * @return 结果
     */
    public int updateForumLike(ForumLike forumLike);

    /**
     * 删除论坛点赞
     * 
     * @param likeId 论坛点赞主键
     * @return 结果
     */
    public int deleteForumLikeByLikeId(Long likeId);

    /**
     * 批量删除论坛点赞
     * 
     * @param likeIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteForumLikeByLikeIds(Long[] likeIds);

    /**
     * 删除用户点赞
     * 
     * @param userId 用户ID
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 结果
     */
    public int deleteForumLikeByUserAndTarget(Long userId, String targetType, Long targetId);
}
