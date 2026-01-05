package com.ruoyi.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

/**
 * JWT 工具类（基于 Mobile Terminal）
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@Component
public class JwtUtil 
{
    @Value("${jwt.secret:ruoyi-vue-master-test-secret-key-for-jwt-token-generation}")
    private String secret;

    @Value("${jwt.expire-minutes:120}")
    private long expireMinutes;

    /**
     * 生成 Token
     */
    public String generateToken(Long userId, String username) 
    {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireMinutes * 60 * 1000);
        return Jwts.builder()
                .setSubject(username)
                .claim("uid", userId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token
     */
    public Claims parseToken(String token) 
    {
        try 
        {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } 
        catch (Exception e) 
        {
            return null;
        }
    }

    /**
     * 提取用户ID
     */
    public Optional<Long> extractUserId(String token) 
    {
        Claims claims = parseToken(token);
        if (claims == null) 
        {
            return Optional.empty();
        }
        Long userId = claims.get("uid", Long.class);
        return Optional.ofNullable(userId);
    }

    /**
     * 获取签名密钥
     */
    private Key signingKey() 
    {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}

