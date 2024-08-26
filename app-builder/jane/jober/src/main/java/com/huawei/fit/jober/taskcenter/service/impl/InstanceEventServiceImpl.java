/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.declaration.InstanceEventDeclaration;
import com.huawei.fit.jober.taskcenter.domain.InstanceEvent;
import com.huawei.fit.jober.taskcenter.domain.InstanceEventType;
import com.huawei.fit.jober.taskcenter.service.InstanceEventService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fit.jober.taskcenter.util.sql.InsertSql;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;
import com.huawei.fit.jober.taskcenter.validation.InstanceEventValidator;

import lombok.RequiredArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 {@link InstanceEventService} 提供实现。
 *
 * @author 梁济时
 * @since 2023-09-04
 */
@Component
@RequiredArgsConstructor
public class InstanceEventServiceImpl implements InstanceEventService {
    private final DynamicSqlExecutor executor;

    private final InstanceEventValidator validator;

    @Override
    public void save(Map<String, List<InstanceEventDeclaration>> declarations, OperationContext context) {
        if (MapUtils.isEmpty(declarations)) {
            return;
        }
        InsertSql sql = InsertSql.custom().into("task_instance_event");
        List<String> clearSourceIds = new LinkedList<>();
        int count = fillSqlValue(declarations, clearSourceIds, sql);
        SqlBuilder deleteSql = SqlBuilder.custom();
        List<Object> deleteArgs = new LinkedList<>();
        deleteSql.append("DELETE FROM ").appendIdentifier("task_instance_event").append(" WHERE ");
        List<String> conditions = new LinkedList<>();
        if (count > 0) {
            List<Map<String, Object>> rows = sql.conflict("source_id", "event_type", "fitable_id")
                    .executeAndReturn(this.executor, "id", "source_id");
            if (rows.size() != count) {
                throw new ServerInternalException("Failed to insert task instance events into database.");
            }
            Map<String, List<String>> groupedIds = rows.stream()
                    .collect(Collectors.groupingBy(row -> ObjectUtils.cast(row.get("source_id")),
                            Collectors.mapping(row -> ObjectUtils.cast(row.get("id")), Collectors.toList())));
            for (Map.Entry<String, List<String>> entry : groupedIds.entrySet()) {
                SqlBuilder conditionSql = SqlBuilder.custom();
                conditionSql.appendIdentifier("source_id")
                        .append(" = ? AND ")
                        .appendIdentifier("id")
                        .append(" NOT IN (")
                        .appendRepeatedly("?, ", entry.getValue().size())
                        .backspace(2)
                        .append(')');
                conditions.add(conditionSql.toString());
                deleteArgs.add(entry.getKey());
                deleteArgs.addAll(entry.getValue());
            }
        }
        for (String sourceId : clearSourceIds) {
            SqlBuilder conditionSql = SqlBuilder.custom();
            conditionSql.appendIdentifier("source_id").append(" = ?");
            conditions.add(conditionSql.toString());
            deleteArgs.add(sourceId);
        }
        if (conditions.size() > 1) {
            deleteSql.append('(').append(conditions.get(0)).append(')');
            for (int i = 1; i < conditions.size(); i++) {
                deleteSql.append(" OR (").append(conditions.get(i)).append(')');
            }
        } else {
            deleteSql.append(conditions.get(0));
        }
        this.executor.executeUpdate(deleteSql.toString(), deleteArgs);
    }

    private int fillSqlValue(Map<String, List<InstanceEventDeclaration>> declarations, List<String> clearSourceIds,
            InsertSql sql) {
        int count = 0;
        for (Map.Entry<String, List<InstanceEventDeclaration>> entry : declarations.entrySet()) {
            String sourceId = this.validator.sourceId(entry.getKey());
            if (CollectionUtils.isEmpty(entry.getValue())) {
                clearSourceIds.add(sourceId);
                continue;
            }
            for (InstanceEventDeclaration declaration : entry.getValue()) {
                if (declaration == null) {
                    continue;
                }
                count++;
                sql.next();
                sql.value("id", Entities.generateId());
                sql.value("source_id", sourceId);
                sql.value("event_type",
                        this.validator.type(declaration.type()
                                .required(() -> new BadRequestException(ErrorCodes.INSTANCE_EVENT_TYPE_REQUIRED))));
                sql.value("fitable_id",
                        this.validator.fitableId(declaration.fitableId()
                                .required(() -> new BadRequestException(ErrorCodes.INSTANCE_EVENT_FITABLE_REQUIRED))));
            }
        }
        return count;
    }

