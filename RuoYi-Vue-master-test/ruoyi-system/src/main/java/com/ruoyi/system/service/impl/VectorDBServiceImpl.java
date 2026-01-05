package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruoyi.system.domain.SearchResult;
import com.ruoyi.system.service.IVectorDBService;

import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.ConnectParam;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.response.SearchResultsWrapper;

/**
 * Milvus 向量数据库服务实现（完整版）
 * 
 * @author ruoyi
 * @date 2025-01-24
 */
@Service
public class VectorDBServiceImpl implements IVectorDBService 
{
    private static final Logger log = LoggerFactory.getLogger(VectorDBServiceImpl.class);

    @Value("${milvus.lite.enabled:false}")
    private boolean enabled;

    @Value("${milvus.lite.host:localhost}")
    private String host;

    @Value("${milvus.lite.port:19530}")
    private int port;

    @Value("${milvus.lite.collection-name:posts_vectors}")
    private String collectionName;

    @Value("${milvus.lite.dimension:768}")
    private int dimension;

    @Value("${milvus.lite.metric-type:IP}")
    private String metricType;

    private MilvusServiceClient client;
    private boolean initialized = false;
    private final String ID_FIELD = "post_id";
    private final String VECTOR_FIELD = "vector";

    @PostConstruct
    public void init() 
    {
        if (!enabled) 
        {
            log.info("Milvus 向量数据库已禁用");
            return;
        }

        try 
        {
            // 连接 Milvus Standalone 服务
            ConnectParam connectParam = ConnectParam.newBuilder()
                .withHost(host)
                .withPort(port)
                .build();
            client = new MilvusServiceClient(connectParam);
            
            // 测试连接（快速失败，避免长时间重试）
            R<Boolean> testResult = client.hasCollection(
                HasCollectionParam.newBuilder()
                    .withCollectionName("_test_connection_")
                    .build()
            );
            
            if (testResult.getStatus() != R.Status.Success.getCode()) 
            {
                throw new RuntimeException("Milvus 连接测试失败，状态码: " + testResult.getStatus());
            }

            // 检查集合是否存在，不存在则创建
            if (!hasCollection()) 
            {
                createCollection();
            }

            // 确保索引存在
            ensureIndex();

            initialized = true;
            log.info("Milvus 向量数据库初始化成功 - Host: {}, Port: {}, Collection: {}", host, port, collectionName);
        } 
        catch (Exception e) 
        {
            initialized = false;
            client = null;
            // 只记录一次警告，避免日志噪音
            log.warn("Milvus 向量数据库初始化失败: {}。RAG 功能将不可用，请启动 Milvus 服务（执行 ./milvus-start.sh）。应用将继续启动，但向量搜索功能将降级到关键词搜索。", 
                e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            initialized = false;
            log.warn("Milvus 初始化失败: {}，向量搜索功能将自动降级到关键词搜索模式", e.getMessage());
        }
    }

    @PreDestroy
    public void destroy() 
    {
        if (client != null) 
        {
            try 
            {
                client.close();
                log.info("Milvus 客户端已关闭");
            } 
            catch (Exception e) 
            {
                log.warn("关闭 Milvus 客户端失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 检查集合是否存在
     */
    private boolean hasCollection() 
    {
        R<Boolean> response = client.hasCollection(
            HasCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build()
        );
        return response.getData() != null && response.getData();
    }

    /**
     * 创建集合
     */
    private void createCollection() 
    {
        // 定义字段
        FieldType idField = FieldType.newBuilder()
            .withName(ID_FIELD)
            .withDataType(DataType.Int64)
            .withPrimaryKey(true)
            .withAutoID(false)
            .build();

        FieldType vectorField = FieldType.newBuilder()
            .withName(VECTOR_FIELD)
            .withDataType(DataType.FloatVector)
            .withDimension(dimension)
            .build();

        // 创建集合
        CreateCollectionParam createParam = CreateCollectionParam.newBuilder()
            .withCollectionName(collectionName)
            .withDescription("帖子向量集合")
            .withShardsNum(2)
            .addFieldType(idField)
            .addFieldType(vectorField)
            .build();

        R<RpcStatus> response = client.createCollection(createParam);
        if (response.getStatus() != R.Status.Success.getCode()) 
        {
            throw new RuntimeException("创建集合失败: " + response.getMessage());
        }
        log.info("Milvus 集合创建成功: {}", collectionName);
    }

    /**
     * 确保索引存在
     */
    private void ensureIndex() 
    {
        try 
        {
            MetricType metric = "IP".equalsIgnoreCase(metricType) ? MetricType.IP : MetricType.L2;
            
            CreateIndexParam indexParam = CreateIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withFieldName(VECTOR_FIELD)
                .withIndexType(IndexType.IVF_FLAT)
                .withMetricType(metric)
                .withExtraParam("{\"nlist\":1024}")
                .build();

            R<RpcStatus> response = client.createIndex(indexParam);
            if (response.getStatus() == R.Status.Success.getCode()) 
            {
                log.info("Milvus 索引创建成功");
            }
        } 
        catch (Exception e) 
        {
            // 索引可能已存在，忽略异常
            log.debug("索引可能已存在: {}", e.getMessage());
        }
    }

    @Override
    public boolean isAvailable() 
    {
        return enabled && initialized && client != null;
    }

    @Override
    public void insertVector(Long postId, float[] vector) 
    {
        if (!isAvailable()) 
        {
            return;
        }

        try 
        {
            List<Long> ids = Collections.singletonList(postId);
            // 手动转换 float[] 到 List<Float>
            List<Float> vectorList = new ArrayList<Float>();
            for (float v : vector) 
            {
                vectorList.add(v);
            }
            List<List<Float>> vectors = Collections.singletonList(vectorList);

            InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(
                    Arrays.asList(
                        new InsertParam.Field(ID_FIELD, ids),
                        new InsertParam.Field(VECTOR_FIELD, vectors)
                    )
                )
                .build();

            R<?> response = client.insert(insertParam);
            if (response.getStatus() == R.Status.Success.getCode()) 
            {
                log.debug("向量插入成功 - Post ID: {}, 维度: {}", postId, vector.length);
            } 
            else 
            {
                log.warn("向量插入失败 - Post ID: {}, 错误: {}", postId, response.getMessage());
            }
        } 
        catch (Exception e) 
        {
            log.warn("向量插入异常 - Post ID: {}, 错误: {}", postId, e.getMessage());
        }
    }

    @Override
    public void updateVector(Long postId, float[] vector) 
    {
        deleteVector(postId);
        insertVector(postId, vector);
    }

    @Override
    public void deleteVector(Long postId) 
    {
        if (!enabled || client == null) 
        {
            return;
        }

        try 
        {
            // 加载集合
            client.loadCollection(
                LoadCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build()
            );

            // 使用 delete API 删除向量
            // 注意：需要先加载集合才能删除
            String expr = ID_FIELD + " == " + postId;
            DeleteParam deleteParam = DeleteParam.newBuilder()
                .withCollectionName(collectionName)
                .withExpr(expr)
                .build();
            
            R<?> response = client.delete(deleteParam);
            if (response.getStatus() == R.Status.Success.getCode()) 
            {
                log.debug("向量删除成功 - Post ID: {}", postId);
            }
        } 
        catch (Exception e) 
        {
            log.warn("向量删除异常 - Post ID: {}, 错误: {}", postId, e.getMessage());
        }
    }

    @Override
    public List<SearchResult> search(float[] queryVector, int topK) 
    {
        if (!isAvailable()) 
        {
            return Collections.emptyList();
        }

        try 
        {
            // 确保集合已加载
            client.loadCollection(
                LoadCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build()
            );

            // 构建搜索参数（手动转换 float[] 到 List<Float>）
            List<Float> queryVectorList = new ArrayList<Float>();
            for (float v : queryVector) 
            {
                queryVectorList.add(v);
            }
            List<List<Float>> searchVectors = Collections.singletonList(queryVectorList);

            // 根据配置选择相似度度量方法
            MetricType metric = "IP".equalsIgnoreCase(metricType) ? MetricType.IP : MetricType.L2;
            
            SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withMetricType(metric)
                .withOutFields(Collections.singletonList(ID_FIELD))
                .withTopK(topK)
                .withVectors(searchVectors)
                .withVectorFieldName(VECTOR_FIELD)
                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                .build();

            R<?> response = client.search(searchParam);
            
            if (response.getStatus() != R.Status.Success.getCode()) 
            {
                log.warn("向量搜索失败: {}", response.getMessage());
                return Collections.emptyList();
            }

            // 解析搜索结果
            Object searchData = response.getData();
            if (searchData == null) 
            {
                return Collections.emptyList();
            }
            
            // 使用正确的类型转换
            io.milvus.grpc.SearchResults searchResults = (io.milvus.grpc.SearchResults) searchData;
            SearchResultsWrapper wrapper = new SearchResultsWrapper(searchResults.getResults());
            List<SearchResult> results = new ArrayList<SearchResult>();

            if (wrapper.getIDScore(0) != null && !wrapper.getIDScore(0).isEmpty()) 
            {
                for (int i = 0; i < wrapper.getIDScore(0).size(); i++) 
                {
                    Long postId = wrapper.getIDScore(0).get(i).getLongID();
                    Float score = wrapper.getIDScore(0).get(i).getScore();
                    
                    // 根据使用的距离度量类型转换分数
                    float similarity;
                    if ("IP".equalsIgnoreCase(metricType)) 
                    {
                        // IP（内积）：值越大越相似
                        // 如果向量已归一化，IP 等价于余弦相似度（范围 -1 到 1）
                        // 转换为 0-1 范围：similarity = (score + 1) / 2
                        if (score >= -1.0f && score <= 1.0f) 
                        {
                            similarity = (score + 1.0f) / 2.0f;  // 将 -1~1 映射到 0~1
                        } 
                        else 
                        {
                            // 如果 score 已经是正数且较大，可能是未归一化的内积
                            similarity = Math.min(1.0f, Math.max(0.0f, score / 10.0f));  // 简单归一化
                        }
                    } 
                    else 
                    {
                        // L2（欧氏距离）：值越小越相似，转换为相似度
                        similarity = 1.0f / (1.0f + score);
                    }
                    
                    results.add(new SearchResult(postId, similarity));
                }
            }

            log.debug("向量搜索完成 - 找到 {} 条结果", results.size());
            return results;
        } 
        catch (Exception e) 
        {
            log.error("向量搜索异常: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
