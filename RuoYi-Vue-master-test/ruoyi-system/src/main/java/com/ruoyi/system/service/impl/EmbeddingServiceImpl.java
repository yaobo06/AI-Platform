package com.ruoyi.system.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.IEmbeddingService;

/**
 * 向量生成服务实现
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@Service
public class EmbeddingServiceImpl implements IEmbeddingService 
{
    private static final Logger log = LoggerFactory.getLogger(EmbeddingServiceImpl.class);

    @Value("${embeddings.api.key:}")
    private String embeddingApiKey;

    @Value("${deepseek.api.key:}")
    private String deepseekApiKey;

    @Value("${embeddings.url:${deepseek.embeddings.url:https://api.deepseek.com/v1/embeddings}}")
    private String embeddingsUrl;

    @Value("${embeddings.model:${deepseek.embeddings.model:deepseek-embedding}}")
    private String model;

    @Value("${embeddings.dimension:${deepseek.embeddings.dimension:768}}")
    private int dimension;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public float[] generateEmbedding(String text) 
    {
        return generateEmbedding(text, false);
    }

    @Override
    public float[] generateEmbedding(String text, boolean throwException) 
    {
        if (StringUtils.isEmpty(text)) 
        {
            if (throwException) 
            {
                throw new IllegalStateException("文本为空");
            }
            return null;
        }

        String apiKey = StringUtils.isNotEmpty(embeddingApiKey) ? embeddingApiKey : deepseekApiKey;
        
        boolean isLocalService = embeddingsUrl != null && 
            (embeddingsUrl.contains("localhost") || embeddingsUrl.contains("127.0.0.1"));
        
        if (!isLocalService && StringUtils.isEmpty(apiKey)) 
        {
            log.warn("Embeddings API Key 未配置，无法生成向量");
            if (throwException) 
            {
                throw new IllegalStateException("API Key 未配置");
            }
            return null;
        }

        try 
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (StringUtils.isNotEmpty(apiKey)) 
            {
                headers.set("Authorization", "Bearer " + apiKey);
            }

            Map<String, Object> requestBody = new HashMap<String, Object>();
            requestBody.put("model", model);
            requestBody.put("input", text);

            HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(embeddingsUrl, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) 
            {
                log.error("Embeddings API 调用失败，状态码: {}", response.getStatusCode());
                if (throwException) 
                {
                    throw new RuntimeException("API 调用失败: " + response.getStatusCode());
                }
                return null;
            }

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            if (jsonNode.has("error")) 
            {
                String errorMsg = jsonNode.get("error").toString();
                log.error("Embeddings API 返回错误: {}", errorMsg);
                if (throwException) 
                {
                    throw new RuntimeException("API 错误: " + errorMsg);
                }
                return null;
            }
            
            if (jsonNode.has("data") && jsonNode.get("data").isArray() && jsonNode.get("data").size() > 0) 
            {
                JsonNode embeddingNode = jsonNode.get("data").get(0).get("embedding");
                if (embeddingNode != null && embeddingNode.isArray()) 
                {
                    float[] vector = new float[embeddingNode.size()];
                    for (int i = 0; i < embeddingNode.size(); i++) 
                    {
                        vector[i] = (float) embeddingNode.get(i).asDouble();
                    }
                    log.debug("成功生成向量，维度: {}", vector.length);
                    return vector;
                }
            }

            log.warn("无法从响应中提取向量");
            if (throwException) 
            {
                throw new RuntimeException("无法从响应中提取向量");
            }
            return null;
        } 
        catch (org.springframework.web.client.HttpClientErrorException e) 
        {
            int statusCode = e.getStatusCode().value();
            log.error("Embeddings API HTTP 错误: {} - {}", statusCode, e.getMessage());
            
            if (statusCode == 401) 
            {
                log.warn("提示：API Key 可能无效或已过期");
            } 
            else if (statusCode == 402) 
            {
                log.warn("提示：账户余额不足，请充值");
            } 
            else if (statusCode == 404) 
            {
                log.warn("提示：Embeddings API 可能不存在，请检查 URL 和模型名称");
            }
            
            if (throwException) 
            {
                throw new RuntimeException("API 调用失败: " + e.getMessage(), e);
            }
            return null;
        } 
        catch (Exception e) 
        {
            log.error("生成向量失败: {}", e.getMessage(), e);
            if (throwException) 
            {
                throw new RuntimeException("生成向量失败: " + e.getMessage(), e);
            }
            return null;
        }
    }

    @Override
    public int getDimension() 
    {
        return dimension;
    }
}

