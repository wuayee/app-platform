/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.store.connector;

import com.huawei.jade.fel.rag.store.config.VectorConfig;
import com.huawei.jade.fel.rag.store.query.Expression;
import com.huawei.jade.fel.rag.store.query.QueryParams;
import com.huawei.jade.fel.rag.store.query.VectorQuery;

import javafx.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * 向量数据库连接器接口。
 *
 * @since 2024-05-07
 */
public interface VectorConnector {
    /**
     * 根据传入的查询参数和配置信息进行查询。
     *
     * @param query 表示查询参数的 {@link VectorQuery}。
     * @param conf 表示配置信息的 {@link VectorConfig}。
     * @return 返回查询到的值及其相关性得分。
     */
    List<Pair<Map<String, Object>, Float>> get(VectorQuery query, VectorConfig conf);

    /**
     * 按照指定的config，对数据库插入input中的内容。
     *
     * @param records 表示数据库输入的
     * {@link List}{@code <}{@link Map}{@code <}{@link String},{@link Object}{@code >}{@code >}。
     * @param conf 表示配置信息的 {@link VectorConfig}。
     */
    void put(List<Map<String, Object>> records, VectorConfig conf);

    /**
     * 根据传入的查询参数和配置进行删除。
     *
     * @param expr 表示删除表达式的 {@link Expression}。
     * @param conf 表示配置信息的 {@link VectorConfig}。
     * @return 返回删除的结果，失败时有相应的错误码。
     */
    Boolean delete(Expression expr, VectorConfig conf);

    /**
     * 根据配置信息创建表。
     *
     * @param conf 表示配置信息的 {@link VectorConfig}。
     */
    void createCollection(VectorConfig conf);

    /**
     * 根据配置信息删除表。
     *
     * @param conf 表示配置信息的 {@link VectorConfig}。
     */
    void dropCollection(VectorConfig conf);

    /**
     * 根据配置信息查询向量库数据
     *
     * @param query 查询参数
     * @param conf 配置信息
     * @return 返回查询到的entity列表，每条数据包含返回的字段及对应的值
     */
    List<Map<String, Object>> scalarQuery(QueryParams query, VectorConfig conf);

    /**
     * 关闭数据库连接。
     */
    void close();
}
