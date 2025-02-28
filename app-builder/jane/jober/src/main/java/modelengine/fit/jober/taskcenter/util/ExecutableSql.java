/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util;

import static modelengine.fitframework.inspection.Validation.notNull;

import java.util.List;
import java.util.Map;

/**
 * 表示可执行的 SQL。
 *
 * @author 梁济时
 * @since 2023-08-10
 */
public interface ExecutableSql {
    /**
     * 获取 SQL 脚本。
     *
     * @return 表示 JDBC SQL 脚本的 {@link String}。
     */
    String sql();

    /**
     * 获取执行 SQL 时使用的参数。
     *
     * @return 表示参数列表的 {@link List}。
     */
    List<Object> args();

    /**
     * 解析参数化的 SQL。
     *
     * @param parameterizedSql 表示参数化的 SQL 脚本的 {@link String}。
     * @param args 表示脚本的执行参数的 {@link Map}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示解析得到的可执行 SQL 的 {@link ExecutableSql}。
     */
    static ExecutableSql resolve(String parameterizedSql, Map<String, Object> args) {
        return DefaultExecutableSql.resolve(parameterizedSql, args);
    }

    /**
     * 创建可执行 SQL 的新实例。
     *
     * @param sql 表示 SQL 脚本的 {@link String}。
     * @param args 表示 SQL 的执行参数的 {@link List}{@code <}{@link Object}{@code >}。
     * @return 表示可执行 SQL 的 {@link ExecutableSql}。
     */
    static ExecutableSql create(String sql, List<Object> args) {
        return new DefaultExecutableSql(sql, args);
    }

    /**
     * 执行更新语句。
     *
     * @param executor 表示动态 SQL 执行器的 {@link DynamicSqlExecutor}。
     * @return 表示受影响的行数的 32 位整数。
     */
    default int executeUpdate(DynamicSqlExecutor executor) {
        return notNull(executor, "Null executor for SQL occurs.").executeUpdate(this.sql(), this.args());
    }

    /**
     * 执行查询语句，并返回首行首列的值。
     *
     * @param executor 表示动态 SQL 执行器的 {@link DynamicSqlExecutor}。
     * @return 表示结果集中首行首列的值的 {@link Object}。
     */
    default Object executeScalar(DynamicSqlExecutor executor) {
        return notNull(executor, "Null executor for SQL occurs.").executeScalar(this.sql(), this.args());
    }

    /**
     * 执行查询语句，并返回结果集。
     *
     * @param executor 表示动态 SQL 执行器的 {@link DynamicSqlExecutor}。
     * @return 表示执行的结果集的 {@link List}{@code <}{@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >>}。
     */
    default List<Map<String, Object>> executeQuery(DynamicSqlExecutor executor) {
        return notNull(executor, "Null executor for SQL occurs.").executeQuery(this.sql(), this.args());
    }
}

