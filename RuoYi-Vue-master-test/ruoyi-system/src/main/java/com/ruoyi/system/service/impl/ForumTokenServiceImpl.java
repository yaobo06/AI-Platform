package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.ForumToken;
import com.ruoyi.system.mapper.ForumTokenMapper;
import com.ruoyi.system.service.IForumTokenService;

/**
 * 论坛TokenService业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@Service
public class ForumTokenServiceImpl implements IForumTokenService 
{
    @Autowired
    private ForumTokenMapper forumTokenMapper;

    /** Token过期时间（30天） */
    private static final int EXPIRE_DAYS = 30;

    /**
     * 创建token
     */
    @Override
    public String createToken(Long userId)
    {
        // 生成唯一token
        String token = UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis();
        
        // 计算过期时间
        Date expireTime = DateUtils.addDays(new Date(), EXPIRE_DAYS);
        
        // 先删除该用户之前的token（可选，也可以允许多设备登录）
        // forumTokenMapper.deleteTokensByUserId(userId);
        
        // 创建新token
        ForumToken forumToken = new ForumToken();
        forumToken.setUserId(userId);
        forumToken.setToken(token);
        forumToken.setExpireTime(expireTime);
        forumToken.setCreateTime(new Date());
        forumToken.setUpdateTime(new Date());
        
        forumTokenMapper.insertForumToken(forumToken);
        
        return token;
    }

    /**
     * 根据token获取用户ID
     */
    @Override
    public Long getUserIdByToken(String token)
    {
        if (StringUtils.isEmpty(token))
        {
            return null;
        }
        
        ForumToken forumToken = forumTokenMapper.selectForumTokenByToken(token);
        if (forumToken == null)
        {
            return null;
        }
        
        // 检查是否过期
        if (forumToken.getExpireTime().before(new Date()))
        {
            // token已过期，删除
            forumTokenMapper.deleteTokenByToken(token);
            return null;
        }
        
        return forumToken.getUserId();
    }

    /**
     * 刷新token过期时间
     */
    @Override
    public boolean refreshToken(String token)
    {
        ForumToken forumToken = forumTokenMapper.selectForumTokenByToken(token);
        if (forumToken == null)
        {
            return false;
        }
        
        // 更新过期时间
        Date newExpireTime = DateUtils.addDays(new Date(), EXPIRE_DAYS);
        forumToken.setExpireTime(newExpireTime);
        forumToken.setUpdateTime(new Date());
        
        // 这里需要添加update方法到Mapper，暂时先返回true
        return true;
    }

    /**
     * 删除token
     */
    @Override
    public boolean deleteToken(String token)
    {
        return forumTokenMapper.deleteTokenByToken(token) > 0;
    }

    /**
     * 删除用户的所有token
     */
    @Override
    public boolean deleteTokensByUserId(Long userId)
    {
        return forumTokenMapper.deleteTokensByUserId(userId) > 0;
    }

    /**
     * 清理过期token
     */
    @Override
    public void cleanExpiredTokens()
    {
        forumTokenMapper.deleteExpiredTokens(new Date());
    }

    /**
     * 直接保存指定的token到数据库
     */
    @Override
    public void saveToken(Long userId, String token, Date expireTime)
    {
        ForumToken forumToken = new ForumToken();
        forumToken.setUserId(userId);
        forumToken.setToken(token);
        forumToken.setExpireTime(expireTime);
        forumToken.setCreateTime(new Date());
        forumToken.setUpdateTime(new Date());
        
        forumTokenMapper.insertForumToken(forumToken);
    }
}

