/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.adapter;

import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.dao.TaskSourceScheduleMapper;
import com.huawei.fit.jober.taskcenter.dao.po.SourceObject;
import com.huawei.fit.jober.taskcenter.dao.po.TaskSourceScheduleObject;
import com.huawei.fit.jober.taskcenter.declaration.SourceDeclaration;
import com.huawei.fit.jober.taskcenter.domain.ScheduleSourceEntity;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.SourceType;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;

import com.alibaba.fastjson.TypeReference;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 定时任务数据源适配器。
 *
 * @author 陈镕希
 * @since 2023-08-14
 */
@Component
public class ScheduleSourceAdapter extends AbstractSourceAdapter {
    private final TaskSourceScheduleMapper taskSourceScheduleMapper;

    private final ObjectSerializer objectSerializer;

    private final DynamicSqlExecutor executor;

    public ScheduleSourceAdapter(TaskSourceScheduleMapper taskSourceScheduleMapper,
            @Fit(alias = "json") ObjectSerializer objectSerializer, DynamicSqlExecutor executor) {
        this.taskSourceScheduleMapper = taskSourceScheduleMapper;
        this.objectSerializer = objectSerializer;
        this.executor = executor;
    }

    @Override
    public SourceType getType() {
        return SourceType.SCHEDULE;
    }

    @Override
    public SourceEntity createExtension(SourceObject sourceObject, SourceDeclaration sourceDeclaration,
            OperationContext context) {
        TaskSourceScheduleObject taskSourceScheduleObject = this.convert(sourceObject.getId(), sourceDeclaration);
        taskSourceScheduleMapper.insert(taskSourceScheduleObject);
        return this.convert(sourceObject, taskSourceScheduleObject);
    }

    @Override
    public void patchExtension(SourceObject sourceObject, SourceDeclaration declaration, OperationContext context) {
        List<Object> parameterList = new LinkedList<>();
        StringBuilder builder = new StringBuilder();
        String prefix = "UPDATE task_source_schedule SET ";
        String suffix = " WHERE id = ?";
        builder.append(prefix);
        List<String> patchColumnList = new LinkedList<>();
        declaration.getFitableId().ifDefined(fitableId -> {
            patchColumnList.add("fitable_id = ?");
            parameterList.add(fitableId);
        });
        declaration.getInterval().ifDefined(interval -> {
            patchColumnList.add("interval = ?");
            parameterList.add(interval);
        });
        declaration.getFilter().ifDefined(filter -> {
            patchColumnList.add("filter = ?");
            parameterList.add(
                    new String(this.objectSerializer.serialize(filter, StandardCharsets.UTF_8),
                            StandardCharsets.UTF_8));
        });
        builder.append(String.join(",", patchColumnList));
        builder.append(suffix);
        parameterList.add(sourceObject.getId());
        int affectedRows = executor.executeUpdate(builder.toString(), parameterList);
        if (affectedRows != 1) {
            throw new ServerInternalException("Failed to patch schedule source of database.");
        }
    }

    @Override
    public void deleteExtension(String sourceId, OperationContext context) {
        taskSourceScheduleMapper.delete(sourceId);
    }

    @Override
    public SourceEntity retrieveExtension(SourceObject sourceObject, OperationContext context) {
        return this.convert(sourceObject, taskSourceScheduleMapper.select(sourceObject.getId()));
    }

    @Override
    public Map<String, List<SourceEntity>> listExtension(List<SourceObject> sourceObjects, OperationContext context) {
        Map<String, SourceObject> sourceObjectIdMap = sourceObjects.stream()
                .collect(Collectors.toMap(SourceObject::getId, Function.identity()));
        Map<String, TaskSourceScheduleObject> scheduleObjectIdMap = taskSourceScheduleMapper.selectBySourceIds(
                        sourceObjects.stream().map(SourceObject::getId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(TaskSourceScheduleObject::getId, Function.identity()));
        return scheduleObjectIdMap.keySet()
                .stream()
                .collect(Collectors.groupingBy(id -> sourceObjectIdMap.get(id).getTaskId(),
                        Collectors.mapping(id -> this.convert(sourceObjectIdMap.get(id), scheduleObjectIdMap.get(id)),
                                Collectors.toList())));
    }

    private TaskSourceScheduleObject convert(String sourceId, SourceDeclaration sourceDeclaration) {
        return TaskSourceScheduleObject.builder()
                .id(sourceId)
                .fitableId(Entities.validateId(sourceDeclaration.getFitableId()
                                .required(() -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "fitableId")),
                        () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_INVALID, "fitableId")))
                .interval(sourceDeclaration.getInterval()
                        .required(() -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "interval")))
                .filter(new String(
                        this.objectSerializer.serialize(
                                sourceDeclaration.getFilter().withDefault(Collections.emptyMap()),
                                StandardCharsets.UTF_8), StandardCharsets.UTF_8))
                .build();
    }

    private SourceEntity convert(SourceObject sourceObject, TaskSourceScheduleObject taskSourceScheduleObject) {
        ScheduleSourceEntity scheduleSourceEntity = new ScheduleSourceEntity();
        this.fill(scheduleSourceEntity, sourceObject);
        scheduleSourceEntity.setFitableId(taskSourceScheduleObject.getFitableId());
        scheduleSourceEntity.setInterval(taskSourceScheduleObject.getInterval());
        Map<String, Object> filterMap = this.objectSerializer.deserialize(
                taskSourceScheduleObject.getFilter().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8,
                new TypeReference<Map<String, Object>>() {}.getType());
        scheduleSourceEntity.setFilter(Optional.ofNullable(filterMap).orElse(new HashMap<>()));
        return scheduleSourceEntity;
    }
}
