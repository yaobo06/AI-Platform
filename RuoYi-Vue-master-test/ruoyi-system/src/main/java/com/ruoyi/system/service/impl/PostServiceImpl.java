package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.system.domain.Post;
import com.ruoyi.system.domain.PostFavorite;
import com.ruoyi.system.domain.PostLike;
import com.ruoyi.system.mapper.PostFavoriteMapper;
import com.ruoyi.system.mapper.PostLikeMapper;
import com.ruoyi.system.mapper.PostMapper;
import com.ruoyi.system.service.IEmbeddingService;
import com.ruoyi.system.service.IPostService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.service.IVectorDBService;

/**
 * 帖子Service业务层处理 (来自 Mobile Terminal)
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@Service
public class PostServiceImpl implements IPostService 
{
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostLikeMapper postLikeMapper;

    @Autowired
    private PostFavoriteMapper postFavoriteMapper;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IEmbeddingService embeddingService;

    @Autowired
    private IVectorDBService vectorDBService;

    @Override
    public Post createPost(Long userId, String title, String content, String category) 
    {
        Date now = new Date();
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(title);
        post.setContent(content);
        post.setCategory(category);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setCreatedAt(now);
        post.setUpdatedAt(now);
        postMapper.insertPost(post);
        
        // 异步生成向量并存储（如果启用）
        generateAndStoreVector(post);
        
        return post;
    }

    /**
     * 异步生成向量并存储到 Milvus
     */
    private void generateAndStoreVector(Post post) 
    {
        try 
        {
            // 组合标题和内容
            String text = (post.getTitle() != null ? post.getTitle() : "") + " " + 
                         (post.getContent() != null ? post.getContent() : "");
            
            if (text.trim().isEmpty()) 
            {
                return;
            }
            
            // 生成向量
            float[] vector = embeddingService.generateEmbedding(text, false);
            
            if (vector != null && vectorDBService.isAvailable()) 
            {
                // 存储到 Milvus
                vectorDBService.insertVector(post.getId(), vector);
            }
        } 
        catch (Exception e) 
        {
            // 静默处理异常，不影响帖子创建
            org.slf4j.LoggerFactory.getLogger(PostServiceImpl.class)
                .warn("生成向量失败: {}", e.getMessage());
        }
    }

    @Override
    public Post selectPostById(Long id) 
    {
        Post post = postMapper.selectPostById(id);
        if (post != null) {
            enrichPostsWithAuthorInfo(java.util.Arrays.asList(post));
        }
        return post;
    }

    @Override
    public PageInfo<Post> listHot(int pageNum, int pageSize) 
    {
        PageHelper.startPage(pageNum, pageSize);
        Post query = new Post();
        List<Post> list = postMapper.selectPostList(query);
        // 按点赞数排序（需要在SQL中实现，这里简化处理）
        list.sort((a, b) -> {
            int likeCompare = (b.getLikeCount() != null ? b.getLikeCount() : 0) 
                           - (a.getLikeCount() != null ? a.getLikeCount() : 0);
            if (likeCompare != 0) {
                return likeCompare;
            }
            // 如果点赞数相同，按创建时间降序
            if (a.getCreatedAt() != null && b.getCreatedAt() != null) {
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            }
            return 0;
        });
        enrichPostsWithAuthorInfo(list);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<Post> listTrending(int pageNum, int pageSize) 
    {
        // 查询所有帖子
        Post query = new Post();
        List<Post> allPosts = postMapper.selectPostList(query);
        
        // 使用加权算法计算热度分数（复用 SearchServiceImpl 的逻辑）
        // 点赞权重：0.6，评论权重：0.4
        double likeWeight = 0.6;
        double commentWeight = 0.4;
        
        // 计算每个帖子的热度分数并排序
        allPosts.sort((a, b) -> {
            double scoreA = calculatePopularityScore(a, likeWeight, commentWeight);
            double scoreB = calculatePopularityScore(b, likeWeight, commentWeight);
            return Double.compare(scoreB, scoreA); // 降序
        });
        
        // 填充作者信息
        enrichPostsWithAuthorInfo(allPosts);
        
        // 使用工具类进行分页
        return com.ruoyi.common.utils.PaginationUtils.paginateListToPageInfo(allPosts, pageNum, pageSize);
    }

    /**
     * 计算帖子热度分数（复用 SearchServiceImpl 的逻辑）
     * 
     * @param post 帖子
     * @param likeWeight 点赞权重
     * @param commentWeight 评论权重
     * @return 热度分数
     */
    private double calculatePopularityScore(Post post, double likeWeight, double commentWeight) 
    {
        int likes = post.getLikeCount() != null ? post.getLikeCount() : 0;
        int comments = post.getCommentCount() != null ? post.getCommentCount() : 0;
        // 使用对数归一化，避免数值过大
        double normalizedLikes = Math.log1p(likes) / Math.log(100);
        double normalizedComments = Math.log1p(comments) / Math.log(50);
        return normalizedLikes * likeWeight + normalizedComments * commentWeight;
    }

    @Override
    public PageInfo<Post> listRecent(int pageNum, int pageSize) 
    {
        PageHelper.startPage(pageNum, pageSize);
        Post query = new Post();
        List<Post> list = postMapper.selectPostList(query);
        // SQL中已按 created_at desc 排序，无需再次排序
        enrichPostsWithAuthorInfo(list);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public boolean like(Long userId, Long postId) 
    {
        PostLike existing = postLikeMapper.selectPostLike(postId, userId);
        
        Post post = postMapper.selectPostById(postId);
        if (post == null) {
            return false;
        }
        
        if (existing != null) {
            // 已点赞，执行取消点赞
            postLikeMapper.deletePostLikeById(existing.getId());
            int currentCount = post.getLikeCount() != null ? post.getLikeCount() : 0;
            post.setLikeCount(Math.max(0, currentCount - 1));
            post.setUpdatedAt(new Date());
            postMapper.updatePost(post);
            return false; // false 表示取消点赞
        } else {
            // 未点赞，执行点赞
            PostLike like = new PostLike();
            like.setPostId(postId);
            like.setUserId(userId);
            like.setCreatedAt(new Date());
            postLikeMapper.insertPostLike(like);

            int currentCount = post.getLikeCount() != null ? post.getLikeCount() : 0;
            post.setLikeCount(currentCount + 1);
            post.setUpdatedAt(new Date());
            postMapper.updatePost(post);
            return true; // true 表示点赞成功
        }
    }

    @Override
    public boolean isLiked(Long userId, Long postId) 
    {
        PostLike existing = postLikeMapper.selectPostLike(postId, userId);
        return existing != null;
    }

    @Override
    @Transactional
    public boolean favorite(Long userId, Long postId) 
    {
        PostFavorite existing = postFavoriteMapper.selectPostFavorite(postId, userId);
        
        Post post = postMapper.selectPostById(postId);
        if (post == null) {
            return false;
        }
        
        if (existing != null) {
            // 已收藏，执行取消收藏
            postFavoriteMapper.deletePostFavoriteById(existing.getId());
            return false; // false 表示取消收藏
        } else {
            // 未收藏，执行收藏
            PostFavorite favorite = new PostFavorite();
            favorite.setPostId(postId);
            favorite.setUserId(userId);
            favorite.setCreatedAt(new Date());
            postFavoriteMapper.insertPostFavorite(favorite);
            return true; // true 表示收藏成功
        }
    }

    @Override
    public boolean isFavorited(Long userId, Long postId) 
    {
        PostFavorite existing = postFavoriteMapper.selectPostFavorite(postId, userId);
        return existing != null;
    }

    @Override
    public void increaseCommentCount(Long postId, int delta) 
    {
        Post post = postMapper.selectPostById(postId);
        if (post != null) {
            int count = post.getCommentCount() == null ? 0 : post.getCommentCount();
            post.setCommentCount(count + delta);
            post.setUpdatedAt(new Date());
            postMapper.updatePost(post);
        }
    }

    @Override
    public PageInfo<Post> listUserPosts(Long userId, int pageNum, int pageSize) 
    {
        PageHelper.startPage(pageNum, pageSize);
        Post query = new Post();
        query.setUserId(userId);
        List<Post> list = postMapper.selectPostList(query);
        enrichPostsWithAuthorInfo(list);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<Post> listUserLikedPosts(Long userId, int pageNum, int pageSize) 
    {
        // 查询用户点赞的帖子ID列表
        List<PostLike> likes = postLikeMapper.selectPostLikesByUserId(userId);
        
        // 提取帖子ID
        List<Long> postIds = likes.stream()
            .map(PostLike::getPostId)
            .collect(java.util.stream.Collectors.toList());
        
        // 使用工具类进行分页处理
        return com.ruoyi.common.utils.PaginationUtils.paginateByIds(
            postIds, 
            pageNum, 
            pageSize,
            pagedPostIds -> {
                List<Post> posts = postMapper.selectPostByIds(pagedPostIds);
                enrichPostsWithAuthorInfo(posts);
                return posts;
            }
        );
    }

    @Override
    public PageInfo<Post> listUserFavoritedPosts(Long userId, int pageNum, int pageSize) 
    {
        // 查询用户收藏的帖子ID列表
        List<PostFavorite> favorites = postFavoriteMapper.selectPostFavoritesByUserId(userId);
        
        // 提取帖子ID
        List<Long> postIds = favorites.stream()
            .map(PostFavorite::getPostId)
            .collect(java.util.stream.Collectors.toList());
        
        // 使用工具类进行分页处理
        return com.ruoyi.common.utils.PaginationUtils.paginateByIds(
            postIds, 
            pageNum, 
            pageSize,
            pagedPostIds -> {
                List<Post> posts = postMapper.selectPostByIds(pagedPostIds);
                enrichPostsWithAuthorInfo(posts);
                return posts;
            }
        );
    }

    /**
     * 为帖子列表填充作者信息
     */
    private void enrichPostsWithAuthorInfo(List<Post> posts) 
    {
        com.ruoyi.common.utils.AuthorInfoUtils.enrichWithAuthorInfo(
            posts,
            Post::getUserId,
            (post, author) -> {
                post.setAuthorName(com.ruoyi.common.utils.AuthorInfoUtils.getDisplayName(author));
                post.setAuthorAvatarUrl(author.getAvatar());
            },
            userService
        );
    }
}

