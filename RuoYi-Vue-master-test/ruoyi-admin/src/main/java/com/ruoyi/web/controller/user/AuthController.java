package com.ruoyi.web.controller.user;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.service.IForumTokenService;
import com.ruoyi.common.utils.JwtUtil;

/**
 * 用户认证控制器（基于 Mobile Terminal 逻辑）
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@RestController
@RequestMapping("/user/auth")
public class AuthController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IForumTokenService forumTokenService;

    /**
     * 用户登录
     */
    @Anonymous
    @PostMapping("/login")
    public AjaxResult login(@Valid @RequestBody LoginRequest request) 
    {
        try 
        {
            // 查询用户
            SysUser user = userService.selectUserByUserName(request.getUsername());
            
            if (user == null) 
            {
                return error("用户名或密码错误");
            }
            
            // 检查密码
            if (user.getPassword() == null || user.getPassword().isEmpty()) 
            {
                return error("用户密码未设置，请联系管理员");
            }
            
            // 验证密码（RuoYi 使用 BCrypt 加密）
            boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
            
            if (!passwordMatches) 
            {
                return error("用户名或密码错误");
            }
            
            // 生成 JWT Token
            String token = jwtUtil.generateToken(user.getUserId(), user.getUserName());
            
            // 将 JWT token 保存到数据库，供论坛接口使用
            // 这样论坛拦截器可以通过 Forum-Token 头获取用户ID
            try {
                // 先删除用户旧的 token（单设备登录）
                forumTokenService.deleteTokensByUserId(user.getUserId());
                
                // 直接保存 JWT token 到数据库
                // 计算过期时间（120分钟后，与 JWT token 过期时间一致）
                Date expireTime = new Date(System.currentTimeMillis() + 120 * 60 * 1000);
                forumTokenService.saveToken(user.getUserId(), token, expireTime);
            } catch (Exception e) {
                logger.warn("保存论坛token失败，但不影响登录: " + e.getMessage());
                // 即使保存失败，也继续返回 token，前端可以使用
            }
            
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("token", token);
            data.put("userId", user.getUserId());
            data.put("username", user.getUserName());
            data.put("nickname", user.getNickName());
            
            return success("登录成功").put("data", data);
        } 
        catch (Exception e) 
        {
            logger.error("登录失败", e);
            return error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @Anonymous
    @PostMapping("/register")
    public AjaxResult register(@Valid @RequestBody RegisterRequest request) 
    {
        try 
        {
            // 检查用户名是否已存在
            SysUser existingUser = userService.selectUserByUserName(request.getUsername());
            if (existingUser != null) 
            {
                return error("用户名已存在");
            }
            
            // 创建新用户
            SysUser user = new SysUser();
            user.setUserName(request.getUsername());
            user.setNickName(request.getNickname());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setStatus("0"); // 正常状态
            
            int result = userService.insertUser(user);
            
            if (result > 0) 
            {
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("userId", user.getUserId());
                return success("注册成功").put("data", data);
            } 
            else 
            {
                return error("注册失败");
            }
        } 
        catch (Exception e) 
        {
            logger.error("注册失败", e);
            return error("注册失败: " + e.getMessage());
        }
    }

    /**
     * 登录请求 DTO
     */
    public static class LoginRequest 
    {
        private String username;
        private String password;

        public String getUsername() 
        {
            return username;
        }

        public void setUsername(String username) 
        {
            this.username = username;
        }

        public String getPassword() 
        {
            return password;
        }

        public void setPassword(String password) 
        {
            this.password = password;
        }
    }

    /**
     * 注册请求 DTO
     */
    public static class RegisterRequest 
    {
        private String username;
        private String password;
        private String nickname;

        public String getUsername() 
        {
            return username;
        }

        public void setUsername(String username) 
        {
            this.username = username;
        }

        public String getPassword() 
        {
            return password;
        }

        public void setPassword(String password) 
        {
            this.password = password;
        }

        public String getNickname() 
        {
            return nickname;
        }

        public void setNickname(String nickname) 
        {
            this.nickname = nickname;
        }
    }
}

