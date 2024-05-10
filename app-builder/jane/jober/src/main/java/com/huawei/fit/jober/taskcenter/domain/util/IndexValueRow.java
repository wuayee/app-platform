/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fit.jober.taskcenter.util.sql.DeleteSql;
import com.huawei.fit.jober.taskcenter.util.sql.InsertSql;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示索引值数据表中的行。
 *
 * @author 梁济时 l00815032
 * @since 2024-02-07
 */
public class IndexValueRow {
    private static final Logger log = Logger.get(IndexValueRow.class);

    public static final String COLUMN_ID = "id";

    public static final String COLUMN_INSTANCE_ID = "instance_id";

    public static final String COLUMN_PROPERTY_ID = "property_id";

    public static final String COLUMN_VALUE = "value";

    private final Map<String, Object> values;

    public IndexValueRow() {
        this(null);
    }

    public IndexValueRow(Map<String, Object> values) {
        if (values == null) {
            this.values = new HashMap<>(4);
        } else {
            this.values = new HashMap<>(values);
        }
    }

    public String id() {
        return cast(this.values.get(COLUMN_ID));
    }

    public void id(String id) {
        this.values.put(COLUMN_ID, id);
    }

    public String instanceId() {
        return cast(this.values.get(COLUMN_INSTANCE_ID));
    }

    public void instanceId(String instanceId) {
        this.values.put(COLUMN_INSTANCE_ID, instanceId);
    }

    public String propertyId() {
        return cast(this.values.get(COLUMN_PROPERTY_ID));
    }

    public void propertyId(String propertyId) {
        this.values.put(COLUMN_PROPERTY_ID, propertyId);
    }

    public Object value() {
        return this.values.get(COLUMN_VALUE);
    }

    public void value(Object value) {
        this.values.put(COLUMN_VALUE, value);
    }

    public static IndexValueRow create(String instanceId, String propertyId, Object value) {
        IndexValueRow row = new IndexValueRow();
        row.id(Entities.generateId());
        row.instanceId(instanceId);
        row.propertyId(propertyId);
        row.value(value);
        return row;
    }

    public static void insert(DynamicSqlExecutor executor, String table, Collection<IndexValueRow> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return;
        }
        InsertSql sql = InsertSql.custom().into(table);
        rows.forEach(row -> sql.next()
                .value(COLUMN_ID, row.id())
                .value(COLUMN_INSTANCE_ID, row.instanceId())
                .value(COLUMN_PROPERTY_ID, row.propertyId())
                .value(COLUMN_VALUE, row.value()));
        int affectedRows = sql.execute(executor);
        if (affectedRows < rows.size()) {
            log.error("Unexpected affected rows occurs when insert index values. [table={}, expected={}, actual={}]",
                    table, rows.size(), affectedRows);
            throw new ServerInternalException("Failed to insert index values.");
        }
    }

    public static void update(DynamicSqlExecutor executor, String table, Collection<IndexValueRow> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return;
        }
        SqlBuilder sql = SqlBuilder.custom().append("WITH ").appendIdentifier("update_values").append('(')
                .appendIdentifier("value_id").append(", ").appendIdentifier("new_value").append(") AS (VALUES");
        List<Object> args = new LinkedList<>();
        for (IndexValueRow row : rows) {
            sql.append("(?, ?), ");
            args.addAll(Arrays.asList(row.id(), row.value()));
        }
        sql.backspace(2).append(") UPDATE ").appendIdentifier(table).append(" SET ").appendIdentifier("value")
                .append(" = ").append(ColumnRef.of("update_values", "new_value")).append(" FROM ")
                .appendIdentifier("update_values").append(" WHERE ").append(ColumnRef.of(table, COLUMN_ID))
                .append(" = ").append(ColumnRef.of("update_values", "value_id"));
        int affectedRows = executor.executeUpdate(sql.toString(), args);
        if (affectedRows < rows.size()) {
            log.error("Unexpected affected rows occurs when update index values. [table={}, expected={}, actual={}]",
                    table, rows.size(), affectedRows);
            throw new ServerInternalException("Failed to update index values");
        }
    }

    public static List<IndexValueRow> select(DynamicSqlExecutor executor, String table, String instanceId,
            Collection<String> propertyIds) {
        SqlBuilder sql = SqlBuilder.custom().append("SELECT ").appendIdentifier(COLUMN_ID).append(", ")
                .appendIdentifier(COLUMN_INSTANCE_ID).append(", ").appendIdentifier(COLUMN_PROPERTY_ID).append(", ")
                .appendIdentifier(COLUMN_VALUE).append(" FROM ").appendIdentifier(table).append(" WHERE ");
        List<Object> args = new LinkedList<>();
        Condition condition = Condition.expectEqual(COLUMN_INSTANCE_ID, instanceId);
        condition = condition.and(Condition.expectIn(COLUMN_PROPERTY_ID, propertyIds));
        condition.toSql(sql, args);
        List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), args);
        return rows.stream().map(IndexValueRow::new).collect(Collectors.toList());
    }

    public static void delete(DynamicSqlExecutor executor, String table, Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        Condition condition = Condition.expectIn(COLUMN_ID, ids);
        int affectedRows = DeleteSql.custom().from(table).where(condition).execute(executor);
        if (affectedRows < ids.size()) {
            log.error("Unexpected affected rows occurs when delete index values. [table={}, expected={}, actual={}]",
                    table, ids.size(), affectedRows);
            throw new ServerInternalException("Failed to delete index values.");
        }
    }
}
