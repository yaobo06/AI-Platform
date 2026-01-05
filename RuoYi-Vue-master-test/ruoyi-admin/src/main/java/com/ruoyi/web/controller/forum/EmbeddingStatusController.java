package com.ruoyi.web.controller.forum;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IEmbeddingService;

/**
 * Embedding 服务状态检测控制器
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@RestController
@RequestMapping("/forum/embedding")
public class EmbeddingStatusController extends BaseController
{
    @Autowired
    private IEmbeddingService embeddingService;

    @Value("${embeddings.url:${deepseek.embeddings.url:http://localhost:8083/embeddings}}")
    private String embeddingsUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 检测 Embedding 服务状态
     */
    @Anonymous
    @GetMapping("/status")
    public AjaxResult getStatus() 
    {
        Map<String, Object> status = new HashMap<String, Object>();
        
        // 1. 检查服务 URL 配置
        status.put("service_url", embeddingsUrl);
        status.put("is_local", embeddingsUrl != null && 
            (embeddingsUrl.contains("localhost") || embeddingsUrl.contains("127.0.0.1")));
        
        // 2. 检查 Python 服务是否可访问
        boolean serviceAvailable = false;
        String serviceStatus = "unknown";
        String serviceMessage = "";
        
        try 
        {
            // 尝试访问健康检查端点
            String healthUrl = embeddingsUrl.replace("/embeddings", "/health");
            ResponseEntity<Map> response = restTemplate.getForEntity(healthUrl, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) 
            {
                Map<String, Object> healthData = response.getBody();
                Boolean modelLoaded = (Boolean) healthData.get("model_loaded");
                serviceAvailable = modelLoaded != null && modelLoaded;
                serviceStatus = serviceAvailable ? "healthy" : "model_not_loaded";
                serviceMessage = serviceAvailable ? "模型已加载" : "服务运行中但模型未加载";
            }
        } 
        catch (Exception e) 
        {
            serviceStatus = "unavailable";
            serviceMessage = "无法连接到服务: " + e.getMessage();
        }
        
        status.put("service_available", serviceAvailable);
        status.put("service_status", serviceStatus);
        status.put("service_message", serviceMessage);
        
        // 3. 测试实际调用（可选，可能较慢）
        boolean testCallSuccess = false;
        String testMessage = "";
        
        if (serviceAvailable) 
        {
            try 
            {
                float[] testVector = embeddingService.generateEmbedding("测试", false);
                if (testVector != null && testVector.length > 0) 
                {
                    testCallSuccess = true;
                    testMessage = String.format("测试成功，向量维度: %d", testVector.length);
                } 
                else 
                {
                    testMessage = "测试失败：返回向量为空";
                }
            } 
            catch (Exception e) 
            {
                testMessage = "测试失败: " + e.getMessage();
            }
        } 
        else 
        {
            testMessage = "服务不可用，跳过测试";
        }
        
        status.put("test_call_success", testCallSuccess);
        status.put("test_message", testMessage);
        
        // 4. 总体状态
        boolean overallAvailable = serviceAvailable && testCallSuccess;
        status.put("available", overallAvailable);
        status.put("recommendation", overallAvailable ? 
            "✅ Embedding 服务正常，可以使用 RAG 功能" : 
            "⚠️ Embedding 服务不可用，RAG 功能将降级到数据库搜索");
        
        return success().put("data", status);
    }

    /**
     * 快速健康检查（不进行实际调用测试）
     */
    @Anonymous
    @GetMapping("/health")
    public AjaxResult health() 
    {
        Map<String, Object> health = new HashMap<String, Object>();
        
        try 
        {
            String healthUrl = embeddingsUrl.replace("/embeddings", "/health");
            ResponseEntity<Map> response = restTemplate.getForEntity(healthUrl, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) 
            {
                health.put("status", "healthy");
                health.put("details", response.getBody());
            } 
            else 
            {
                health.put("status", "unhealthy");
                health.put("details", "HTTP " + response.getStatusCode());
            }
        } 
        catch (Exception e) 
        {
            health.put("status", "unavailable");
            health.put("error", e.getMessage());
        }
        
        return success().put("data", health);
    }
}

