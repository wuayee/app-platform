/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.community.vectorestore.milvus;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.StringUtils.isNotBlank;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.UuidUtils;
import com.huawei.jade.fel.community.vectorestore.milvus.config.MilvusSchemaConfig;
import com.huawei.jade.fel.core.document.Document;
import com.huawei.jade.fel.core.document.DocumentEmbedModel;
import com.huawei.jade.fel.core.document.MeasurableDocument;
import com.huawei.jade.fel.core.embed.Embedding;
import com.huawei.jade.fel.core.retriever.filter.ExpressionParser;
import com.huawei.jade.fel.core.vectorstore.SearchOption;
import com.huawei.jade.fel.core.vectorstore.VectorStore;

import com.alibaba.fastjson.JSONObject;

import io.milvus.client.MilvusClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.DescribeIndexResponse;
import io.milvus.grpc.GetLoadStateResponse;
import io.milvus.grpc.LoadState;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.CollectionSchemaParam;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.GetLoadStateParam;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.index.DescribeIndexParam;
import io.milvus.response.SearchResultsWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

/**
 * 表示 {@link VectorStore} 的 milvus 实现。
 *
 * @author 易文渊
 * @see <a href="https://milvus.io/">milvus</a>
 * @since 2024-08-10
 */
@Component
public class MilvusVectorStore implements VectorStore {
    static final String DOC_ID_FIELD_NAME = "doc_id";
    static final String TEXT_FIELD_NAME = "text";
    static final String EMBEDDING_FIELD_NAME = "embedding";
    static final String METADATA_FIELD_NAME = "metadata";
    private static final String DISTANCE_FIELD_NAME = "distance";
    private static final Long DEFAULT_PARTITION_ID = 1L;
    private static final List<FieldType> COLLECTION_FIELD_TYPES = Arrays.asList(FieldType.newBuilder()
                    .withName(DOC_ID_FIELD_NAME)
                    .withDataType(DataType.VarChar)
                    .withMaxLength(36)
                    .withPrimaryKey(true)
                    .withAutoID(false)
                    .build(),
            FieldType.newBuilder()
                    .withName(TEXT_FIELD_NAME)
                    .withDataType(DataType.VarChar)
                    .withMaxLength(65535)
                    .build(),
            FieldType.newBuilder().withName(METADATA_FIELD_NAME).withDataType(DataType.JSON).build());
    private static final List<String> OUTPUT_FIELDS =
            Arrays.asList(DOC_ID_FIELD_NAME, TEXT_FIELD_NAME, METADATA_FIELD_NAME);

    private final MilvusClient milvusClient;
    private final MilvusSchemaConfig config;
    private final DocumentEmbedModel embedModel;
    private final ExpressionParser expressionParser = new DefaultMilvusExpressionParser();

    /**
     * 创建 milvus 向量数据库实例。
     *
     * @param milvusClient 表示 milvus 客户端的 {@link MilvusClient}。
     * @param config 表示数据库元数据配置的 {@link MilvusSchemaConfig}。
     * @param embedModel 表示嵌入模型的 {@link DocumentEmbedModel}。
     * @throws IllegalArgumentException 当 {@code milvusClient}、{@code config}、{@code embedModel} 为 {@code null} 时。
     */
    public MilvusVectorStore(MilvusClient milvusClient, MilvusSchemaConfig config, DocumentEmbedModel embedModel) {
        this.milvusClient = notNull(milvusClient, "The milvus client cannot be null.");
        this.config = notNull(config, "The config cannot be null.");
        this.embedModel = notNull(embedModel, "The embed model cannot be null.");
    }

    /**
     * 初始化 {@link MilvusVectorStore}。
     * <p>这个方法会检查并创建必要的集合、索引和加载集合</p>。
     */
    @PostConstruct
    public void init() {
        if (!this.hasCollection()) {
            this.createCollection();
        }
        if (!this.hasIndex()) {
            this.createIndex();
        }
        if (!hasLoadCollection()) {
            this.loadCollection();
        }
    }

