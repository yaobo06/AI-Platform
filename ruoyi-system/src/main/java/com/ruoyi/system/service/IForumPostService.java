package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.ForumPost;

/**
 * 论坛帖子Service接口
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public interface IForumPostService 
{
    /**
     * 查询论坛帖子
     * 
     * @param postId 论坛帖子主键
     * @return 论坛帖子
     */
    public ForumPost selectForumPostByPostId(Long postId);

    /**
     * 查询论坛帖子列表
     * 
     * @param forumPost 论坛帖子
     * @return 论坛帖子集合
     */
    public List<ForumPost> selectForumPostList(ForumPost forumPost);

    /**
     * 新增论坛帖子
     * 
     * @param forumPost 论坛帖子
     * @return 结果
     */
    public int insertForumPost(ForumPost forumPost);

    /**
     * 修改论坛帖子
     * 
     * @param forumPost 论坛帖子
     * @return 结果
     */
    public int updateForumPost(ForumPost forumPost);

    /**
     * 批量删除论坛帖子
     * 
     * @param postIds 需要删除的论坛帖子主键集合
     * @return 结果
     */
    public int deleteForumPostByPostIds(Long[] postIds);

    /**
     * 删除论坛帖子信息
     * 
     * @param postId 论坛帖子主键
     * @return 结果
     */
    public int deleteForumPostByPostId(Long postId);

    /**
     * 增加浏览次数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    public int increaseViewCount(Long postId);

    /**
     * 增加回复次数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    public int increaseReplyCount(Long postId);

    /**
     * 减少回复次数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    public int decreaseReplyCount(Long postId);

    /**
     * 点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    public int likePost(Long postId, Long userId);

    /**
     * 取消点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    public int unlikePost(Long postId, Long userId);

    /**
     * 检查用户是否点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否点赞
     */
    public boolean isLikedByUser(Long postId, Long userId);
}
