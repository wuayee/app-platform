/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util;

import java.util.List;
import java.util.Map;

/**
 * 为动态 SQL 提供执行程序。
 *
 * @author 孙怡菲 s00664640
 * @since 2023-07-24
 */
public interface DynamicSqlExecutor {
    /**
     * 执行查询并返回查询得到的结果集。
     *
     * @param sql 表示待执行的 SQL 的 {@link String}。
     * @return 表示执行的结果集的 {@link List}{@code <}{@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >>}。
     */
    List<Map<String, Object>> executeQuery(String sql);

    /**
     * 执行查询并返回查询得到的结果集。
     *
     * @param sql 表示待执行的 SQL 的 {@link String}。
     * @param args 表示执行 SQL 时使用的参数的 {@link List}。
     * @return 表示执行的结果集的 {@link List}{@code <}{@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >>}。
     */
    List<Map<String, Object>> executeQuery(String sql, List<?> args);

    /**
     * 执行查询并返回第一行第一列的结果。
     *
     * @param sql 表示待执行的 SQL 的 {@link String}。
     * @return 表示结果集中第一行第一列的结果的 {@link Object}。
     */
    Object executeScalar(String sql);

    /**
     * 执行查询并返回第一行第一列的结果。
     *
     * @param sql 表示待执行的 SQL 的 {@link String}。
     * @param args 表示执行 SQL 时使用的参数的 {@link List}。
     * @return 表示结果集中第一行第一列的结果的 {@link Object}。
     */
    Object executeScalar(String sql, List<?> args);

    /**
     * 执行 SQL 并返回受影响的行数。
     *
     * @param sql 表示待执行的 SQL 的 {@link String}。
     * @return 表示受影响的行数的 32 位整数。
     */
    int executeUpdate(String sql);

    /**
     * 执行 SQL 并返回受影响的行数。
     *
     * @param sql 表示待执行的 SQL 的 {@link String}。
     * @param args 表示执行 SQL 时使用的参数的 {@link List}。
     * @return 表示受影响的行数的 32 位整数。
     */
    int executeUpdate(String sql, List<?> args);
}