    @Override
    public void persistent(List<Document> documents) {
        notNull(documents, "The documents cannot null.");
        List<String> docIds = new ArrayList<>();
        List<String> texts = new ArrayList<>();
        List<JSONObject> metadataList = new ArrayList<>();
        List<Long> partitionIds = new ArrayList<>();
        for (Document document : documents) {
            docIds.add(StringUtils.getIfBlank(document.id(), UuidUtils::randomUuidString));
            texts.add(notBlank(document.text(), "The document text cannot be blank."));
            metadataList.add(new JSONObject(notNull(document.metadata(), "The metadata cannot be null.")));
            partitionIds.add(ObjectUtils.cast(document.metadata()
                    .getOrDefault(config.getPartitionKey(), DEFAULT_PARTITION_ID)));
        }
        List<List<Float>> embeddings =
                this.embedModel.embed(documents).stream().map(Embedding::embedding).collect(Collectors.toList());
        List<InsertParam.Field> fields = Arrays.asList(new InsertParam.Field(EMBEDDING_FIELD_NAME, embeddings),
                new InsertParam.Field(DOC_ID_FIELD_NAME, docIds),
                new InsertParam.Field(TEXT_FIELD_NAME, texts),
                new InsertParam.Field(METADATA_FIELD_NAME, metadataList),
                new InsertParam.Field(config.getPartitionKey(), partitionIds));
        InsertParam insertParam = InsertParam.newBuilder()
                .withDatabaseName(this.config.getDatabaseName())
                .withCollectionName(this.config.getCollectionName())
                .withFields(fields)
                .build();
        R<MutationResult> response = this.milvusClient.insert(insertParam);
        handleResponse(response, "Fail insert documents to milvus.");
    }

    @Override
    public List<MeasurableDocument> search(String query, SearchOption option) {
        notBlank(query, "The query cannot be blank.");
        List<Float> embedding = this.embedModel.embed(query).embedding();
        String expression =
                option.filter() != null ? this.expressionParser.parse(option.filter().expression()) : StringUtils.EMPTY;
        SearchParam.Builder builder = SearchParam.newBuilder()
                .withCollectionName(this.config.getCollectionName())
                .withConsistencyLevel(this.config.getConsistencyLevel())
                .withMetricType(this.config.getMetricType())
                .withOutFields(OUTPUT_FIELDS)
                .withVectorFieldName(EMBEDDING_FIELD_NAME)
                .withTopK(option.topK())
                .withVectors(Collections.singletonList(embedding));
        if (isNotBlank(expression)) {
            builder.withExpr(expression);
        }
        R<SearchResults> response = this.milvusClient.search(builder.build());
        handleResponse(response, "Fail to search from milvus.");

        return new SearchResultsWrapper(response.getData().getResults()).getRowRecords(0).stream().map(row -> {
            Document document = Document.custom()
                    .id(ObjectUtils.cast(row.get(DOC_ID_FIELD_NAME)))
                    .text(ObjectUtils.cast(row.get(TEXT_FIELD_NAME)))
                    .metadata(ObjectUtils.<JSONObject>cast(row.get(METADATA_FIELD_NAME)).getInnerMap())
                    .build();
            float distance = ObjectUtils.cast(row.get(DISTANCE_FIELD_NAME));
            float score =
                    (this.config.getMetricType() == MetricType.IP || this.config.getMetricType() == MetricType.COSINE)
                            ? distance
                            : (1.0f - distance);
            return new MeasurableDocument(document, score);
        }).collect(Collectors.toList());
    }

    @Override
    public void delete(List<String> ids) {
        notNull(ids, "The document id list cannot be null.");
        DeleteParam deleteParam = DeleteParam.newBuilder()
                .withCollectionName(this.config.getCollectionName())
                .withExpr(StringUtils.format("{0} in [{1}]",
                        DOC_ID_FIELD_NAME,
                        ids.stream().map(id -> "'" + id + "'").collect(Collectors.joining(","))))
                .build();
        R<MutationResult> response = this.milvusClient.delete(deleteParam);
        handleResponse(response, "Fail delete documents from milvus.");
    }

