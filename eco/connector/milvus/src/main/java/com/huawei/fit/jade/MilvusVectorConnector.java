/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jade;

import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.inspection.Nullable;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.rag.common.ScoreNormalizer;
import com.huawei.jade.fel.rag.store.config.VectorConfig;
import com.huawei.jade.fel.rag.store.connector.VectorConnector;
import com.huawei.jade.fel.rag.store.connector.schema.VectorField;
import com.huawei.jade.fel.rag.store.connector.schema.VectorFieldDataType;
import com.huawei.jade.fel.rag.store.connector.schema.VectorSchema;
import com.huawei.jade.fel.rag.store.query.Expression;
import com.huawei.jade.fel.rag.store.query.VectorQuery;

import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.exception.IllegalResponseException;
import io.milvus.grpc.CheckHealthResponse;
import io.milvus.grpc.DataType;
import io.milvus.grpc.DescribeCollectionResponse;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.SearchResults;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.DescribeCollectionParam;
import io.milvus.param.collection.DropCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.response.DescCollResponseWrapper;
import io.milvus.response.SearchResultsWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.util.Pair;

/**
 * MIlvus数据库连接器。
 *
 * @since 2024-05-07
 */
public class MilvusVectorConnector implements VectorConnector {
    private static final Logger logger = Logger.get(MilvusVectorConnector.class);
    private static final Map<VectorFieldDataType, DataType> dataTypeMap =
            MapBuilder.<VectorFieldDataType, DataType>get()
            .put(VectorFieldDataType.VARCHAR, DataType.VarChar)
            .put(VectorFieldDataType.JSON, DataType.JSON)
            .put(VectorFieldDataType.FLOATVECTOR, DataType.FloatVector)
            .build();

    private MilvusClient milvusClient = null;

    /**
     * 根据传入的参数构建 {@link MilvusVectorConnector} 实例。
     *
     * @param host host
     * @param port port
     * @param userName userName
     * @param passwd passwd
     * @param databaseName databaseName
     */
    public MilvusVectorConnector(String host, int port, String userName, String passwd, String databaseName) {
        ConnectParam.Builder builder = ConnectParam.newBuilder().withHost(host).withPort(port)
                .withDatabaseName(databaseName == null ? "default" : databaseName);
        if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(passwd)) {
            builder.withAuthorization(userName, passwd);
        }

        ConnectParam connectParam = builder.build();

        MilvusClient client = new MilvusServiceClient(connectParam);

