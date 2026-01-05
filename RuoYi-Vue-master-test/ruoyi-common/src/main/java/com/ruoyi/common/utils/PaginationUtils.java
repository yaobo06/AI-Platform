package com.ruoyi.common.utils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.github.pagehelper.PageInfo;

/**
 * 分页工具类（用于手动分页场景）
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
public class PaginationUtils 
{
    /**
     * 手动分页处理（从ID列表提取并分页）
     * 
     * @param <T> ID类型
     * @param <R> 结果类型
     * @param idList ID列表
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页大小
     * @param idToEntityMapper ID到实体的映射函数
     * @return 分页结果
     */
    public static <T, R> PageInfo<R> paginateByIds(
            List<T> idList, 
            int pageNum, 
            int pageSize,
            Function<List<T>, List<R>> idToEntityMapper) 
    {
        if (idList == null || idList.isEmpty()) 
        {
            return new PageInfo<>(Collections.<R>emptyList());
        }
        
        // 手动分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, idList.size());
        
        if (start >= idList.size()) 
        {
            return new PageInfo<>(Collections.<R>emptyList());
        }
        
        List<T> pagedIds = idList.subList(start, end);
        List<R> entities = idToEntityMapper.apply(pagedIds);
        
        // 构建分页结果
        PageInfo<R> pageInfo = new PageInfo<>(entities);
        pageInfo.setTotal(idList.size());
        return pageInfo;
    }
    
    /**
     * 手动分页处理（直接对列表分页）
     * 
     * @param <T> 列表元素类型
     * @param list 列表
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页大小
     * @return 分页后的列表
     */
    public static <T> List<T> paginateList(List<T> list, int pageNum, int pageSize) 
    {
        if (list == null || list.isEmpty()) 
        {
            return Collections.emptyList();
        }
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        
        if (start >= list.size()) 
        {
            return Collections.emptyList();
        }
        
        return list.subList(start, end);
    }
    
    /**
     * 手动分页处理（直接对列表分页，返回分页信息）
     * 
     * @param <T> 列表元素类型
     * @param list 列表
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public static <T> PageInfo<T> paginateListToPageInfo(List<T> list, int pageNum, int pageSize) 
    {
        if (list == null || list.isEmpty()) 
        {
            return new PageInfo<>(Collections.<T>emptyList());
        }
        
        List<T> paginatedList = paginateList(list, pageNum, pageSize);
        PageInfo<T> pageInfo = new PageInfo<>(paginatedList);
        pageInfo.setTotal(list.size());
        return pageInfo;
    }
    
    /**
     * 计算总页数
     * 
     * @param total 总记录数
     * @param pageSize 每页大小
     * @return 总页数
     */
    public static int calculateTotalPages(int total, int pageSize) 
    {
        if (pageSize <= 0) 
        {
            return 0;
        }
        return (total + pageSize - 1) / pageSize; // 向上取整
    }
}

