/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.store.connector;

import com.huawei.jade.fel.rag.store.connector.schema.RdbColumn;
import com.huawei.jade.fel.rag.store.query.Expression;

import java.util.List;
import java.util.Map;

/**
 * sql数据库连接器接口。
 *
 * @since 2024-05-07
 */
public interface SqlConnector {
    /**
     * 用查询语句从指定的表名中进行查询。
     *
     * @param tableName 表示表名的 {@link String}。
     * @param expr 表示查询语句的 {@link String}。
     * @return 返回查询到的结果。
     */
    List<Map<String, Object>> get(String tableName, Expression expr);

    /**
     * 将键值数据插入到指定的表中。
     *
     * @param tableName 表示表名的 {@link String}。
     * @param value 表示要插入的键值数据的 {@link Map}{@code <}{@link String},{@link Object}{@code >}。
     */
    void put(String tableName, Map<String, Object> value);

    /**
     * 根据传入的表名和删除语句进行删除。
     *
     * @param tableName 表示表名的 {@link String}。
     * @param expr 表示删除语句的 {@link String}。
     */
    void delete(String tableName, Expression expr);

    /**
     * 执行sql语句。
     *
     * @param sql sql
     * @return 返回执行的结果。
     */
    List<Map<String, Object>> execute(String sql);

    /**
     * 创建表。
     *
     * @param tableName 表名称
     * @param columns 列信息
     *
     */
    void createTable(String tableName, List<RdbColumn> columns);

    /**
     * 删除表。
     *
     * @param tableName 表名称
     */
    void dropTable(String tableName);

    /**
     * 关闭数据库连接。
     *
     */
    void close();
}