        R<CheckHealthResponse> res = client.checkHealth();
        if (res == null) {
            throw new IllegalResponseException("CheckHealth return null.");
        }
        if (R.success().getStatus().equals(res.getStatus())) {
            milvusClient = client;
        } else {
            logger.error("Failed to pass health check");
            throw new IllegalResponseException(res.getMessage());
        }
    }

    /**
     * 关闭数据库连接。
     */
    @Override
    public void close() {
        if (milvusClient != null) {
            milvusClient.close();
        }
    }

    /**
     * 根据传入的查询参数和配置信息进行查询。
     *
     * @param query 表示查询参数的
     *              {@link List}{@code <}{@link Pair}{@code <}{@link Map}
     *              {@code <}{@link String},{@link Object}{@code >},{@link Float}{@code >}{@code >}。
     * @param conf 表示配置信息的 {@link VectorConfig}。
     * @return 返回查询到的值及其相关性得分。
     */
    @Override
    public List<Pair<Map<String, Object>, Float>> get(VectorQuery query, VectorConfig conf) {
        Validation.notNull(query, "The vector query cannot be null.");
        Validation.notNull(conf, "The vector conf cannot be null.");

        SearchParam param =
                SearchParam.newBuilder()
                        .withCollectionName(conf.getCollectionName())
                        .withMetricType(metricTypeMatch(conf.getMetricType()))
                        .withTopK(query.getTopK())
                        .withVectors(Arrays.asList(query.getEmbedding()))
                        .withVectorFieldName(conf.getVectorFieldName())
                        .withConsistencyLevel(ConsistencyLevelEnum.EVENTUALLY)
                        .withOutFields(Arrays.asList("*"))
                        .build();

        List<Pair<Map<String, Object>, Float>> result = new ArrayList<>();

        R<SearchResults> response = milvusClient.search(param);
        if (response.getStatus() != R.Status.Success.getCode()) {
            logger.error(response.getMessage());
            return result;
        }
        SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());
        List<SearchResultsWrapper.IDScore> scores = wrapper.getIDScore(0);

        for (SearchResultsWrapper.IDScore s : scores) {
            result.add(new Pair<>(s.getFieldValues(),
                    ScoreNormalizer.process(s.getScore(), conf.getMetricType(), conf.isShouldNormalizeScore())));
        }
        return result;
    }

    /**
     * 按照指定的config，对数据库插入input中的内容。
     *
     * @param records 表示对数据库的输入的
     *                {@link List}{@code <}{@link Map}{@code <}{@link String},{@link Object}{@code >}{@code >}。
     * @param conf 表示配置信息的 {@link VectorConfig}。
     * @throws RuntimeException 插入失败时抛异常
     */
    @Override
    public void put(List<Map<String, Object>> records, VectorConfig conf) {
        Map<String, DataType> schema = getCollectionSchema(conf.getCollectionName());
        List<InsertParam.Field> fieldsInsert = records.stream()
                .flatMap(record -> record.entrySet().stream().filter(entry -> schema.containsKey(entry.getKey())))
                .reduce((Map<String, List<Object>>) new HashMap<String, List<Object>>(), (acc, cur) -> {
                    acc.putIfAbsent(cur.getKey(), new ArrayList<>());
                    acc.get(cur.getKey()).add(cur.getValue());
                    return acc;
                }, MapUtils::merge)
                .entrySet()
                .stream()
                .map(entry -> new InsertParam.Field(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        InsertParam param = InsertParam.newBuilder()
                .withDatabaseName(conf.getDatabaseName())
                .withCollectionName(conf.getCollectionName())
                .withFields(fieldsInsert)
                .build();
        R<MutationResult> response = milvusClient.insert(param);
        if (response.getStatus() != R.Status.Success.getCode()) {
            logger.error("insert err: {}", response.getStatus());
            throw new IllegalResponseException(response.getMessage());
        }
    }

    /**
     * 根据传入的查询参数和配置进行删除。
     *
     * @param expr 表示删除表达式的 {@link Expression}。
     * @param conf 表示配置信息的 {@link VectorConfig}。
     * @return 返回删除的结果，失败时有相应的错误码。
     */
    @Override
    public Boolean delete(Expression expr, VectorConfig conf) {
        return true;
    }

    private <T> void setIfPresent(VectorField field, String propertyName, Class<T> propertyType, Consumer<T> consumer) {
        Optional.ofNullable(field.getProperty(propertyName))
                .filter(propertyType::isInstance)
                .map(propertyType::cast)
                .ifPresent(consumer);
    }

    private List<FieldType> buildFields(VectorSchema schema) {
        List<FieldType> fields = new ArrayList<>();

        for (VectorField field : schema.getFields()) {
            FieldType.Builder builder = FieldType.newBuilder()
                    .withName(field.getName())
                    .withDataType(dataTypeMatch(field.getDataType()));

            setIfPresent(field, "isPrimaryKey", Boolean.class, builder::withPrimaryKey);
            setIfPresent(field, "maxLen", Integer.class, builder::withMaxLength);
            setIfPresent(field, "dimension", Integer.class, builder::withDimension);
            setIfPresent(field, "maxCapacity", Integer.class, builder::withMaxCapacity);

            fields.add(builder.build());
        }

        return fields;
    }

    private boolean isCollectionExists(VectorConfig conf) {
        return this.milvusClient
                .hasCollection(HasCollectionParam.newBuilder()
                        .withDatabaseName(conf.getDatabaseName())
                        .withCollectionName(conf.getCollectionName())
                        .build())
                .getData();
    }

    /**
     * 根据配置信息创建表。
     *
     * @param conf 表示配置信息的 {@link VectorConfig}。
     */
    @Override
    public void createCollection(VectorConfig conf) {
        if (isCollectionExists(conf)) {
            return;
        }

        CreateCollectionParam param = CreateCollectionParam.newBuilder()
                        .withDatabaseName(conf.getDatabaseName())
                        .withCollectionName(conf.getCollectionName())
                        .withFieldTypes(buildFields(conf.getSchema()))
                        .build();
        R<RpcStatus> response = milvusClient.createCollection(param);
        if (response.getStatus() != R.Status.Success.getCode()) {
            logger.error("create collection " + conf.getCollectionName() + " err");
            throw new IllegalResponseException(response.getMessage());
        }

        response = milvusClient.createIndex(
                        CreateIndexParam.newBuilder()
                                .withDatabaseName(conf.getDatabaseName())
                                .withCollectionName(conf.getCollectionName())
                                .withFieldName(conf.getVectorFieldName())
                                .withIndexType(indexTypeMatch(conf.getIndexType()))
                                .withMetricType(metricTypeMatch(conf.getMetricType()))
                                .withExtraParam(conf.getExtraParam())
                                .build());
        if (response.getStatus() != R.Status.Success.getCode()) {
            logger.error("create index err");
            throw new IllegalResponseException(response.getMessage());
        }
        loadCollection(conf);
    }

    private void loadCollection(VectorConfig conf) {
        R<RpcStatus> response = milvusClient.loadCollection(
                LoadCollectionParam.newBuilder()
                        .withDatabaseName(conf.getDatabaseName())
                        .withCollectionName(conf.getCollectionName())
                        .build());

        if (response.getStatus() != R.Status.Success.getCode()) {
            logger.error("load collection err");
            throw new IllegalResponseException(response.getMessage());
        }
    }

    /**
     * 根据配置信息删除表。
     *
     * @param conf 表示配置信息的 {@link VectorConfig}。
     */
    @Override
    public void dropCollection(VectorConfig conf) {
        R<RpcStatus> response =
                milvusClient.dropCollection(DropCollectionParam.newBuilder()
                        .withDatabaseName(conf.getDatabaseName())
                        .withCollectionName(conf.getCollectionName())
                        .build());
        if (response.getStatus() != R.Status.Success.getCode()) {
            logger.error("drop collection " + conf.getCollectionName() + "err");
            throw new IllegalResponseException(response.getMessage());
        }
    }

    @Nullable
    private Map<String, DataType> getCollectionSchema(String collection) {
        DescribeCollectionParam param = DescribeCollectionParam.newBuilder().withCollectionName(collection).build();
        R<DescribeCollectionResponse> response = milvusClient.describeCollection(param);
        if (response.getStatus() != R.Status.Success.getCode()) {
            logger.error(response.getMessage());
            return null;
        }

        Map<String, DataType> schema = new HashMap<>();
        DescCollResponseWrapper wrapper = new DescCollResponseWrapper(response.getData());
        wrapper.getFields()
                .forEach(
                        (filed) -> {
                            schema.put(filed.getName(), filed.getDataType());
                        });
        return schema;
    }

    private DataType dataTypeMatch(@Nonnull VectorFieldDataType type) {
        return dataTypeMap.getOrDefault(type, DataType.None);
    }

    private MetricType metricTypeMatch(@Nonnull com.huawei.jade.fel.rag.store.config.MetricType metricType) {
        return MetricType.valueOf(metricType.name().toUpperCase(Locale.ROOT));
    }

    private IndexType indexTypeMatch(@Nonnull com.huawei.jade.fel.rag.store.config.IndexType indexType) {
        return IndexType.valueOf(indexType.name().toUpperCase(Locale.ROOT));
    }
}
