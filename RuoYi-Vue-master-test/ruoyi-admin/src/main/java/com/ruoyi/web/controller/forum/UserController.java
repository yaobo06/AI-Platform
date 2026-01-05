package com.ruoyi.web.controller.forum;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.ruoyi.common.annotation.Login;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.Comment;
import com.ruoyi.system.domain.Post;
import com.ruoyi.system.service.ICommentService;
import com.ruoyi.system.service.IPostService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 用户Controller (来自 Mobile Terminal，用户个人中心功能)
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@RestController
@RequestMapping("/forum/user")
public class UserController extends BaseController
{
    @Autowired
    private IPostService postService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private ISysUserService userService;

    /**
     * 获取我的发帖
     */
    @Login(required = true)
    @GetMapping("/posts")
    public AjaxResult getMyPosts(HttpServletRequest request,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size) 
    {
        Long userId = (Long) request.getAttribute("forumUserId");
        if (userId == null) {
            return error("未登录");
        }

        PageInfo<Post> result = postService.listUserPosts(userId, page, size);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("records", result.getList());
        data.put("total", result.getTotal());
        data.put("current", result.getPageNum());
        data.put("size", result.getPageSize());
        return success().put("data", data);
    }

    /**
     * 获取我的点赞
     */
    @Login(required = true)
    @GetMapping("/liked-posts")
    public AjaxResult getMyLikedPosts(HttpServletRequest request,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size) 
    {
        Long userId = (Long) request.getAttribute("forumUserId");
        if (userId == null) {
            return error("未登录");
        }

        PageInfo<Post> result = postService.listUserLikedPosts(userId, page, size);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("records", result.getList());
        data.put("total", result.getTotal());
        data.put("current", result.getPageNum());
        data.put("size", result.getPageSize());
        return success().put("data", data);
    }

    /**
     * 获取我的收藏
     */
    @Login(required = true)
    @GetMapping("/favorited-posts")
    public AjaxResult getMyFavoritedPosts(HttpServletRequest request,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) 
    {
        Long userId = (Long) request.getAttribute("forumUserId");
        if (userId == null) {
            return error("未登录");
        }

        PageInfo<Post> result = postService.listUserFavoritedPosts(userId, page, size);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("records", result.getList());
        data.put("total", result.getTotal());
        data.put("current", result.getPageNum());
        data.put("size", result.getPageSize());
        return success().put("data", data);
    }

    /**
     * 获取我的评论
     */
    @Login(required = true)
    @GetMapping("/comments")
    public AjaxResult getMyComments(HttpServletRequest request,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size) 
    {
        Long userId = (Long) request.getAttribute("forumUserId");
        if (userId == null) {
            return error("未登录");
        }

        PageInfo<Comment> result = commentService.listUserComments(userId, page, size);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("records", result.getList());
        data.put("total", result.getTotal());
        data.put("current", result.getPageNum());
        data.put("size", result.getPageSize());
        return success().put("data", data);
    }

    /**
     * 获取当前登录用户信息
     */
    @Login(required = true)
    @GetMapping("/info")
    public AjaxResult getInfo(HttpServletRequest request) 
    {
        Long userId = (Long) request.getAttribute("forumUserId");
        if (userId == null) {
            return error("未登录");
        }

        SysUser user = userService.selectUserById(userId);
        if (user == null) {
            return error("用户不存在");
        }

        // 隐藏敏感信息
        user.setPassword(null);
        
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("user", user);
        return success().put("data", data);
    }

    /**
     * 退出登录
     */
    @Login(required = true)
    @PostMapping("/logout")
    public AjaxResult logout(HttpServletRequest request) 
    {
        // JWT token 是无状态的，客户端删除即可
        // 这里可以记录退出日志等操作
        return success("退出成功");
    }
}

