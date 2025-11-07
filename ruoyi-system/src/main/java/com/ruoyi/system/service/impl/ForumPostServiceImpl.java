package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ForumPostMapper;
import com.ruoyi.system.mapper.ForumLikeMapper;
import com.ruoyi.system.domain.ForumPost;
import com.ruoyi.system.domain.ForumLike;
import com.ruoyi.system.service.IForumPostService;

/**
 * 论坛帖子Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@Service
public class ForumPostServiceImpl implements IForumPostService 
{
    @Autowired
    private ForumPostMapper forumPostMapper;

    @Autowired
    private ForumLikeMapper forumLikeMapper;

    /**
     * 查询论坛帖子
     * 
     * @param postId 论坛帖子主键
     * @return 论坛帖子
     */
    @Override
    public ForumPost selectForumPostByPostId(Long postId)
    {
        return forumPostMapper.selectForumPostByPostId(postId);
    }

    /**
     * 查询论坛帖子列表
     * 
     * @param forumPost 论坛帖子
     * @return 论坛帖子
     */
    @Override
    public List<ForumPost> selectForumPostList(ForumPost forumPost)
    {
        return forumPostMapper.selectForumPostList(forumPost);
    }

    /**
     * 新增论坛帖子
     * 
     * @param forumPost 论坛帖子
     * @return 结果
     */
    @Override
    public int insertForumPost(ForumPost forumPost)
    {
        return forumPostMapper.insertForumPost(forumPost);
    }

    /**
     * 修改论坛帖子
     * 
     * @param forumPost 论坛帖子
     * @return 结果
     */
    @Override
    public int updateForumPost(ForumPost forumPost)
    {
        return forumPostMapper.updateForumPost(forumPost);
    }

    /**
     * 批量删除论坛帖子
     * 
     * @param postIds 需要删除的论坛帖子主键
     * @return 结果
     */
    @Override
    public int deleteForumPostByPostIds(Long[] postIds)
    {
        return forumPostMapper.deleteForumPostByPostIds(postIds);
    }

    /**
     * 删除论坛帖子信息
     * 
     * @param postId 论坛帖子主键
     * @return 结果
     */
    @Override
    public int deleteForumPostByPostId(Long postId)
    {
        return forumPostMapper.deleteForumPostByPostId(postId);
    }

    /**
     * 增加浏览次数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    @Override
    public int increaseViewCount(Long postId)
    {
        return forumPostMapper.incrementViewCount(postId);
    }

    /**
     * 增加回复次数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    @Override
    public int increaseReplyCount(Long postId)
    {
        return forumPostMapper.incrementReplyCount(postId);
    }

    /**
     * 减少回复次数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    @Override
    public int decreaseReplyCount(Long postId)
    {
        return forumPostMapper.decrementReplyCount(postId);
    }

    /**
     * 点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public int likePost(Long postId, Long userId)
    {
        // 检查是否已经点赞
        ForumLike existingLike = forumLikeMapper.selectForumLikeByUserAndTarget(userId, "1", postId);
        if (existingLike != null) {
            return 0; // 已经点赞过了
        }

        // 添加点赞记录
        ForumLike forumLike = new ForumLike();
        forumLike.setUserId(userId);
        forumLike.setTargetType("1"); // 1表示帖子
        forumLike.setTargetId(postId);
        int result = forumLikeMapper.insertForumLike(forumLike);

        if (result > 0) {
            // 增加帖子点赞数
            forumPostMapper.incrementLikeCount(postId);
        }

        return result;
    }

    /**
     * 取消点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override

    public int unlikePost(Long postId, Long userId)
    {
        // 删除点赞记录
        int result = forumLikeMapper.deleteForumLikeByUserAndTarget(userId, "1", postId);

        if (result > 0) {
            // 减少帖子点赞数
            forumPostMapper.decrementLikeCount(postId);
        }

        return result;
    }

    /**
     * 检查用户是否点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否点赞
     */
    @Override
    public boolean isLikedByUser(Long postId, Long userId)
    {
        ForumLike forumLike = forumLikeMapper.selectForumLikeByUserAndTarget(userId, "1", postId);
        return forumLike != null;
    }
}
