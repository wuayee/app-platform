/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.util;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.jober.common.ServerInternalException;
import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fit.jober.taskcenter.util.sql.Condition;
import modelengine.fit.jober.taskcenter.util.sql.DeleteSql;
import modelengine.fit.jober.taskcenter.util.sql.InsertSql;
import modelengine.fit.jober.taskcenter.util.sql.SqlBuilder;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 列表数据
 *
 * @author 梁济时
 * @since 2024-01-24
 */
public class ListValue {
    /**
     * 列id
     */
    public static final String COLUMN_ID = "id";

    /**
     * 实例id
     */
    public static final String COLUMN_INSTANCE_ID = "instance_id";

    /**
     * 属性id
     */
    public static final String COLUMN_PROPERTY_ID = "property_id";

    /**
     * 索引
     */
    public static final String COLUMN_INDEX = "index";

    /**
     * 值
     */
    public static final String COLUMN_VALUE = "value";

    private static final Logger log = Logger.get(ListValue.class);

    private final Map<String, Object> values;

    /**
     * 构造函数
     */
    public ListValue() {
        this(null);
    }

    /**
     * 构造函数
     *
     * @param values 值
     */
    public ListValue(Map<String, Object> values) {
        if (values == null) {
            this.values = new LinkedHashMap<>(5);
        } else {
            this.values = values;
        }
    }

    /**
     * 获取id
     *
     * @return id的值
     */
    public String id() {
        return cast(this.values.get(COLUMN_ID));
    }

    /**
     * 设置id
     *
     * @param value id的值
     */
    public void id(String value) {
        this.values.put(COLUMN_ID, value);
    }

    /**
     * 返回实例id
     *
     * @return 实例id
     */
    public String instanceId() {
        return cast(this.values.get(COLUMN_INSTANCE_ID));
    }

    /**
     * 设置实例id
     *
     * @param value 实例id的值
     */
    public void instanceId(String value) {
        this.values.put(COLUMN_INSTANCE_ID, value);
    }

    /**
     * 获取属性id
     *
     * @return 属性id
     */
    public String propertyId() {
        return cast(this.values.get(COLUMN_PROPERTY_ID));
    }

    /**
     * 设置属性值
     *
     * @param value 属性值
     */
    public void propertyId(String value) {
        this.values.put(COLUMN_PROPERTY_ID, value);
    }

    /**
     * 获取索引
     *
     * @return 索引
     */
    public int index() {
        return ObjectUtils.<Number>cast(this.values.get(COLUMN_INDEX)).intValue();
    }

    /**
     * 设置索引
     *
     * @param value 索引
     */
    public void index(int value) {
        this.values.put(COLUMN_INDEX, value);
    }

    /**
     * 获取value
     *
     * @return value
     */
    public Object value() {
        return this.values.get(COLUMN_VALUE);
    }

    /**
     * 设置value
     *
     * @param value 值
     */
    public void value(Object value) {
        this.values.put(COLUMN_VALUE, value);
    }

    /**
     * 通过实例id获取数据
     *
     * @param executor sql执行器
     * @param table 表名称
     * @param instanceId 实例id
     * @return 数据
     */
    public static List<ListValue> selectByInstance(DynamicSqlExecutor executor, String table, String instanceId) {
        return selectByInstances(executor, table, Collections.singletonList(instanceId));
    }

    /**
     * 批量查询列表数据
     *
     * @param executor sql执行器
     * @param table 数据库表
     * @param instanceIds 实例id列表
     * @return 数据
     */
    public static List<ListValue> selectByInstances(DynamicSqlExecutor executor, String table,
            List<String> instanceIds) {
        if (CollectionUtils.isEmpty(instanceIds)) {
            return Collections.emptyList();
        }
        return selectByCondition(executor, table, Condition.expectIn(COLUMN_INSTANCE_ID, instanceIds));
    }

    private static List<ListValue> selectByCondition(DynamicSqlExecutor executor, String table, Condition condition) {
        SqlBuilder sql = SqlBuilder.custom()
                .append("SELECT ")
                .appendIdentifier(COLUMN_ID)
                .append(", ")
                .appendIdentifier(COLUMN_INSTANCE_ID)
                .append(", ")
                .appendIdentifier(COLUMN_PROPERTY_ID)
                .append(", ")
                .appendIdentifier(COLUMN_INDEX)
                .append(", ")
                .appendIdentifier(COLUMN_VALUE)
                .append(" FROM ")
                .appendIdentifier(table)
                .append(" WHERE ");
        List<Object> args = new LinkedList<>();
        condition.toSql(sql, args);
        List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), args);
        return rows.stream().map(ListValue::new).collect(Collectors.toList());
    }

    /**
     * 插入数据
     *
     * @param executor sql执行器
     * @param table 数据库表
     * @param values 值
     */
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
                    table,
                    values.size(),
                    affectedRows);
            throw new ServerInternalException("Failed to insert list values of task instance.");
        }
    }

    /**
     * 通过实例id删除数据
     *
     * @param executor sql执行器
     * @param table 数据库表
     * @param instanceId 实例id
     */
    public static void deleteByInstance(DynamicSqlExecutor executor, String table, String instanceId) {
        deleteByCondition(executor, table, Condition.expectEqual(COLUMN_INSTANCE_ID, instanceId));
    }

    private static void deleteByCondition(DynamicSqlExecutor executor, String table, Condition condition) {
        DeleteSql.custom().from(table).where(condition).execute(executor);
    }
}
