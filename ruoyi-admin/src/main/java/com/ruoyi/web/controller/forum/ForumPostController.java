package com.ruoyi.web.controller.forum;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.ForumPost;
import com.ruoyi.system.service.IForumPostService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.annotation.Anonymous;

/**
 * 论坛帖子Controller
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@RestController
@RequestMapping("/forum/posts")
public class ForumPostController extends BaseController
{
    @Autowired
    private IForumPostService forumPostService;

    /**
     * 查询论坛帖子列表
     */
    @Anonymous
    @GetMapping("/list") //list查询mysql数据库
    public TableDataInfo list(ForumPost forumPost)
    {
        startPage();
        List<ForumPost> list = forumPostService.selectForumPostList(forumPost);
        return getDataTable(list); //将查询到的结果格式化并分页展示
    }

    /**
     * 导出论坛帖子列表
     */
    @Log(title = "论坛帖子", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ForumPost forumPost)
    {
        List<ForumPost> list = forumPostService.selectForumPostList(forumPost);
        ExcelUtil<ForumPost> util = new ExcelUtil<ForumPost>(ForumPost.class);
        util.exportExcel(response, list, "论坛帖子数据");
    }

    /**
     * 获取论坛帖子详细信息
     */
    @Anonymous
    @GetMapping(value = "/{postId}")
    public AjaxResult getInfo(@PathVariable("postId") Long postId)
    {
        // 增加浏览次数
        forumPostService.increaseViewCount(postId);
        
        ForumPost forumPost = forumPostService.selectForumPostByPostId(postId);
        
        // 检查当前用户是否点赞（只有登录用户才检查）
        if (forumPost != null) {
            try {
                Long userId = SecurityUtils.getUserId();
                boolean isLiked = forumPostService.isLikedByUser(postId, userId);
                forumPost.setRemark(isLiked ? "1" : "0"); // 使用remark字段传递点赞状态
            } catch (Exception e) {
                // 用户未登录，设置默认未点赞状态
                forumPost.setRemark("0");
            }
        }
        
        return success(forumPost);
    }

    /**
     * 新增论坛帖子
     */
    @Log(title = "论坛帖子", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ForumPost forumPost)
    {
        forumPost.setUserId(SecurityUtils.getUserId());
        forumPost.setCreateBy(SecurityUtils.getUsername());
        return toAjax(forumPostService.insertForumPost(forumPost));
    }

    /**
     * 修改论坛帖子
     */
    @Log(title = "论坛帖子", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ForumPost forumPost)
    {
        forumPost.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(forumPostService.updateForumPost(forumPost));
    }

    /**
     * 删除论坛帖子
     */
    @Log(title = "论坛帖子", businessType = BusinessType.DELETE)
	@DeleteMapping("/{postIds}")
    public AjaxResult remove(@PathVariable Long[] postIds)
    {
        return toAjax(forumPostService.deleteForumPostByPostIds(postIds));
    }

    /**
     * 点赞帖子
     */
    @PostMapping("/{postId}/like")
    public AjaxResult likePost(@PathVariable("postId") Long postId)
    {
        Long userId = SecurityUtils.getUserId();
        int result = forumPostService.likePost(postId, userId);
        if (result > 0) {
            return success("点赞成功");
        } else {
            return error("您已经点赞过了");
        }
    }

    /**
     * 取消点赞帖子
     */
    @PostMapping("/{postId}/unlike")
    public AjaxResult unlikePost(@PathVariable("postId") Long postId)
    {
        Long userId = SecurityUtils.getUserId();
        int result = forumPostService.unlikePost(postId, userId);
        if (result > 0) {
            return success("取消点赞成功");
        } else {
            return error("操作失败");
        }
    }
}
