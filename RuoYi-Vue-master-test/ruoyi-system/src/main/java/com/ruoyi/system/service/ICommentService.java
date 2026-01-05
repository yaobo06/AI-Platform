package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Comment;
import com.github.pagehelper.PageInfo;

/**
 * 评论Service接口 (来自 Mobile Terminal)
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public interface ICommentService 
{
    /**
     * 添加评论
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @param parentId 父评论ID（可为null）
     * @param content 内容
     * @param attachmentUrl 附件URL（可为null）
     * @return 评论
     */
    public Comment addComment(Long userId, Long postId, Long parentId, String content, String attachmentUrl);

    /**
     * 查询评论列表
     * 
     * @param postId 帖子ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 评论分页信息
     */
    public PageInfo<Comment> listComments(Long postId, int pageNum, int pageSize);

    /**
     * 查询回复列表
     * 
     * @param parentId 父评论ID
     * @return 回复列表
     */
    public List<Comment> listReplies(Long parentId);

    /**
     * 获取用户的评论列表（包括回复）
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 评论分页信息
     */
    public PageInfo<Comment> listUserComments(Long userId, int pageNum, int pageSize);
}

