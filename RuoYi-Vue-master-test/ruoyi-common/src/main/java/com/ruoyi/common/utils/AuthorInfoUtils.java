package com.ruoyi.common.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ruoyi.common.core.domain.entity.SysUser;

/**
 * 作者信息填充工具类
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public class AuthorInfoUtils 
{
    /**
     * 批量查询用户信息并构建用户Map
     * 
     * @param userIds 用户ID列表
     * @param userService 用户服务（需要实现 selectUserById 方法）
     * @return 用户ID到用户对象的映射
     */
    public static Map<Long, SysUser> buildUserMap(List<Long> userIds, Object userService) 
    {
        Map<Long, SysUser> userMap = new HashMap<Long, SysUser>();
        if (userIds == null || userIds.isEmpty() || userService == null) 
        {
            return userMap;
        }
        
        try 
        {
            java.lang.reflect.Method selectUserById = userService.getClass().getMethod("selectUserById", Long.class);
            for (Long userId : userIds) 
            {
                SysUser user = (SysUser) selectUserById.invoke(userService, userId);
                if (user != null) 
                {
                    userMap.put(userId, user);
                }
            }
        } 
        catch (Exception e) 
        {
            // 如果反射失败，返回空Map
        }
        
        return userMap;
    }
    
    /**
     * 从用户对象提取显示名称（优先使用昵称，否则使用用户名）
     * 
     * @param user 用户对象
     * @return 显示名称
     */
    public static String getDisplayName(SysUser user) 
    {
        if (user == null) 
        {
            return null;
        }
        return StringUtils.isNotEmpty(user.getNickName()) ? user.getNickName() : user.getUserName();
    }
    
    /**
     * 为实体列表填充作者信息（通用方法）
     * 
     * @param <T> 实体类型（必须包含 userId 字段）
     * @param entities 实体列表
     * @param getUserIdFunc 获取用户ID的函数
     * @param setAuthorInfoFunc 设置作者信息的函数
     * @param userService 用户服务（需要实现 selectUserById 方法）
     */
    public static <T> void enrichWithAuthorInfo(
            List<T> entities,
            Function<T, Long> getUserIdFunc,
            AuthorInfoSetter<T> setAuthorInfoFunc,
            Object userService) 
    {
        if (entities == null || entities.isEmpty() || userService == null) 
        {
            return;
        }
        
        // 收集所有用户ID
        List<Long> userIds = entities.stream()
                .map(getUserIdFunc)
                .filter(userId -> userId != null)
                .distinct()
                .collect(Collectors.toList());
        
        if (userIds.isEmpty()) 
        {
            return;
        }
        
        // 批量查询用户信息
        Map<Long, SysUser> userMap = buildUserMap(userIds, userService);
        
        // 为每个实体设置作者信息
        for (T entity : entities) 
        {
            Long userId = getUserIdFunc.apply(entity);
            if (userId != null) 
            {
                SysUser author = userMap.get(userId);
                if (author != null) 
                {
                    setAuthorInfoFunc.setAuthorInfo(entity, author);
                }
            }
        }
    }
    
    /**
     * 作者信息设置器接口
     * 
     * @param <T> 实体类型
     */
    @FunctionalInterface
    public interface AuthorInfoSetter<T> 
    {
        /**
         * 设置作者信息
         * 
         * @param entity 实体对象
         * @param author 作者用户对象
         */
        void setAuthorInfo(T entity, SysUser author);
    }
}

