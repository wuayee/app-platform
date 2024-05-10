/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.store.connector;

import java.util.Map;
import java.util.Set;

/**
 * 键值数据库连接器接口。
 *
 * @since 2024-05-07
 */
public interface KvConnector {
    /**
     * 根据传入的键在指定的namespace进行查询。
     *
     * @param key 表示要查询的键的 {@link String}。
     * @param namespace 表示namespace名称的 {@link String}。
     * @return 返回查询到的值的 {@link String}。
     */
    String get(String key, String namespace);

    /**
     * 将传入的键值对插入到指定的namespace中。
     *
     * @param kvPairs 表示键值对的 {@link Map} {@code <} {@link String}, {@link String} {@code >}。
     * @param namespace 表示namespace名称的 {@link String}。
     */
    void put(Map<String, String> kvPairs, String namespace);

    /**
     * 在指定的namespace中删除传入的键。
     *
     * @param key 表示要删除的键的 {@link String}。
     * @param namespace 表示namespace名称的 {@link String}。
     * @return 返回删除的结果，失败时有相应的错误码。
     */
    Boolean delete(String key, String namespace);

    /**
     * 获取指定namespace的所有的键。
     *
     * @param namespace 表示namespace名称的 {@link String}。
     * @return 返回键的集合。
     */
    Set<String> keys(String namespace);

    /**
     * 关闭数据库连接。
     */
    void close();
}
