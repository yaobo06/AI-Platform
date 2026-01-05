package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Post;
import com.github.pagehelper.PageInfo;

/**
 * 帖子Service接口 (来自 Mobile Terminal)
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public interface IPostService 
{
    /**
     * 创建帖子
     * 
     * @param userId 用户ID
     * @param title 标题
     * @param content 内容
     * @param category 分类
     * @return 帖子
     */
    public Post createPost(Long userId, String title, String content, String category);

    /**
     * 查询帖子
     * 
     * @param id 帖子主键
     * @return 帖子
     */
    public Post selectPostById(Long id);

    /**
     * 查询帖子列表（热门）
     * 
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 帖子分页信息
     */
    public PageInfo<Post> listHot(int pageNum, int pageSize);

    /**
     * 查询帖子列表（最近）
     * 
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 帖子分页信息
     */
    public PageInfo<Post> listRecent(int pageNum, int pageSize);

    /**
     * 查询热门帖子列表（按点赞和评论加权计算，用于"大家都在看"）
     * 
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 热门帖子分页信息
     */
    public PageInfo<Post> listTrending(int pageNum, int pageSize);

    /**
     * 点赞或取消点赞
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return true表示点赞成功，false表示取消点赞成功
     */
    public boolean like(Long userId, Long postId);

    /**
     * 检查用户是否已点赞
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否已点赞
     */
    public boolean isLiked(Long userId, Long postId);

    /**
     * 收藏或取消收藏
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return true表示收藏成功，false表示取消收藏成功
     */
    public boolean favorite(Long userId, Long postId);

    /**
     * 检查用户是否已收藏
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否已收藏
     */
    public boolean isFavorited(Long userId, Long postId);

    /**
     * 增加评论数
     * 
     * @param postId 帖子ID
     * @param delta 增量
     */
    public void increaseCommentCount(Long postId, int delta);

    /**
     * 获取用户发布的帖子
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 帖子分页信息
     */
    public PageInfo<Post> listUserPosts(Long userId, int pageNum, int pageSize);

    /**
     * 获取用户点赞的帖子
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 帖子分页信息
     */
    public PageInfo<Post> listUserLikedPosts(Long userId, int pageNum, int pageSize);

    /**
     * 获取用户收藏的帖子
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 帖子分页信息
     */
    public PageInfo<Post> listUserFavoritedPosts(Long userId, int pageNum, int pageSize);
}

