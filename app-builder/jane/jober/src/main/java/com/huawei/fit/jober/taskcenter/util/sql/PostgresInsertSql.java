/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 {@link InsertSql} 提供基于 Postgresql 的实现。
 *
 * @author 梁济时 l00815032
 * @since 2023-10-24
 */
class PostgresInsertSql implements InsertSql {
    private String table;

    private final Set<String> columns;

    private final List<Map<String, Object>> rows;

    private Map<String, Object> row;

    private List<String> uniqueColumns;

    private List<String> updateColumns;

    PostgresInsertSql() {
        this.columns = new LinkedHashSet<>();
        this.rows = new ArrayList<>();
    }

    @Override
    public InsertSql into(String table) {
        String actual = StringUtils.trim(table);
        if (StringUtils.isEmpty(actual)) {
            throw new IllegalArgumentException("The table to insert values cannot be blank.");
        }
        this.table = actual;
        return this;
    }

    @Override
    public InsertSql value(String column, Object value) {
        String actual = StringUtils.trim(column);
        if (StringUtils.isEmpty(actual)) {
            throw new IllegalArgumentException("The column to insert value cannot be blank.");
        }
        this.columns.add(column);
        this.row().put(actual, value);
        return this;
    }

    @Override
    public InsertSql next() {
        this.row = null;
        return this;
    }

    @Override
    public InsertSql conflict(String... columns) {
        List<String> actual = Optional.ofNullable(columns)
                .map(Stream::of)
                .orElseGet(Stream::empty)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        if (actual.isEmpty()) {
            throw new IllegalArgumentException("No columns specified to resolve conflicts.");
        }
        this.uniqueColumns = actual;
        return this;
    }

    @Override
    public InsertSql update(String... columns) {
        List<String> actual = Optional.ofNullable(columns)
                .map(Stream::of)
                .orElseGet(Stream::empty)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        if (actual.isEmpty()) {
            throw new IllegalArgumentException("No columns specified to update when conflicted.");
        }
        this.updateColumns = actual;
        return this;
    }

    @Override
    public int execute(DynamicSqlExecutor executor) {
        notNull(executor, "The executor for INSERT cannot be null.");
        SqlBuilder sql = SqlBuilder.custom();
        List<Object> args = new LinkedList<>();
        this.toSql(sql, args, Collections.emptyList());
        return executor.executeUpdate(sql.toString(), args);
    }

    @Override
    public List<Map<String, Object>> executeAndReturn(DynamicSqlExecutor executor, String... columns) {
        notNull(executor, "The executor for INSERT cannot be null.");
        List<String> returningColumns = Optional.ofNullable(columns)
                .map(Stream::of)
                .orElseGet(Stream::empty)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        if (returningColumns.isEmpty()) {
            throw new IllegalArgumentException("No columns specified to return values.");
        }
        SqlBuilder sql = SqlBuilder.custom();
        List<Object> args = new LinkedList<>();
        this.toSql(sql, args, returningColumns);
        return executor.executeQuery(sql.toString(), args);
    }

    private Map<String, Object> row() {
        if (this.row == null) {
            this.row = new HashMap<>();
            this.rows.add(this.row);
        }
        return this.row;
    }

    private void toSql(SqlBuilder sql, List<Object> args, List<String> returningColumns) {
        if (StringUtils.isEmpty(this.table)) {
            throw new IllegalStateException("No table specified to insert values.");
        }
        if (CollectionUtils.isEmpty(this.columns)) {
            throw new IllegalStateException("No value specified to insert into table.");
        }
        if (CollectionUtils.isNotEmpty(this.updateColumns) && CollectionUtils.isEmpty(this.uniqueColumns)) {
            throw new IllegalStateException("No columns specified but required to resolve conflicts.");
        }
        this.acceptPrefix(sql);
        this.acceptValueRows(sql, args);
        this.acceptConflictResolution(sql, returningColumns);
        this.acceptReturningColumns(sql, returningColumns);
    }

    private void acceptPrefix(SqlBuilder sql) {
        sql.append("INSERT INTO ").appendIdentifier(this.table).append('(');
        for (String column : this.columns) {
            sql.appendIdentifier(column).append(", ");
        }
        sql.backspace(2).append(") VALUES");
    }

    private void acceptValueRows(SqlBuilder sql, List<Object> args) {
        for (Map<String, Object> row : this.rows) {
            sql.append('(');
            for (String column : this.columns) {
                Object value = row.get(column);
                if (value instanceof SqlValue) {
                    SqlValue sqlValue = (SqlValue) value;
                    sql.append(sqlValue.wrapPlaceholder("?")).append(", ");
                    args.add(sqlValue.get());
                } else {
                    sql.append("?, ");
                    args.add(value);
                }
            }
            sql.backspace(2).append("), ");
        }
        sql.backspace(2);
    }

    private void acceptConflictResolution(SqlBuilder sql, List<String> returningColumns) {
        if (CollectionUtils.isNotEmpty(this.uniqueColumns)) {
            sql.append(" ON CONFLICT (");
            for (String column : this.uniqueColumns) {
                sql.appendIdentifier(column).append(", ");
            }
            sql.backspace(2).append(')');
            if (CollectionUtils.isNotEmpty(this.updateColumns)) {
                sql.append(" DO UPDATE SET ");
                for (String column : this.updateColumns) {
                    sql.appendIdentifier(column).append(" = EXCLUDED.").appendIdentifier(column).append(", ");
                }
                sql.backspace(2);
            } else if (returningColumns.isEmpty()) {
                sql.append(" DO NOTHING");
            } else {
                String column = this.uniqueColumns.get(0);
                sql.append(" DO UPDATE SET ").appendIdentifier(column).append(" = EXCLUDED.").appendIdentifier(column);
            }
        }
    }

    private void acceptReturningColumns(SqlBuilder sql, List<String> returningColumns) {
        if (!returningColumns.isEmpty()) {
            sql.append(" RETURNING ");
            for (String column : returningColumns) {
                sql.appendIdentifier(column).append(", ");
            }
            sql.backspace(2);
        }
    }
}
