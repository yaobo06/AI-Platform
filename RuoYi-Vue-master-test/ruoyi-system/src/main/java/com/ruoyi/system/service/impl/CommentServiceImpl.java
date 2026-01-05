package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.Comment;
import com.ruoyi.system.mapper.CommentMapper;
import com.ruoyi.system.service.ICommentService;
import com.ruoyi.system.service.IPostService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 评论Service业务层处理 (来自 Mobile Terminal)
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@Service
public class CommentServiceImpl implements ICommentService 
{
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private IPostService postService;

    @Autowired
    private ISysUserService userService;

    @Override
    public Comment addComment(Long userId, Long postId, Long parentId, String content, String attachmentUrl) 
    {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPostId(postId);
        comment.setParentId(parentId);
        comment.setContent(content);
        comment.setAttachmentUrl(attachmentUrl);
        comment.setCreatedAt(new Date());
        commentMapper.insertComment(comment);
        postService.increaseCommentCount(postId, 1);
        return comment;
    }

    @Override
    public PageInfo<Comment> listComments(Long postId, int pageNum, int pageSize) 
    {
        PageHelper.startPage(pageNum, pageSize);
        Comment query = new Comment();
        query.setPostId(postId);
        query.setParentId(null); // 只查询顶级评论
        List<Comment> list = commentMapper.selectCommentList(query);
        enrichCommentsWithAuthorInfo(list);
        return new PageInfo<>(list);
    }

    @Override
    public List<Comment> listReplies(Long parentId) 
    {
        Comment query = new Comment();
        query.setParentId(parentId);
        List<Comment> comments = commentMapper.selectCommentList(query);
        enrichCommentsWithAuthorInfo(comments);
        return comments;
    }

    /**
     * 为评论列表填充作者信息
     */
    private void enrichCommentsWithAuthorInfo(List<Comment> comments) 
    {
        if (comments == null || comments.isEmpty()) {
            return;
        }
        
        // 收集所有用户ID
        List<Long> userIds = comments.stream()
                .map(Comment::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询用户信息
        Map<Long, SysUser> userMap = new HashMap<>();
        for (Long userId : userIds) {
            SysUser user = userService.selectUserById(userId);
            if (user != null) {
                userMap.put(userId, user);
            }
        }
        
        // 为每个评论设置作者信息
        for (Comment comment : comments) {
            SysUser author = userMap.get(comment.getUserId());
            if (author != null) {
                comment.setAuthorName(com.ruoyi.common.utils.AuthorInfoUtils.getDisplayName(author));
                comment.setAuthorAvatarUrl(author.getAvatar());
            }
            
            // 如果有父评论ID，查询父评论信息
            if (comment.getParentId() != null) {
                Comment parent = commentMapper.selectCommentById(comment.getParentId());
                if (parent != null) {
                    // 确保父评论的作者信息也被填充
                    SysUser parentAuthor = userMap.get(parent.getUserId());
                    if (parentAuthor == null) {
                        parentAuthor = userService.selectUserById(parent.getUserId());
                        if (parentAuthor != null) {
                            userMap.put(parent.getUserId(), parentAuthor);
                        }
                    }
                    if (parentAuthor != null) {
                        parent.setAuthorName(com.ruoyi.common.utils.AuthorInfoUtils.getDisplayName(parentAuthor));
                        parent.setAuthorAvatarUrl(parentAuthor.getAvatar());
                    }
                    comment.setParentComment(parent);
                }
            }

            // 检查是否有回复
            Comment countQuery = new Comment();
            countQuery.setParentId(comment.getId());
            long replyCount = commentMapper.countComments(countQuery);
            comment.setHasReplies(replyCount > 0);
        }
    }

    @Override
    public PageInfo<Comment> listUserComments(Long userId, int pageNum, int pageSize) 
    {
        PageHelper.startPage(pageNum, pageSize);
        Comment query = new Comment();
        query.setUserId(userId);
        List<Comment> list = commentMapper.selectCommentList(query);
        enrichCommentsWithAuthorInfo(list);
        
        // 检查每个评论是否有回复
        for (Comment comment : list) 
        {
            Comment countQuery = new Comment();
            countQuery.setParentId(comment.getId());
            long replyCount = commentMapper.countComments(countQuery);
            comment.setHasReplies(replyCount > 0);
        }
        
        return new PageInfo<>(list);
    }
}

