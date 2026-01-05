package com.ruoyi.system.service;

import java.util.Date;

/**
 * 论坛TokenService接口
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public interface IForumTokenService 
{
    /**
     * 创建token
     * 
     * @param userId 用户ID
     * @return token值
     */
    public String createToken(Long userId);

    /**
     * 根据token获取用户ID
     * 
     * @param token token值
     * @return 用户ID，如果token无效或过期返回null
     */
    public Long getUserIdByToken(String token);

    /**
     * 刷新token过期时间
     * 
     * @param token token值
     * @return 是否成功
     */
    public boolean refreshToken(String token);

    /**
     * 删除token
     * 
     * @param token token值
     * @return 是否成功
     */
    public boolean deleteToken(String token);

    /**
     * 删除用户的所有token
     * 
     * @param userId 用户ID
     * @return 是否成功
     */
    public boolean deleteTokensByUserId(Long userId);

    /**
     * 清理过期token
     */
    public void cleanExpiredTokens();

    /**
     * 直接保存指定的token到数据库
     * 
     * @param userId 用户ID
     * @param token token值
     * @param expireTime 过期时间
     */
    public void saveToken(Long userId, String token, Date expireTime);
}

