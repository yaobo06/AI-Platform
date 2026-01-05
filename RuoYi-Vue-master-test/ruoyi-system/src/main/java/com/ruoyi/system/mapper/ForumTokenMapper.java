package com.ruoyi.system.mapper;

import java.util.Date;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.ForumToken;

/**
 * 论坛TokenMapper接口
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public interface ForumTokenMapper 
{
    /**
     * 新增论坛Token
     * 
     * @param forumToken 论坛Token
     * @return 结果
     */
    public int insertForumToken(ForumToken forumToken);

    /**
     * 根据token查询用户ID
     * 
     * @param token token值
     * @return 用户ID
     */
    public Long selectUserIdByToken(String token);

    /**
     * 根据token查询Token信息
     * 
     * @param token token值
     * @return Token信息
     */
    public ForumToken selectForumTokenByToken(String token);

    /**
     * 删除过期token
     * 
     * @param expireTime 过期时间
     * @return 结果
     */
    public int deleteExpiredTokens(Date expireTime);

    /**
     * 根据用户ID删除所有token
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteTokensByUserId(Long userId);

    /**
     * 根据token删除
     * 
     * @param token token值
     * @return 结果
     */
    public int deleteTokenByToken(String token);
}

