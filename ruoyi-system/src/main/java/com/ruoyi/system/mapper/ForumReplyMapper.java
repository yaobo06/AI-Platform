package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.ForumReply;

/**
 * 论坛回复Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public interface ForumReplyMapper 
{
    /**
     * 查询论坛回复
     * 
     * @param replyId 论坛回复主键
     * @return 论坛回复
     */
    public ForumReply selectForumReplyByReplyId(Long replyId);

    /**
     * 查询论坛回复列表
     * 
     * @param forumReply 论坛回复
     * @return 论坛回复集合
     */
    public List<ForumReply> selectForumReplyList(ForumReply forumReply);

    /**
     * 查询帖子的回复列表（树形结构）
     * 
     * @param postId 帖子ID
     * @return 论坛回复集合
     */
    public List<ForumReply> selectForumReplyTreeByPostId(Long postId);

    /**
     * 新增论坛回复
     * 
     * @param forumReply 论坛回复
     * @return 结果
     */
    public int insertForumReply(ForumReply forumReply);

    /**
     * 修改论坛回复
     * 
     * @param forumReply 论坛回复
     * @return 结果
     */
    public int updateForumReply(ForumReply forumReply);

    /**
     * 删除论坛回复
     * 
     * @param replyId 论坛回复主键
     * @return 结果
     */
    public int deleteForumReplyByReplyId(Long replyId);

    /**
     * 批量删除论坛回复
     * 
     * @param replyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteForumReplyByReplyIds(Long[] replyIds);

    /**
     * 根据帖子ID删除回复
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    public int deleteForumReplyByPostId(Long postId);

    /**
     * 增加点赞次数
     * 
     * @param replyId 回复ID
     * @return 结果
     */
    public int increaseLikeCount(Long replyId);

    /**
     * 减少点赞次数
     * 
     * @param replyId 回复ID
     * @return 结果
     */
    public int decreaseLikeCount(Long replyId);
}
