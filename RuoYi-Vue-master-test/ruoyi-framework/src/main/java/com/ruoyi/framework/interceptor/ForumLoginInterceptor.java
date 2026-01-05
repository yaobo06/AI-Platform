package com.ruoyi.framework.interceptor;

import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.annotation.Login;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.IForumTokenService;

/**
 * 论坛登录拦截器
 * 
 * @author ruoyi
 */
@Component
public class ForumLoginInterceptor implements HandlerInterceptor
{
    @Autowired
    private IForumTokenService forumTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        // 只拦截论坛相关的接口
        String requestURI = request.getRequestURI();
        if (!requestURI.startsWith("/forum"))
        {
            return true;
        }

        if (handler instanceof HandlerMethod)
        {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            
            // 检查方法上的@Login注解
            Login loginAnnotation = method.getAnnotation(Login.class);
            if (loginAnnotation == null)
            {
                // 检查类上的@Login注解
                loginAnnotation = handlerMethod.getBeanType().getAnnotation(Login.class);
            }

            if (loginAnnotation != null)
            {
                boolean required = loginAnnotation.required();
                
                // 获取token（无论required是true还是false，都尝试获取）
                String token = getToken(request);
                
                if (required)
                {
                    // 需要登录，验证token
                    if (StringUtils.isEmpty(token))
                    {
                        AjaxResult ajaxResult = AjaxResult.error(401, "未登录或登录已过期，请重新登录");
                        ServletUtils.renderString(response, JSON.toJSONString(ajaxResult));
                        return false;
                    }

                    // 验证token有效性
                    Long userId = forumTokenService.getUserIdByToken(token);
                    if (userId == null)
                    {
                        AjaxResult ajaxResult = AjaxResult.error(401, "登录已过期，请重新登录");
                        ServletUtils.renderString(response, JSON.toJSONString(ajaxResult));
                        return false;
                    }

                    // 将userId存入request，供Controller使用
                    request.setAttribute("forumUserId", userId);
                }
                else
                {
                    // required=false时，如果提供了token，也验证并设置userId（用于检查点赞状态等）
                    if (!StringUtils.isEmpty(token))
                    {
                        Long userId = forumTokenService.getUserIdByToken(token);
                        if (userId != null)
                        {
                            request.setAttribute("forumUserId", userId);
                        }
                    }
                }
            }
        }
        
        return true;
    }

    /**
     * 获取请求的token
     */
    private String getToken(HttpServletRequest request)
    {
        String token = request.getHeader("Forum-Token");
        if (StringUtils.isEmpty(token))
        {
            token = request.getParameter("forumToken");
        }
        return token;
    }
}

