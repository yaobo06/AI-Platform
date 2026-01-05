package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.PostLike;

/**
 * 帖子点赞Mapper接口 (来自 Mobile Terminal)
 * 
 * @date 2025-01-24
 */
public interface PostLikeMapper 
{
    /**
     * 查询点赞
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 点赞
     */
    public PostLike selectPostLike(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 新增点赞
     * 
     * @param postLike 点赞
     * @return 结果
     */
    public int insertPostLike(PostLike postLike);

    /**
     * 删除点赞
     * 
     * @param id 点赞主键
     * @return 结果
     */
    public int deletePostLikeById(Long id);

    /**
     * 删除点赞（根据帖子ID和用户ID）
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    public int deletePostLike(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 查询用户的所有点赞（根据用户ID）
     * 
     * @param userId 用户ID
     * @return 点赞列表
     */
    public List<PostLike> selectPostLikesByUserId(Long userId);
}

