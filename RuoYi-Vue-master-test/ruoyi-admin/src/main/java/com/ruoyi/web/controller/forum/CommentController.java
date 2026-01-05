package com.ruoyi.web.controller.forum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Login;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.Comment;
import com.ruoyi.system.service.ICommentService;

/**
 * 评论Controller (来自 Mobile Terminal，适配前端接口)
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@RestController
@RequestMapping("/forum/posts/{postId}/comments")
public class CommentController extends BaseController
{
    @Autowired
    private ICommentService commentService;

    /**
     * 获取评论列表
     */
    @Anonymous
    @GetMapping
    public AjaxResult list(@PathVariable Long postId,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size) 
    {
        PageInfo<Comment> comments = commentService.listComments(postId, page, size);
        Map<String, Object> data = new HashMap<>();
        data.put("records", comments.getList());
        data.put("total", comments.getTotal());
        data.put("current", comments.getPageNum());
        data.put("size", comments.getPageSize());
        return success().put("data", data);
    }

    /**
     * 获取回复列表
     */
    @Anonymous
    @GetMapping("/{parentId}/replies")
    public AjaxResult listReplies(@PathVariable Long postId, @PathVariable Long parentId) 
    {
        List<Comment> replies = commentService.listReplies(parentId);
        return success().put("data", replies);
    }

    /**
     * 创建评论
     */
    @Login(required = true)
    @PostMapping
    public AjaxResult create(HttpServletRequest request,
                            @PathVariable Long postId,
                            @RequestBody Map<String, Object> body) 
    {
        Long userId = (Long) request.getAttribute("forumUserId");
        if (userId == null) {
            return error("未登录");
        }

        String content = (String) body.get("content");
        if (content == null || content.trim().isEmpty()) {
            return error("评论内容不能为空");
        }

        Long parentId = null;
        if (body.get("parentId") != null) {
            if (body.get("parentId") instanceof Number) {
                parentId = ((Number) body.get("parentId")).longValue();
            } else if (body.get("parentId") instanceof String) {
                try {
                    parentId = Long.parseLong((String) body.get("parentId"));
                } catch (NumberFormatException e) {
                    // 忽略
                }
            }
        }

        Comment comment = commentService.addComment(userId, postId, parentId, content, null);
        return success("评论成功").put("data", comment);
    }
}

