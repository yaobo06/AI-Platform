package com.ruoyi.web.controller.forum;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IForumPostService;
import com.ruoyi.system.service.IForumReplyService;
import com.ruoyi.common.annotation.Anonymous;

/**
 * 论坛统计Controller
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@RestController
@RequestMapping("/forum")
public class ForumStatsController extends BaseController
{
    @Autowired
    private IForumPostService forumPostService;

    @Autowired
    private IForumReplyService forumReplyService;

    /**
     * 获取论坛统计信息
     */
    @Anonymous
    @GetMapping("/stats")
    public AjaxResult getStats()
    {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取帖子总数
        int totalPosts = forumPostService.selectForumPostList(null).size();
        
        // 获取评论总数
        int totalComments = forumReplyService.selectForumReplyList(null).size();
        
        // 获取用户总数（这里简化处理，实际应该从用户表统计）
        int totalUsers = 100; // 暂时写死，后续可以从用户表统计
        
        stats.put("totalPosts", totalPosts);
        stats.put("totalComments", totalComments);
        stats.put("totalUsers", totalUsers);
        
        return success(stats);
    }
}
