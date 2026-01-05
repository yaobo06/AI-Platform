package com.ruoyi.web.controller.forum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.Post;
import com.ruoyi.system.service.ISearchService;

/**
 * 搜索Controller (来自 Mobile Terminal)
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@RestController
@RequestMapping("/forum/search")
public class SearchController extends BaseController
{
    @Autowired
    private ISearchService searchService;

    /**
     * 搜索帖子（仿照 Mobile Terminal Backend 逻辑）
     */
    @Anonymous
    @GetMapping
    public AjaxResult search(@RequestParam(value = "q", required = false) String q,
                            @RequestParam(value = "query", required = false) String queryParam,
                            @RequestParam(defaultValue = "hybrid") String mode,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size) 
    {
        // 支持 q 和 query 两种参数名
        String query = q != null ? q : (queryParam != null ? queryParam : "");
        
        if (query == null || query.trim().isEmpty()) 
        {
            Map<String, Object> payload = new HashMap<String, Object>();
            payload.put("mode", mode);
            payload.put("results", java.util.Collections.emptyList());
            payload.put("count", 0);
            payload.put("total", 0);
            payload.put("page", page);
            payload.put("size", size);
            payload.put("totalPages", 0);
            return success(payload);
        }
        
        logger.info("搜索请求 - 查询: \"{}\", 模式: {}, 页码: {}, 每页: {}", query, mode, page, size);
        
        List<Post> list = searchService.search(query, mode, size * 2); // 获取更多结果用于分页
        int total = list.size();
        
        logger.debug("搜索服务返回结果数: {}", total);
        
        // 使用工具类进行分页
        List<Post> paginatedList = com.ruoyi.common.utils.PaginationUtils.paginateList(list, page, size);
        
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("mode", mode);
        payload.put("results", paginatedList);
        payload.put("count", paginatedList.size());
        payload.put("total", total);
        payload.put("page", page);
        payload.put("size", size);
        payload.put("totalPages", com.ruoyi.common.utils.PaginationUtils.calculateTotalPages(total, size));
        
        return success(payload);
    }
}

