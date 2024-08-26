/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.store;

import modelengine.fel.rag.store.config.KvConfig;
import modelengine.fel.rag.store.connector.KvConnector;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

/**
 * Key-Value类型数据库基类。
 *
 * @param <I> 表示数据库输入的 {@link I}。
 * @param <O> 表示数据库输出的 {@link O}。
 * @since 2024-05-07
 */
public abstract class KvStore<I, O> implements Store<I, O, String, KvConfig> {
    @Getter
    @Setter
    private KvConfig config;

    private final KvConnector conn;

    /**
     * 根据传入的键值型数据库连接器构建 {@link KvConnector} 实例。
     *
     * @param conn 表示键值型数据库连接器的 {@link KvConnector}。
     */
    KvStore(KvConnector conn) {
        this.conn = conn;
    }

    /**
     * 根据传入的查询参数和指定的配置进行查询。
     *
     * @param key 表示查询键的 {@link String}。
     * @param conf 表示配置信息的 {@link KvConfig}。
     * @return 返回查询到的结果。
     */
    public O get(String key, KvConfig conf) {
        return parseOutput(conn.get(key, conf.getNamespace()));
    }

    /**
     * 按照指定的config，对数据库插入input中的内容。
     *
     * @param input 表示数据库输入的 {@link I}。
     * @param conf 表示配置信息的 {@link KvConfig}。
     */
    public void put(I input, KvConfig conf) {
        conn.put(formatInput(input), conf.getNamespace());
    }

    /**
     * 根据传入的查询参数和配置进行删除。
     *
     * @param key 表示要删除的键的 {@link String}。
     * @param conf 表示配置信息的 {@link KvConfig}。
     * @return 返回删除的结果，失败时有相应的错误码。
     */
    public Boolean delete(String key, KvConfig conf) {
        return conn.delete(key, conf.getNamespace());
    }

    /**
     * 获取数据库中所有的键。
     *
     * @param conf 表示配置信息的 {@link KvConfig}。
     * @return 返回键的集合。
     */
    public Set<String> keys(KvConfig conf) {
        return conn.keys(conf.getNamespace());
    }

    /**
     * 将输入格式化为键值对。
     *
     * @param input 表示输入的 {@link I}。
     * @return 返回键值对。
     */
    protected abstract Map<String, String> formatInput(I input);

    /**
     * 解析输出。
     *
     * @param value 表示输出的 {@link String}。
     * @return 返回解析后的数据
     */
    protected abstract O parseOutput(String value);
}
