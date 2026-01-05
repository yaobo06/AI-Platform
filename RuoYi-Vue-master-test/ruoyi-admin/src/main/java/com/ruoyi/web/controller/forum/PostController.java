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
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.Comment;
import com.ruoyi.system.domain.Post;
import com.ruoyi.system.domain.ForumCategory;
import com.ruoyi.system.mapper.PostMapper;
import com.ruoyi.system.mapper.ForumCategoryMapper;
import com.ruoyi.system.service.ICommentService;
import com.ruoyi.system.service.IPostService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 帖子Controller (来自 Mobile Terminal，适配前端接口)
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@RestController
@RequestMapping("/forum/posts")
public class PostController extends BaseController
{
    @Autowired
    private IPostService postService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ForumCategoryMapper forumCategoryMapper;

    /**
     * 创建帖子
     */
    @Login(required = true)
    @PostMapping
    public AjaxResult createPost(HttpServletRequest request, @RequestBody Map<String, Object> body) 
    {
        Long userId = (Long) request.getAttribute("forumUserId");
        if (userId == null) {
            return error("未登录");
        }

        String title = (String) body.get("title");
        String content = (String) body.get("content");
        
        // 支持 categoryId (数字) 或 category (字符串)
        String category = null;
        if (body.get("category") != null) {
            category = body.get("category").toString();
        } else if (body.get("categoryId") != null) {
            // 将 categoryId 转换为 category 名称
            Object categoryIdObj = body.get("categoryId");
            if (categoryIdObj instanceof Number) {
                Long categoryId = ((Number) categoryIdObj).longValue();
                category = convertCategoryIdToName(categoryId);
            } else if (categoryIdObj instanceof String) {
                try {
                    Long categoryId = Long.parseLong((String) categoryIdObj);
                    category = convertCategoryIdToName(categoryId);
                } catch (NumberFormatException e) {
                    category = (String) categoryIdObj;
                }
            }
        }

        if (title == null || title.trim().isEmpty()) {
            return error("标题不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            return error("内容不能为空");
        }
        if (category == null || category.trim().isEmpty()) {
            return error("分类不能为空");
        }

        Post post = postService.createPost(userId, title, content, category);
        return success("发布成功").put("data", post);
    }

    /**
     * 将 categoryId 转换为 category 名称
     */
    private String convertCategoryIdToName(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        // 从数据库查询分类名称
        try {
            ForumCategory category = forumCategoryMapper.selectForumCategoryByCategoryId(categoryId);
            if (category != null && category.getCategoryName() != null) {
                return category.getCategoryName();
            }
        } catch (Exception e) {
            // 如果查询失败，使用默认映射
        }
        // 默认映射关系（兼容旧数据）
        Map<Long, String> categoryMap = new HashMap<>();
        categoryMap.put(1L, "share");
        categoryMap.put(2L, "question");
        categoryMap.put(3L, "discuss");
        categoryMap.put(4L, "news");
        return categoryMap.getOrDefault(categoryId, String.valueOf(categoryId));
    }

    /**
     * 获取热门帖子列表
     */
    @Anonymous
    @GetMapping("/hot")
    public AjaxResult listHot(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size) 
    {
        PageInfo<Post> result = postService.listHot(page, size);
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getList());
        data.put("total", result.getTotal());
        data.put("current", result.getPageNum());
        data.put("size", result.getPageSize());
        return success().put("data", data);
    }

