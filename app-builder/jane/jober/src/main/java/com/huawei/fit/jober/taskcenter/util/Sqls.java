/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util;

import com.huawei.fit.jober.taskcenter.util.sql.OrderBy;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;

import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 提供 SQL 相关的工具方法。
 *
 * @author 梁济时
 * @since 2023-08-17
 */
public final class Sqls {
    private static final SqlCache SQLS = new SqlCache();

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private Sqls() {
    }

    /**
     * 获取指定模块中指定键的 SQL 脚本。
     *
     * @param module 表示 SQL 脚本所属的模块的 {@link String}。
     * @param key 表示 SQL 脚本的键的 {@link String}。
     * @return 表示指定模块下指定键的 SQL 脚本的 {@link String}。
     * @throws IllegalArgumentException {@code module} 或 {@code key} 为 {@code null}。
     */
    public static String script(String module, String key) {
        return SQLS.module(module).script(key);
    }

    /**
     * 转义在 LIKE 中使用的模糊匹配的值。
     *
     * @param value 表示原始值的 {@link String}。
     * @return 表示转义后的值的 {@link String}。
     */
    public static String escapeLikeValue(String value) {
        return "%" + value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
    }

    /**
     * 转义在 LIKE 中使用的模糊匹配的值，只模糊匹配左侧
     *
     * @param value 表示原始值的 {@link String}。
     * @return 表示转义后的值的 {@link String}。
     */
    public static String escapeLeftLikeValue(String value) {
        return "%" + value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    /**
     * 将指定值转为 64 位整数。
     *
     * @param value 表示待转为 64 位整数的值的 {@link Object}。
     * @return 如果为 {@code null}，则为 {@code 0}，否则将其转为 64 位整数。
     * @throws IllegalStateException {@code value} 不是一个数值（{@link Number}）。
     */
    public static long longValue(Object value) {
        if (value == null) {
            return 0L;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else {
            throw new IllegalStateException(
                    StringUtils.format("The value is required to be a number. value={0}", value));
        }
    }

    /**
     * andIn
     *
     * @param sql sql
     * @param column column
     * @param count count
     */
    public static void andIn(StringBuilder sql, String column, int count) {
        if (count < 1) {
            return;
        }
        sql.append(" AND ");
        in(sql, column, count);
    }

    /**
     * in
     *
     * @param sql sql
     * @param column column
     * @param count count
     */
    public static void in(StringBuilder sql, String column, int count) {
        if (count < 1) {
            return;
        }
        sql.append(column).append(" IN (?");
        for (int i = 1; i < count; i++) {
            sql.append(", ?");
        }
        sql.append(')');
    }

    /**
     * andNotIn
     *
     * @param sql sql
     * @param column column
     * @param count count
     */
    public static void andNotIn(StringBuilder sql, String column, int count) {
        if (count < 1) {
            return;
        }
        sql.append(" AND ");
        notIn(sql, column, count);
    }

    /**
     * notIn
     *
     * @param sql sql
     * @param column column
     * @param count count
     */
    public static void notIn(StringBuilder sql, String column, int count) {
        if (count < 1) {
            return;
        }
        sql.append(column).append(" NOT IN (?");
        for (int i = 1; i < count; i++) {
            sql.append(", ?");
        }
        sql.append(')');
    }

    /**
     * andLikeAny
     *
     * @param sql sql
     * @param column column
     * @param count count
     */
    public static void andLikeAny(StringBuilder sql, String column, int count) {
        if (count < 1) {
            return;
        }
        sql.append(" AND (");
        likeAny(sql, column, count);
        sql.append(')');
    }

    /**
     * likeAny
     *
     * @param sql sql
     * @param column column
     * @param count count
     */
    public static void likeAny(StringBuilder sql, String column, int count) {
        if (count < 1) {
            return;
        }
        for (int i = 0; i < count; i++) {
            sql.append(column).append(" LIKE ? ESCAPE '\\' OR ");
        }
        sql.setLength(sql.length() - 4);
    }

    /**
     * orderBy
     *
     * @param sql sql
     * @param orderObjects orderObjects
     */
    public static void orderBy(SqlBuilder sql, List<OrderBy> orderObjects) {
        if (CollectionUtils.isEmpty(orderObjects)) {
            return;
        }
        sql.append(" ORDER BY ");
        sql.append(orderObjects.stream().map(orderBy -> orderBy.property() + " " + orderBy.order() + " NULLS LAST")
                .collect(Collectors.joining(",")));
    }

    /**
     * appendIdentifier
     *
     * @param builder builder
     * @param identifier identifier
     */
    public static void appendIdentifier(StringBuilder builder, String identifier) {
        builder.append('"').append(identifier).append('"');
    }

    /**
     * 将指定的值作为标识符使用。
     *
     * @param value 表示待作为标识符使用的值的 {@link String}。
     * @return 表示作为标识符后的值的 {@link String}。
     */
    public static String identifier(String value) {
        return '"' + value + '"';
    }
}

