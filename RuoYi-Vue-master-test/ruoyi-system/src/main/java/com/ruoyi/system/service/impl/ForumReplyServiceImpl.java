package com.ruoyi.system.service.impl;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ForumReplyMapper;
import com.ruoyi.system.mapper.ForumLikeMapper;
import com.ruoyi.system.domain.ForumReply;
import com.ruoyi.system.domain.ForumLike;
import com.ruoyi.system.service.IForumReplyService;

/**
 * 论坛回复Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@Service
public class ForumReplyServiceImpl implements IForumReplyService 
{
    @Autowired
    private ForumReplyMapper forumReplyMapper;

    @Autowired
    private ForumLikeMapper forumLikeMapper;

    /**
     * 查询论坛回复
     * 
     * @param replyId 论坛回复主键
     * @return 论坛回复
     */
    @Override
    public ForumReply selectForumReplyByReplyId(Long replyId)
    {
        ForumReply reply = forumReplyMapper.selectForumReplyByReplyId(replyId);
        enrichDisplayFields(reply);
        return reply;
    }

    /**
     * 查询论坛回复列表
     * 
     * @param forumReply 论坛回复
     * @return 论坛回复
     */
    @Override
    public List<ForumReply> selectForumReplyList(ForumReply forumReply)
    {
        List<ForumReply> list = forumReplyMapper.selectForumReplyList(forumReply);
        list.forEach(this::enrichDisplayFields);
        return list;
    }

    /**
     * 查询帖子的回复列表（树形结构）
     * 
     * @param postId 帖子ID
     * @return 论坛回复集合
     */
    @Override
    public List<ForumReply> selectForumReplyTreeByPostId(Long postId)
    {
        List<ForumReply> replies = forumReplyMapper.selectForumReplyTreeByPostId(postId);
        replies.forEach(this::enrichReplyRecursively);
        return replies;
    }

    /**
     * 新增论坛回复
     * 
     * @param forumReply 论坛回复
     * @return 结果
     */
    @Override
    public int insertForumReply(ForumReply forumReply)
    {
        return forumReplyMapper.insertForumReply(forumReply);
    }

    /**
     * 修改论坛回复
     * 
     * @param forumReply 论坛回复
     * @return 结果
     */
    @Override
    public int updateForumReply(ForumReply forumReply)
    {
        return forumReplyMapper.updateForumReply(forumReply);
    }

    /**
     * 批量删除论坛回复
     * 
     * @param replyIds 需要删除的论坛回复主键
     * @return 结果
     */
    @Override
    public int deleteForumReplyByReplyIds(Long[] replyIds)
    {
        return forumReplyMapper.deleteForumReplyByReplyIds(replyIds);
    }

    /**
     * 删除论坛回复信息
     * 
     * @param replyId 论坛回复主键
     * @return 结果
     */
    @Override
    public int deleteForumReplyByReplyId(Long replyId)
    {
        return forumReplyMapper.deleteForumReplyByReplyId(replyId);
    }

    /**
     * 根据帖子ID删除回复
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    @Override
    public int deleteForumReplyByPostId(Long postId)
    {
        return forumReplyMapper.deleteForumReplyByPostId(postId);
    }

    /**
     * 点赞回复
     * 
     * @param replyId 回复ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public int likeReply(Long replyId, Long userId)
    {
        // 检查是否已经点赞
        ForumLike existingLike = forumLikeMapper.selectForumLikeByUserAndTarget(userId, "2", replyId);
        if (existingLike != null) {
            return 0; // 已经点赞过了
        }

        // 添加点赞记录
        ForumLike forumLike = new ForumLike();
        forumLike.setUserId(userId);
        forumLike.setTargetType("2"); // 2表示回复
        forumLike.setTargetId(replyId);
        int result = forumLikeMapper.insertForumLike(forumLike);

        if (result > 0) {
            // 增加回复点赞数
            forumReplyMapper.increaseLikeCount(replyId);
        }

        return result;
    }

    /**
     * 取消点赞回复
     * 
     * @param replyId 回复ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public int unlikeReply(Long replyId, Long userId)
    {
        // 删除点赞记录
        int result = forumLikeMapper.deleteForumLikeByUserAndTarget(userId, "2", replyId);

        if (result > 0) {
            // 减少回复点赞数
            forumReplyMapper.decreaseLikeCount(replyId);
        }

        return result;
    }

    /**
     * 检查用户是否点赞回复
     * 
     * @param replyId 回复ID
     * @param userId 用户ID
     * @return 是否点赞
     */
    @Override
    public boolean isLikedByUser(Long replyId, Long userId)
    {
        ForumLike forumLike = forumLikeMapper.selectForumLikeByUserAndTarget(userId, "2", replyId);
        return forumLike != null;
    }

    private void enrichReplyRecursively(ForumReply reply)
    {
        enrichDisplayFields(reply);
        if (reply != null && reply.getChildren() != null)
        {
            reply.getChildren().forEach(this::enrichReplyRecursively);
        }
    }

    private void enrichDisplayFields(ForumReply reply)
    {
        if (reply == null)
        {
            return;
        }
        if ((reply.getUserId() == null || reply.getUserId() == 0L) && StringUtils.isBlank(reply.getUserName()))
        {
            reply.setUserName(reply.getCreateBy());
        }
    }
}