    /**
     * 根据给定的分区唯一标志删除对应的文档。
     *
     * @param partitionId 表示分区唯一标志的 {@code long}。
     */
    public void deletePartition(long partitionId) {
        DeleteParam deleteParam = DeleteParam.newBuilder()
                .withCollectionName(this.config.getCollectionName())
                .withExpr(StringUtils.format("{0}=={1}", config.getPartitionKey(), partitionId))
                .build();
        R<MutationResult> response = this.milvusClient.delete(deleteParam);
        handleResponse(response, "Fail delete partition from milvus.");
    }

    private static void handleResponse(R<?> response, String message) {
        if (response.getStatus() == R.Status.Success.getCode()) {
            return;
        }
        throw new FitException(message, response.getException());
    }

    private boolean hasCollection() {
        HasCollectionParam hasCollectionParam = HasCollectionParam.newBuilder()
                .withDatabaseName(this.config.getDatabaseName())
                .withCollectionName(this.config.getCollectionName())
                .build();
        R<Boolean> response = this.milvusClient.hasCollection(hasCollectionParam);
        handleResponse(response, "Fail to send check request to milvus.");
        return response.getData();
    }

    private void createCollection() {
        FieldType partitionFieldType = FieldType.newBuilder()
                .withName(config.getPartitionKey())
                .withDataType(DataType.Int64)
                .withPartitionKey(true)
                .build();
        FieldType embeddingFieldType = FieldType.newBuilder()
                .withName(EMBEDDING_FIELD_NAME)
                .withDataType(DataType.FloatVector)
                .withDimension(this.config.getDimension())
                .build();
        CollectionSchemaParam collectionSchemaParam = CollectionSchemaParam.newBuilder()
                .withFieldTypes(COLLECTION_FIELD_TYPES)
                .addFieldType(partitionFieldType)
                .addFieldType(embeddingFieldType)
                .build();

        CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
                .withDatabaseName(this.config.getDatabaseName())
                .withCollectionName(this.config.getCollectionName())
                .withDescription(this.config.getDescription())
                .withConsistencyLevel(this.config.getConsistencyLevel())
                .withPartitionsNum(this.config.getPartitionNum())
                .withShardsNum(this.config.getShardNum())
                .withSchema(collectionSchemaParam)
                .build();
        R<RpcStatus> response = this.milvusClient.createCollection(createCollectionParam);
        handleResponse(response, "Fail to create collection.");
    }

    private boolean hasIndex() {
        DescribeIndexParam describeIndexParam = DescribeIndexParam.newBuilder()
                .withDatabaseName(this.config.getDatabaseName())
                .withCollectionName(this.config.getCollectionName())
                .build();
        R<DescribeIndexResponse> response = this.milvusClient.describeIndex(describeIndexParam);
        return response.getData() != null;
    }

    private void createIndex() {
        CreateIndexParam createIndexParam = CreateIndexParam.newBuilder()
                .withDatabaseName(this.config.getDatabaseName())
                .withCollectionName(this.config.getCollectionName())
                .withFieldName(EMBEDDING_FIELD_NAME)
                .withIndexType(this.config.getIndexType())
                .withMetricType(this.config.getMetricType())
                .withExtraParam(this.config.getIndexParameter())
                .build();
        R<RpcStatus> response = this.milvusClient.createIndex(createIndexParam);
        handleResponse(response, "Fail to create index.");
    }

    private boolean hasLoadCollection() {
        GetLoadStateParam getLoadStateParam = GetLoadStateParam.newBuilder()
                .withDatabaseName(this.config.getDatabaseName())
                .withCollectionName(this.config.getCollectionName())
                .build();
        R<GetLoadStateResponse> response = this.milvusClient.getLoadState(getLoadStateParam);
        handleResponse(response, "Fail to get collection load state.");
        return response.getData().getState() == LoadState.LoadStateLoaded;
    }

    private void loadCollection() {
        LoadCollectionParam loadCollectionParam = LoadCollectionParam.newBuilder()
                .withDatabaseName(this.config.getDatabaseName())
                .withCollectionName(this.config.getCollectionName())
                .build();
        R<RpcStatus> response = this.milvusClient.loadCollection(loadCollectionParam);
        handleResponse(response, "Fail to get collection load state.");
    }
}