/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql;

import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;

import java.util.List;
import java.util.Map;

/**
 * 表示一个插入语句。
 *
 * @author 梁济时
 * @since 2023-09-04
 */
public interface InsertSql {
    /**
     * 指定待将数据插入到的数据表。
     *
     * @param table 表示数据表的名称的 {@link String}。
     * @return 表示当前的脚本的 {@link InsertSql}。
     * @throws IllegalArgumentException {@code table} 是一个空白字符串。
     */
    InsertSql into(String table);

    /**
     * 指示指定列的值。
     *
     * @param column 表示待设置值的数据列的名称的 {@link String}。
     * @param value 表示待插入的数据值的 {@link Object}。
     * @return 表示当前的脚本的 {@link InsertSql}。
     * @throws IllegalArgumentException {@code column} 是一个空白字符串。
     */
    InsertSql value(String column, Object value);

    /**
     * 指示待插入下一行数据。
     *
     * @return 表示当前的脚本的 {@link InsertSql}。
     */
    InsertSql next();

    /**
     * 指示用以判定数据冲突的数据列。
     *
     * @param columns 表示用以判定冲突的数据列的 {@link String}{@code []}。
     * @return 表示当前的脚本的 {@link InsertSql}。
     * @throws IllegalArgumentException {@code columns} 中未包含有效的数据列名称。
     */
    InsertSql conflict(String... columns);

    /**
     * 指示当发生数据冲突时更新的数据列。
     *
     * @param columns 表示待更新的数据列的 {@link String}{@code []}。
     * @return 表示当前的脚本的 {@link InsertSql}。
     * @throws IllegalArgumentException {@code columns} 中未包含有效的数据列名称。
     */
    InsertSql update(String... columns);

    /**
     * 执行 SQL。
     *
     * @param executor 表示 SQL 执行器的 {@link DynamicSqlExecutor}。
     * @return 表示受影响的行数的 32 位整数。
     * @throws IllegalArgumentException {@code executor} 为 {@code null}。
     */
    int execute(DynamicSqlExecutor executor);

    /**
     * 执行 SQL 并返回指定列的数据。
     *
     * @param executor 表示 SQL 执行器的 {@link DynamicSqlExecutor}。
     * @param columns 表示待返回值的数据列的 {@link String}{@code []}。
     * @return 表示返回数据的 {@link List}{@code <}{@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >>}。
     * @throws IllegalArgumentException {@code executor} 为 {@code null} 或 {@code columns} 中未包含有效的数据列名称。
     */
    List<Map<String, Object>> executeAndReturn(DynamicSqlExecutor executor, String... columns);

    /**
     * 返回一个 INSERT SQL 语句用以自定义。
     *
     * @return 表示插入语句的 {@link InsertSql}。
     */
    static InsertSql custom() {
        return new PostgresInsertSql();
    }
}

