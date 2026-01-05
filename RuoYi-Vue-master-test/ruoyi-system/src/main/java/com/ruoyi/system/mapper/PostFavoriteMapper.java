package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.PostFavorite;

/**
 * 帖子收藏Mapper接口 (来自 Mobile Terminal)
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public interface PostFavoriteMapper 
{
    /**
     * 查询收藏
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 收藏
     */
    public PostFavorite selectPostFavorite(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 新增收藏
     * 
     * @param postFavorite 收藏
     * @return 结果
     */
    public int insertPostFavorite(PostFavorite postFavorite);

    /**
     * 删除收藏
     * 
     * @param id 收藏主键
     * @return 结果
     */
    public int deletePostFavoriteById(Long id);

    /**
     * 删除收藏（根据帖子ID和用户ID）
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    public int deletePostFavorite(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 查询用户的所有收藏（根据用户ID）
     * 
     * @param userId 用户ID
     * @return 收藏列表
     */
    public List<PostFavorite> selectPostFavoritesByUserId(Long userId);
}

