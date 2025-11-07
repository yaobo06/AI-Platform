package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.ForumPost;

/**
 * 论坛帖子Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public interface ForumPostMapper 
{
    /**
     * 查询论坛帖子

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
     * 删除论坛帖子
     * 
     * @param postId 论坛帖子主键
     * @return 结果
     */
    public int deleteForumPostByPostId(Long postId);

    /**
     * 批量删除论坛帖子
     * 
     * @param postIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteForumPostByPostIds(Long[] postIds);

    /**
     * 增加浏览次数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    public int incrementViewCount(Long postId);

    /**
     * 增加回复次数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    public int incrementReplyCount(Long postId);

    /**
     * 减少回复次数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    public int decrementReplyCount(Long postId);

    /**
     * 增加点赞次数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    public int incrementLikeCount(Long postId);

    /**
     * 减少点赞次数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    public int decrementLikeCount(Long postId);

    /**
     * 更新最后回复信息
     * 
     * @param forumPost 帖子信息
     * @return 结果
     */
    public int updateLastReplyInfo(ForumPost forumPost);
}
