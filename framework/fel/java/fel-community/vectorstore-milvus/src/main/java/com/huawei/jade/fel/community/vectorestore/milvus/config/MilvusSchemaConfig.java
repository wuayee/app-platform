/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.community.vectorestore.milvus.config;

import com.huawei.fitframework.annotation.AcceptConfigValues;
import com.huawei.fitframework.annotation.Component;

import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;

/**
 * 表示 milvus 数据库的配置。
 *
 * @author 易文渊
 * @since 2024-08-12
 */
@Component
@AcceptConfigValues("fel.milvus.schema")
public class MilvusSchemaConfig {
    private String databaseName = "default";
    private String collectionName = "default";
    private String description = "Fel document vector store";
    private String partitionKey = "partition_id";
    private int partitionNum = 64;
    private int shardNum = 2;
    private int dimension = 1024;
    private IndexType indexType = IndexType.IVF_FLAT;
    private String indexParameter = "{\"nlist\":1024}";
    private MetricType metricType = MetricType.COSINE;
    private ConsistencyLevelEnum consistencyLevel = ConsistencyLevelEnum.STRONG;

    /**
     * 获取 Milvus 数据库的名称。
     *
     * @return 表示 Milvus 数据库名称的 {@link String}。
     */
    public String getDatabaseName() {
        return this.databaseName;
    }

    /**
     * 设置 Milvus 数据库的名称。
     *
     * @param databaseName 表示 Milvus 集合名称的 {@link String}。
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * 获取 Milvus 集合的名称。
     *
     * @return 表示 Milvus 集合名称的 {@link String}。
     */
    public String getCollectionName() {
        return this.collectionName;
    }

    /**
     * 设置 milvus 集合的名称。
     *
     * @param collectionName 表示 milvus 集合名称的 {@link String}。
     */
    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    /**
     * 获取 Milvus 集合的描述信息。
     *
     * @return 表示 Milvus 集合描述信息的 {@link String}。
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 设置 Milvus 集合的描述信息。
     *
     * @param description 表示 Milvus 集合描述信息的 {@link String}。
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取 Milvus 集合的分区键。
     *
     * @return 表示 Milvus 集合分区键的 {@link String}。
     */
    public String getPartitionKey() {
        return this.partitionKey;
    }

    /**
     * 设置 Milvus 集合的分区键。
     *
     * @param partitionKey 表示 Milvus 集合分区键的 {@link String}。
     */
    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }

    /**
     * 获取 Milvus 集合的分区数。
     *
     * @return 表示 Milvus 集合分区数的 {@code int}。
     */
    public int getPartitionNum() {
        return this.partitionNum;
    }

    /**
     * 设置 Milvus 集合的分区数。
     *
     * @param partitionNum 表示 Milvus 集合分区数的 {@code int}。
     */
    public void setPartitionNum(int partitionNum) {
        this.partitionNum = partitionNum;
    }

    /**
     * 获取 Milvus 集合的分片数。
     *
     * @return 表示 Milvus 集合分片数的 {@code int}。
     */
    public int getShardNum() {
        return this.shardNum;
    }

    /**
     * 设置 Milvus 集合的分片数。
     *
     * @param shardNum 表示 Milvus 集合分片数的 {@code int}。
     */
    public void setShardNum(int shardNum) {
        this.shardNum = shardNum;
    }

    /**
     * 获取 Milvus 集合的向量维度。
     *
     * @return 表示 Milvus 集合向量维度的 {@code int}。
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * 设置 Milvus 集合的向量维度。
     *
     * @param dimension 表示 Milvus 集合向量维度的 {@code int}。
     */
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    /**
     * 获取 Milvus 集合的索引类型。
     *
     * @return 表示 Milvus 集合索引类型的 {@link IndexType}。
     */
    public IndexType getIndexType() {
        return this.indexType;
    }

    /**
     * 设置 Milvus 集合的索引类型。
     *
     * @param indexType 表示 Milvus 集合索引类型的 {@link IndexType}。
     */
    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }

    /**
     * 获取 Milvus 集合的索引额外参数。
     *
     * @return 表示 Milvus 集合索引额外参数的 {@link String}。
     */
    public String getIndexParameter() {
        return this.indexParameter;
    }

    /**
     * 设置 Milvus 集合的索引额外参数。
     *
     * @param indexParameter 表示 Milvus 索引额外参数的 {@link String}。
     */
    public void setIndexParameter(String indexParameter) {
        this.indexParameter = indexParameter;
    }

    /**
     * 获取 Milvus 集合的距离度量类型。
     *
     * @return 表示 Milvus 集合距离度量类型的 {@link MetricType}。
     */
    public MetricType getMetricType() {
        return this.metricType;
    }

    /**
     * 设置 Milvus 集合的距离度量类型。
     *
     * @param metricType 表示 Milvus 集合距离度量类型的 {@link MetricType}。
     */
    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    /**
     * 获取 Milvus 集合的一致性级别。
     *
     * @return 表示 Milvus 集合一致性级别的 {@link ConsistencyLevelEnum}。
     */
    public ConsistencyLevelEnum getConsistencyLevel() {
        return this.consistencyLevel;
    }

    /**
     * 设置 Milvus 集合的一致性级别。
     *
     * @param consistencyLevel 表示 Milvus 集合一致性级别的 {@link ConsistencyLevelEnum}。
     */
    public void setConsistencyLevel(ConsistencyLevelEnum consistencyLevel) {
        this.consistencyLevel = consistencyLevel;
    }
}