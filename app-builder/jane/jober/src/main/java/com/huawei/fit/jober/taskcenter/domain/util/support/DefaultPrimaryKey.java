/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util.support;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.util.PrimaryKey;
import com.huawei.fit.jober.taskcenter.domain.util.PrimaryValue;
import com.huawei.fit.jober.taskcenter.domain.util.TaskInstanceRow;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;

import modelengine.fitframework.util.ObjectUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 为 {@link PrimaryKey} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-10-28
 */
public class DefaultPrimaryKey implements PrimaryKey {
    private final TaskEntity task;

    private final List<TaskProperty> primaryProperties;

    public DefaultPrimaryKey(TaskEntity task, List<TaskProperty> primaryProperties) {
        this.task = task;
        this.primaryProperties = primaryProperties;
    }

    @Override
    public PrimaryValue getPrimaryValue(Map<String, Object> info) {
        Map<String, Object> primaryValues = new HashMap<>(this.primaryProperties.size());
        for (TaskProperty property : this.primaryProperties) {
            String name = property.name();
            Object value = info.get(name);
            primaryValues.put(name, value);
        }
        return new DefaultPrimaryValue(primaryValues);
    }

    @Override
    public String selectId(DynamicSqlExecutor executor, Map<String, Object> info, String table) {
        PrimaryValue value = this.getPrimaryValue(info);
        Map<PrimaryValue, String> ids = this.selectIds(executor, table, Collections.singleton(value));
        return ids.get(value);
    }

    @Override
    public Map<PrimaryValue, String> selectIds(DynamicSqlExecutor executor, List<TaskInstance> instances,
            String table) {
        if (instances.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<PrimaryValue> primaryValues = new HashSet<>(instances.size());
        for (TaskInstance object : instances) {
            PrimaryValue primaryValue = this.getPrimaryValue(object.info());
            if (primaryValues.contains(primaryValue)) {
                throw new BadRequestException(ErrorCodes.INSTANCE_EXISTS, primaryValue);
            } else {
                primaryValues.add(primaryValue);
            }
        }
        return this.selectIds(executor, table, primaryValues);
    }

    private Map<PrimaryValue, String> selectIds(DynamicSqlExecutor executor, String table, Set<PrimaryValue> values) {
        SqlBuilder sql = SqlBuilder.custom();
        List<Object> args = new LinkedList<>();
        sql.append("SELECT id");
        for (TaskProperty primaryKey : this.primaryProperties) {
            sql.append(", ")
                    .appendIdentifier(primaryKey.column())
                    .append(" AS ")
                    .appendIdentifier(TaskInstanceRow.INFO_PREFIX + primaryKey.name());
        }
        sql.append(" FROM ").appendIdentifier(table).append(" WHERE ");
        for (PrimaryValue value : values) {
            sql.append('(');
            sql.appendIdentifier("task_id").append(" = ?");
            args.add(this.task.getId());
            for (TaskProperty property : this.primaryProperties) {
                sql.append(" AND ");
                whereValueEquals(sql, args, property, value);
            }
            sql.append(')').append(" OR ");
        }
        sql.backspace(4);
        List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), args);
        return rows.stream().collect(Collectors.toMap(this::getPrimaryValue, row -> ObjectUtils.cast(row.get("id"))));
    }

    private static void whereValueEquals(SqlBuilder sql, List<Object> args, TaskProperty property,
            PrimaryValue primaryValue) {
        Object value = primaryValue.values().get(property.name());
        sql.appendIdentifier(property.column());
        if (value == null) {
            sql.append(" IS NULL");
        } else {
            sql.append(" = ?");
            args.add(value);
        }
    }
}