    @Override
    public Map<String, List<InstanceEvent>> lookupByTaskSources(List<String> taskSourceIds) {
        List<String> actualSourceIds = Optional.ofNullable(taskSourceIds)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .filter(Entities::isId)
                .collect(Collectors.toList());
        if (actualSourceIds.isEmpty()) {
            return Collections.emptyMap();
        }
        SqlBuilder sql = SqlBuilder.custom();
        sql.append("SELECT ")
                .appendIdentifier("id").append(", ").appendIdentifier("source_id").append(", ")
                .appendIdentifier("event_type")
                .append(", ")
                .appendIdentifier("fitable_id")
                .append(" FROM ")
                .appendIdentifier("task_instance_event")
                .append(" WHERE ")
                .appendIdentifier("source_id")
                .append(" IN (")
                .appendRepeatedly("?, ", actualSourceIds.size())
                .backspace(2)
                .append(')');
        List<Object> args = new LinkedList<>(actualSourceIds);
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
        return rows.stream()
                .collect(Collectors.groupingBy(row -> ObjectUtils.cast(row.get("source_id")),
                        Collectors.mapping(InstanceEventServiceImpl::readInstanceEvent, Collectors.toList())));
    }

    @Override
    public Map<String, List<InstanceEvent>> lookupByTaskType(String taskTypeId) {
        if (!Entities.isId(taskTypeId)) {
            return Collections.emptyMap();
        }
        SqlBuilder sql = SqlBuilder.custom();
        sql.append("SELECT ")
                .appendIdentifier("id")
                .append(", ")
                .appendIdentifier("source_id")
                .append(", ")
                .appendIdentifier("event_type")
                .append(", ")
                .appendIdentifier("fitable_id")
                .append(" FROM ")
                .appendIdentifier("task_instance_event")
                .append(" AS ")
                .appendIdentifier("tie")
                .append(" INNER JOIN ")
                .appendIdentifier("task_node_source")
                .append(" AS ")
                .appendIdentifier("tns")
                .append(" ON ")
                .appendIdentifier("tns")
                .append('.')
                .appendIdentifier("source_id")
                .append(" = ")
                .appendIdentifier("tie")
                .append('.')
                .appendIdentifier("source_id")
                .append(" WHERE ")
                .appendIdentifier("tns")
                .append('.')
                .appendIdentifier("node_id")
                .append(" = ?");
        List<Object> args = Collections.singletonList(taskTypeId);
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
        return rows.stream()
                .collect(Collectors.groupingBy(row -> ObjectUtils.cast(row.get("source_id")),
                        Collectors.mapping(InstanceEventServiceImpl::readInstanceEvent, Collectors.toList())));
    }

    @Override
    public void deleteByTaskSources(List<String> taskSourceIds) {
        List<String> actualSourceIds = Optional.ofNullable(taskSourceIds)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .filter(Entities::isId)
                .collect(Collectors.toList());
        if (actualSourceIds.isEmpty()) {
            return;
        }
        SqlBuilder sql = SqlBuilder.custom();
        sql.append("DELETE FROM ")
                .appendIdentifier("task_instance_event")
                .append(" WHERE ")
                .appendIdentifier("source_id")
                .append(" IN (")
                .appendRepeatedly("?, ", actualSourceIds.size())
                .backspace(2)
                .append(')');
        List<Object> args = new LinkedList<>(actualSourceIds);
        this.executor.executeUpdate(sql.toString(), args);
    }

    @Override
    public void deleteByTaskType(String taskTypeId) {
        if (!Entities.isId(taskTypeId)) {
            return;
        }
        SqlBuilder sql = SqlBuilder.custom();
        sql.append("DELETE FROM ")
                .appendIdentifier("task_instance_event")
                .append(" AS ")
                .appendIdentifier("tie")
                .append(" USING ")
                .appendIdentifier("task_node_source")
                .append(" AS ")
                .appendIdentifier("tns")
                .append(" = ")
                .appendIdentifier("tie")
                .append('.')
                .appendIdentifier("source_id")
                .append(" WHERE ")
                .appendIdentifier("tns")
                .append('.')
                .appendIdentifier("source_id")
                .append(" AND ")
                .appendIdentifier("tns")
                .append('.')
                .appendIdentifier("node_id")
                .append(" = ?");
        List<Object> args = Collections.singletonList(taskTypeId);
        this.executor.executeUpdate(sql.toString(), args);
    }

    private static InstanceEvent readInstanceEvent(Map<String, Object> row) {
        return InstanceEvent.custom()
                .type(Enums.parse(InstanceEventType.class, ObjectUtils.cast(row.get("event_type"))))
                .fitableId(ObjectUtils.cast(row.get("fitable_id")))
                .build();
    }
}
