/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql;

import static modelengine.fitframework.inspection.Validation.notNull;

import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fitframework.util.StringUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 为 {@link UpdateSql} 提供基于 Postgresql 的实现。
 *
 * @author 梁济时
 * @since 2023-10-24
 */
class PostgresUpdateSql implements UpdateSql {
    private String tableName;

    private final Map<String, Object> values;

    private Condition condition;

    PostgresUpdateSql() {
        this.values = new LinkedHashMap<>();
    }

    @Override
    public UpdateSql table(String tableName) {
        this.tableName = tableName;
        return this;
    }

    @Override
    public UpdateSql set(String column, Object value) {
        this.values.put(column, value);
        return this;
    }

    @Override
    public UpdateSql where(Condition condition) {
        this.condition = condition;
        return this;
    }

    @Override
    public int execute(DynamicSqlExecutor executor) {
        notNull(executor, "The executor for UPDATE cannot be null.");
        if (StringUtils.isEmpty(this.tableName)) {
            throw new IllegalStateException("No table specified to update.");
        }
        if (this.values.isEmpty()) {
            throw new IllegalStateException("No value specified to update to table.");
        }
        SqlBuilder sql = SqlBuilder.custom();
        List<Object> args = new LinkedList<>();
        sql.append("UPDATE ").appendIdentifier(this.tableName).append(" SET ");
        Iterator<Map.Entry<String, Object>> iterator = this.values.entrySet().iterator();
        Map.Entry<String, Object> entry = iterator.next();
        this.appendValue(sql, args, entry.getKey(), entry.getValue());
        while (iterator.hasNext()) {
            entry = iterator.next();
            sql.append(", ");
            this.appendValue(sql, args, entry.getKey(), entry.getValue());
        }
        if (this.condition != null) {
            sql.append(" WHERE ");
            this.condition.toSql(sql, args);
        }
        return executor.executeUpdate(sql.toString(), args);
    }

    private void appendValue(SqlBuilder sql, List<Object> args, String column, Object value) {
        sql.appendIdentifier(column);
        if (value instanceof SqlValue) {
            SqlValue sqlValue = (SqlValue) value;
            sql.append(" = ").append(sqlValue.wrapPlaceholder("?"));
            args.add(sqlValue.get());
        } else {
            sql.append(" = ?");
            args.add(value);
        }
    }
}
