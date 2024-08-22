/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.store;

import modelengine.fel.rag.store.config.VectorConfig;
import modelengine.fel.rag.store.connector.VectorConnector;
import modelengine.fel.rag.store.query.VectorQuery;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

import javafx.util.Pair;

/**
 * 向量数据库基类。
 *
 * @param <I> 表示数据库输入的 {@link I}。
 * @param <O> 表示数据库输出的 {@link O}。
 *
 * @since 2024-05-07
 */
public abstract class VectorStore<I, O> implements Store<I, O, VectorQuery, VectorConfig> {
    @Getter
    @Setter
    private VectorConfig config;

    private final VectorConnector conn;

    /**
     * 根据传入的向量数据库连接器创建 {@link VectorStore} 实例。
     *
     * @param conn 表示向量数据库连接器的 {@link VectorConnector}。
     */
    public VectorStore(VectorConnector conn) {
        this.conn = conn;
    }

    /**
     * 根据传入的查询参数和初始化时指定的配置进行查询。
     *
     * @param query 表示查询参数的  {@link VectorQuery}。
     * @return 返回查询到的结果.
     */
    public O get(VectorQuery query) {
        return get(query, config);
    }

    /**
     * 根据传入的查询参数和配置进行查询.
     *
     * @param query 表示查询参数的 {@link VectorQuery}。
     * @param conf 表示配置信息的 {@link VectorConfig}。
     * @return 返回查询到的结果.
     */
    public O get(VectorQuery query, VectorConfig conf) {
        return parseOutput(conn.get(query, conf));
    }

    /**
     * 按照初始化时指定的配置，对数据库插入数据。
     *
     * @param input 表示要插入的数据的 {@link I}。
     */
    public void put(I input) {
        put(input, config);
    }

    /**
     * 按照指定的配置，对数据库插入数据。
     *
     * @param input 表示要插入的数据的 {@link I}。
     * @param conf 表示配置信息的 {@link VectorConfig}。
     */
    public void put(I input, VectorConfig conf) {
        conn.put(formatInput(input), conf);
    }

    /**
     * 根据传入的查询参数和配置进行删除。
     *
     * @param query 表示查询参数的 {@link VectorQuery}。
     * @param conf 表示配置信息的 {@link VectorConfig}。
     * @return 返回删除的结果，失败时有相应的错误码。
     */
    public Boolean delete(VectorQuery query, VectorConfig conf) {
        return conn.delete(query.getExpr(), conf);
    }

    /**
     * 创建表。
     */
    public void createCollection() {
        conn.createCollection(config);
    }

    /**
     * 删除表。
     */
    public void dropCollection() {
        conn.dropCollection(config);
    }

    /**
     * 将输入格式化为键值对列表。
     *
     * @param input 表示输入的 {@link I}。
     * @return 返回键值对列表。
     */
    protected abstract List<Map<String, Object>> formatInput(I input);

    /**
     * 解析输出。
     *
     * @param value 表示输入的 {@link List}{@code <}{@link Pair}{@code <}{@link Map}
     *                       {@code <}{@link String},{@link Object}{@code >},{@link Float}{@code >}{@code >}。
     * @return 返回解析后的数据。
     */
    protected abstract O parseOutput(List<Pair<Map<String, Object>, Float>> value);
}
