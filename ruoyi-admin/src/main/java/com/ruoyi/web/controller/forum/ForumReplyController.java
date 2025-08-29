package com.ruoyi.web.controller.forum;

import java.util.List;
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
import com.ruoyi.system.domain.ForumReply;
import com.ruoyi.system.service.IForumReplyService;
import com.ruoyi.system.service.IForumPostService;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 论坛回复Controller
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@RestController
@RequestMapping("/forum/posts")
public class ForumReplyController extends BaseController
{
    @Autowired
    private IForumReplyService forumReplyService;

    @Autowired
    private IForumPostService forumPostService;

    /**
     * 查询论坛回复列表
     */
    @GetMapping("/{postId}/comments")
    public AjaxResult list(@PathVariable("postId") Long postId)
    {
        List<ForumReply> list = forumReplyService.selectForumReplyTreeByPostId(postId);
        return success(list);
    }

    /**
     * 获取论坛回复详细信息
     */
    @GetMapping("/comments/{replyId}")
    public AjaxResult getInfo(@PathVariable("replyId") Long replyId)
    {
        return success(forumReplyService.selectForumReplyByReplyId(replyId));
    }

    /**
     * 新增论坛回复
     */
    @Log(title = "论坛回复", businessType = BusinessType.INSERT)
    @PostMapping("/{postId}/comments")
    public AjaxResult add(@PathVariable("postId") Long postId, @RequestBody ForumReply forumReply)
    {
        forumReply.setPostId(postId);
        forumReply.setUserId(SecurityUtils.getUserId());
        forumReply.setCreateBy(SecurityUtils.getUsername());
        
        int result = forumReplyService.insertForumReply(forumReply);
        
        if (result > 0) {
            // 增加帖子回复数
            forumPostService.increaseReplyCount(postId);
            
            // 更新帖子最后回复信息
            ForumReply newReply = forumReplyService.selectForumReplyByReplyId(forumReply.getReplyId());
            if (newReply != null) {
                // 这里可以更新帖子的最后回复时间和最后回复用户ID
                // 暂时简化处理
            }
        }
        
        return toAjax(result);
    }

    /**
     * 修改论坛回复
     */
    @Log(title = "论坛回复", businessType = BusinessType.UPDATE)
    @PutMapping("/comments")
    public AjaxResult edit(@RequestBody ForumReply forumReply)
    {
        forumReply.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(forumReplyService.updateForumReply(forumReply));
    }

    /**
     * 删除论坛回复
     */
    @Log(title = "论坛回复", businessType = BusinessType.DELETE)
	@DeleteMapping("/comments/{replyIds}")
    public AjaxResult remove(@PathVariable Long[] replyIds)
    {
        return toAjax(forumReplyService.deleteForumReplyByReplyIds(replyIds));
    }

    /**
     * 点赞回复
     */
    @PostMapping("/comments/{replyId}/like")
    public AjaxResult likeReply(@PathVariable("replyId") Long replyId)
    {
        Long userId = SecurityUtils.getUserId();
        int result = forumReplyService.likeReply(replyId, userId);
        if (result > 0) {
            return success("点赞成功");
        } else {
            return error("您已经点赞过了");
        }
    }

    /**
     * 取消点赞回复
     */
    @PostMapping("/comments/{replyId}/unlike")
    public AjaxResult unlikeReply(@PathVariable("replyId") Long replyId)
    {
        Long userId = SecurityUtils.getUserId();
        int result = forumReplyService.unlikeReply(replyId, userId);
        if (result > 0) {
            return success("取消点赞成功");
        } else {
            return error("操作失败");
        }
    }
}
