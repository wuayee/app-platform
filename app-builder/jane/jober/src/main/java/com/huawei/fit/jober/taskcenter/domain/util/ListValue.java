/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fit.jober.taskcenter.util.sql.DeleteSql;
import com.huawei.fit.jober.taskcenter.util.sql.InsertSql;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.CollectionUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO 待添加类型描述信息。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-24
 */
public class ListValue {
    private static final Logger log = Logger.get(ListValue.class);

    public static final String COLUMN_ID = "id";

    public static final String COLUMN_INSTANCE_ID = "instance_id";

    public static final String COLUMN_PROPERTY_ID = "property_id";

    public static final String COLUMN_INDEX = "index";

    public static final String COLUMN_VALUE = "value";

    private final Map<String, Object> values;

    public ListValue() {
        this(null);
    }

    public ListValue(Map<String, Object> values) {
        if (values == null) {
            this.values = new LinkedHashMap<>(5);
        } else {
            this.values = values;
        }
    }

    public String id() {
        return cast(this.values.get(COLUMN_ID));
    }

    public void id(String value) {
        this.values.put(COLUMN_ID, value);
    }

    public String instanceId() {
        return cast(this.values.get(COLUMN_INSTANCE_ID));
    }

    public void instanceId(String value) {
        this.values.put(COLUMN_INSTANCE_ID, value);
    }

    public String propertyId() {
        return cast(this.values.get(COLUMN_PROPERTY_ID));
    }

    public void propertyId(String value) {
        this.values.put(COLUMN_PROPERTY_ID, value);
    }

    public int index() {
        return ((Number) this.values.get(COLUMN_INDEX)).intValue();
    }

    public void index(int value) {
        this.values.put(COLUMN_INDEX, value);
    }

    public Object value() {
        return this.values.get(COLUMN_VALUE);
    }

    public void value(Object value) {
        this.values.put(COLUMN_VALUE, value);
    }

    public static List<ListValue> selectByInstance(DynamicSqlExecutor executor, String table, String instanceId) {
        return selectByInstances(executor, table, Collections.singletonList(instanceId));
    }

    public static List<ListValue> selectByInstances(DynamicSqlExecutor executor, String table,
            List<String> instanceIds) {
        if (CollectionUtils.isEmpty(instanceIds)) {
            return Collections.emptyList();
        }
        return selectByCondition(executor, table, Condition.expectIn(COLUMN_INSTANCE_ID, instanceIds));
    }

    private static List<ListValue> selectByCondition(DynamicSqlExecutor executor, String table, Condition condition) {
        SqlBuilder sql = SqlBuilder.custom().append("SELECT ").appendIdentifier(COLUMN_ID).append(", ")
                .appendIdentifier(COLUMN_INSTANCE_ID).append(", ").appendIdentifier(COLUMN_PROPERTY_ID).append(", ")
                .appendIdentifier(COLUMN_INDEX).append(", ").appendIdentifier(COLUMN_VALUE).append(" FROM ")
                .appendIdentifier(table).append(" WHERE ");
        List<Object> args = new LinkedList<>();
        condition.toSql(sql, args);
        List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), args);
        return rows.stream().map(ListValue::new).collect(Collectors.toList());
    }

    public static void insert(DynamicSqlExecutor executor, String table, List<ListValue> values) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        InsertSql sql = InsertSql.custom().into(table);
        for (ListValue value : values) {
            sql.next();
            sql.value(COLUMN_ID, value.id());
            sql.value(COLUMN_INSTANCE_ID, value.instanceId());
            sql.value(COLUMN_PROPERTY_ID, value.propertyId());
            sql.value(COLUMN_INDEX, value.index());
            sql.value(COLUMN_VALUE, value.value());
        }
        int affectedRows = sql.execute(executor);
        if (affectedRows < values.size()) {
            log.error("Unexpected affected rows occurs when insert list values. [table={}, expected={}, actual={}]",
                    table, values.size(), affectedRows);
            throw new ServerInternalException("Failed to insert list values of task instance.");
        }
    }

    public static void deleteByInstance(DynamicSqlExecutor executor, String table, String instanceId) {
        deleteByCondition(executor, table, Condition.expectEqual(COLUMN_INSTANCE_ID, instanceId));
    }

    private static void deleteByCondition(DynamicSqlExecutor executor, String table, Condition condition) {
        DeleteSql.custom().from(table).where(condition).execute(executor);
    }
}