    /**
     * 获取最近帖子列表
     */
    @Anonymous
    @GetMapping("/recent")
    public AjaxResult listRecent(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size) 
    {
        PageInfo<Post> result = postService.listRecent(page, size);
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getList());
        data.put("total", result.getTotal());
        data.put("current", result.getPageNum());
        data.put("size", result.getPageSize());
        return success().put("data", data);
    }

    /**
     * 获取帖子列表（兼容前端接口）
     */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String categoryId,
                              @RequestParam(required = false) String title,
                              Post post) 
    {
        startPage();
        // 使用通用查询方法，支持分类筛选
        Post query = new Post();
        
        // 支持通过 categoryId 或 category 筛选
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            // 如果传入的是 categoryId，需要转换为 category 名称
            try {
                Long catId = Long.parseLong(categoryId);
                String categoryName = convertCategoryIdToName(catId);
                if (categoryName != null) {
                    query.setCategory(categoryName);
                }
            } catch (NumberFormatException e) {
                // 如果解析失败，直接作为 category 使用
                query.setCategory(categoryId);
            }
        } else if (post != null && post.getCategory() != null && !post.getCategory().trim().isEmpty()) {
            query.setCategory(post.getCategory());
        }
        
        // 支持标题搜索
        if (title != null && !title.trim().isEmpty()) {
            query.setTitle(title);
        } else if (post != null && post.getTitle() != null && !post.getTitle().trim().isEmpty()) {
            query.setTitle(post.getTitle());
        }
        
        List<Post> list = postMapper.selectPostList(query);
        // SQL中已按 created_at desc 排序，无需再次排序
        // 填充作者信息
        com.ruoyi.common.utils.AuthorInfoUtils.enrichWithAuthorInfo(
            list,
            Post::getUserId,
            (p, author) -> {
                p.setAuthorName(com.ruoyi.common.utils.AuthorInfoUtils.getDisplayName(author));
                p.setAuthorAvatarUrl(author.getAvatar());
            },
            userService
        );
        return getDataTable(list);
    }

    /**
     * 获取帖子详情
     */
    @Login(required = false)
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable("id") Long id,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size,
                            HttpServletRequest request) 
    {
        if (id == null) {
            return error("帖子ID不能为空");
        }
        
        Post post = postService.selectPostById(id);
        if (post == null) {
            return error("帖子不存在");
        }

        // 检查当前用户是否点赞
        Long userId = (Long) request.getAttribute("forumUserId");
        if (userId != null) {
            boolean isLiked = postService.isLiked(userId, id);
            post.setRemark(isLiked ? "1" : "0"); // 使用remark字段传递点赞状态
        } else {
            post.setRemark("0");
        }

        // 获取评论列表
        PageInfo<Comment> comments = commentService.listComments(id, page, size);

        Map<String, Object> payload = new HashMap<>();
        payload.put("post", post);
        payload.put("comments", comments);

        return success().put("data", payload);
    }

    /**
     * 点赞帖子
     */
    @Login(required = true)
    @PostMapping("/{id}/like")
    public AjaxResult like(HttpServletRequest request, @PathVariable("id") Long id) 
    {
        Long userId = (Long) request.getAttribute("forumUserId");
        if (userId == null) {
            return error("未登录");
        }

        boolean liked = postService.like(userId, id);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        return success(liked ? "已点赞" : "已取消点赞").put("data", result);
    }

    /**
     * 取消点赞（兼容前端接口）
     */
    @Login(required = true)
    @PostMapping("/{id}/unlike")
    public AjaxResult unlike(HttpServletRequest request, @PathVariable("id") Long id) 
    {
        Long userId = (Long) request.getAttribute("forumUserId");
        if (userId == null) {
            return error("未登录");
        }

        postService.like(userId, id); // 再次调用会取消点赞
        Map<String, Object> result = new HashMap<>();
        result.put("liked", false);
        return success("已取消点赞").put("data", result);
    }

    /**
     * 收藏帖子
     */
    @Login(required = true)
    @PostMapping("/{id}/favorite")
    public AjaxResult favorite(HttpServletRequest request, @PathVariable("id") Long id) 
    {
        Long userId = (Long) request.getAttribute("forumUserId");
        if (userId == null) {
            return error("未登录");
        }

        boolean favorited = postService.favorite(userId, id);
        Map<String, Object> result = new HashMap<>();
        result.put("favorited", favorited);
        return success(favorited ? "已收藏" : "已取消收藏").put("data", result);
    }

    /**
     * 检查用户是否已点赞
     */
    @Login(required = true)
    @GetMapping("/{id}/like/status")
    public AjaxResult getLikeStatus(HttpServletRequest request, @PathVariable("id") Long id) 
    {
        Long userId = (Long) request.getAttribute("forumUserId");
        if (userId == null) {
            return error("未登录");
        }

        boolean isLiked = postService.isLiked(userId, id);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", isLiked);
        return success().put("data", result);
    }

    /**
     * 检查用户是否已收藏
     */
    @Login(required = true)
    @GetMapping("/{id}/favorite/status")
    public AjaxResult getFavoriteStatus(HttpServletRequest request, @PathVariable("id") Long id) 
    {
        Long userId = (Long) request.getAttribute("forumUserId");
        if (userId == null) {
            return error("未登录");
        }

        boolean isFavorited = postService.isFavorited(userId, id);
        Map<String, Object> result = new HashMap<>();
        result.put("favorited", isFavorited);
        return success().put("data", result);
    }
}

