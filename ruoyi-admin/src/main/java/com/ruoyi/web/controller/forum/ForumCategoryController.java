package com.ruoyi.web.controller.forum;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.ForumCategory;
import com.ruoyi.system.service.IForumCategoryService;
import com.ruoyi.common.annotation.Anonymous;

/**
 * 论坛分类Controller
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@RestController
@RequestMapping("/forum")
public class ForumCategoryController extends BaseController
{
    @Autowired
    private IForumCategoryService forumCategoryService;

    /**
     * 获取论坛分类列表
     */
    @Anonymous
    @GetMapping("/categories")
    public AjaxResult list()
    {
        List<ForumCategory> list = forumCategoryService.selectForumCategoryList(new ForumCategory());
        return success(list);
    }
}
