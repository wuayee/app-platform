/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util;

import static modelengine.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fit.jober.taskcenter.util.sql.DeleteSql;
import com.huawei.fit.jober.taskcenter.util.sql.InsertSql;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;

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
 * @author 梁济时
 * @since 2024-02-07
 */
public class IndexValueRow {
    /**
     * id列
     */
    public static final String COLUMN_ID = "id";

    /**
     * instance_id列
     */
    public static final String COLUMN_INSTANCE_ID = "instance_id";

    /**
     * property_id列
     */
    public static final String COLUMN_PROPERTY_ID = "property_id";

    /**
     * value列
     */
    public static final String COLUMN_VALUE = "value";
    private static final Logger log = Logger.get(IndexValueRow.class);

    private final Map<String, Object> values;

    public IndexValueRow() {
        this(null);
    }

    /**
     * IndexValueRow构造函数
     *
     * @param values 索引数据行
     */
    public IndexValueRow(Map<String, Object> values) {
        if (values == null) {
            this.values = new HashMap<>(4);
        } else {
            this.values = new HashMap<>(values);
        }
    }

    /**
     * 获得id列的值
     *
     * @return 返回id列的值
     */
    public String id() {
        return cast(this.values.get(COLUMN_ID));
    }

    /**
     * 设置id列的值
     *
     * @param id 表示id的{@link String}
     */
    public void id(String id) {
        this.values.put(COLUMN_ID, id);
    }

    /**
     * 获得instance_id列的值
     *
     * @return instance_id列的值
     */
    public String instanceId() {
        return cast(this.values.get(COLUMN_INSTANCE_ID));
    }

    /**
     * 设置instance_id列的值
     *
     * @param instanceId 表示实例id的{@link String}
     */
    public void instanceId(String instanceId) {
        this.values.put(COLUMN_INSTANCE_ID, instanceId);
    }

    /**
     * 获得property_id列的值
     *
     * @return property_id列的值
     */
    public String propertyId() {
        return cast(this.values.get(COLUMN_PROPERTY_ID));
    }

    /**
     * 设置property_id列的值
     *
     * @param propertyId 表示属性id的{@link String}
     */
    public void propertyId(String propertyId) {
        this.values.put(COLUMN_PROPERTY_ID, propertyId);
    }

    /**
     * 获得value列的值
     *
     * @return value列的值
     */
    public Object value() {
        return this.values.get(COLUMN_VALUE);
    }

    /**
     * 设置value列的值
     *
     * @param value 表示值的{@link Object}
     */
    public void value(Object value) {
        this.values.put(COLUMN_VALUE, value);
    }

    /**
     * 创建索引数据行
     *
     * @param instanceId 表示实例id的{@link String}
     * @param propertyId 表示属性id的{@link String}
     * @param value 表示值的{@link Object}
     * @return 索引数据行
     */
    public static IndexValueRow create(String instanceId, String propertyId, Object value) {
        IndexValueRow row = new IndexValueRow();
        row.id(Entities.generateId());
        row.instanceId(instanceId);
        row.propertyId(propertyId);
        row.value(value);
        return row;
    }

    /**
     * 向表中插入索引数据行
     *
     * @param executor 表示SQL执行器的{@link DynamicSqlExecutor}
     * @param table 表示待插入数据表的{@link String}
     * @param rows 表示待插入的索引数据行的集合的{@link Collection}{@code <}{@link IndexValueRow}{@code >}
     */
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

    /**
     * 向表中更新索引数据行
     *
     * @param executor 表示SQL执行器的{@link DynamicSqlExecutor}
     * @param table 表示待更新数据表的{@link String}
     * @param rows 表示待更新的索引数据行的集合的{@link Collection}{@code <}{@link IndexValueRow}{@code >}
     */
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

    /**
     * 向表中选择索引数据行
     *
     * @param executor 表示SQL执行器的{@link DynamicSqlExecutor}
     * @param table 表示待插入数据表的{@link String}
     * @param instanceId 表示实例id的{@link String}
     * @param propertyIds 表示属性id的集合的{@link Collection}{@code <}{@link String}{@code >}
     * @return 索引数据行列表
     */
    public static List<IndexValueRow> select(DynamicSqlExecutor executor, String table, String instanceId,
            Collection<String> propertyIds) {
        SqlBuilder sql = SqlBuilder.custom().append("SELECT ").appendIdentifier(COLUMN_ID).append(", ")
                .appendIdentifier(COLUMN_INSTANCE_ID).append(", ").appendIdentifier(COLUMN_PROPERTY_ID).append(", ")
                .appendIdentifier(COLUMN_VALUE).append(" FROM ").appendIdentifier(table).append(" WHERE ");
        List<Object> args = new LinkedList<>();
        Condition condition = Condition.expectEqual(COLUMN_INSTANCE_ID, instanceId);
        condition = Condition.and(condition, Condition.expectIn(COLUMN_PROPERTY_ID, propertyIds));
        condition.toSql(sql, args);
        List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), args);
        return rows.stream().map(IndexValueRow::new).collect(Collectors.toList());
    }

    /**
     * 向表中更新索引数据行
     *
     * @param executor 表示SQL执行器的{@link DynamicSqlExecutor}
     * @param table 表示待删除数据表的{@link String}
     * @param ids 表示待删除的索引数据行的id的集合的{@link Collection}{@code <}{@link String}{@code >}
     */
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
