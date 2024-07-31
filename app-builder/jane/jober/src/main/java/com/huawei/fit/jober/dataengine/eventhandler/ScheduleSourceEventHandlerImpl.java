/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.eventhandler;

import static com.huawei.fit.jober.common.ErrorCodes.CANNOT_FIND_CORRESPONDING_CONSUMER;
import static com.huawei.fit.jober.common.ErrorCodes.ENTITY_NOT_FOUND;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.event.ScheduleSourceEvent;
import com.huawei.fit.jober.common.event.entity.SourceMetaData;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.dataengine.genericable.StaticDataEngine;
import com.huawei.fit.jober.dataengine.rest.request.StaticMetaDataTaskDTO;
import com.huawei.fit.jober.entity.Filter;
import com.huawei.fit.jober.taskcenter.domain.ScheduleSourceEntity;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.SourceType;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.event.EventHandler;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.StringUtils;

import com.alibaba.fastjson.TypeReference;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 定时任务数据源事件Handler。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-21
 */
@Component
public class ScheduleSourceEventHandlerImpl implements EventHandler<ScheduleSourceEvent> {
    private static final String SOURCE_METADATA_KEY = "metadata";

    private static final String SOURCE_STATUS_KEY = "status";

    private static final Logger log = Logger.get(ScheduleSourceEventHandlerImpl.class);

    private final StaticDataEngine staticDataEngine;

    private final TaskService taskService;

    private final Map<String, Consumer<SourceMetaData>> sourceEventHandlerStrategy = new HashMap<>();

    private final ObjectSerializer objectSerializer;

    /**
     * ScheduleSourceEventHandlerImpl
     *
     * @param staticDataEngine staticDataEngine
     * @param taskService taskService
     * @param objectSerializer objectSerializer
     */
    public ScheduleSourceEventHandlerImpl(StaticDataEngine staticDataEngine, TaskService taskService,
            @Fit(alias = "json") ObjectSerializer objectSerializer) {
        this.staticDataEngine = staticDataEngine;
        this.taskService = taskService;
        this.objectSerializer = objectSerializer;
        this.sourceEventHandlerStrategy.put("create", this::createEventStrategy);
        this.sourceEventHandlerStrategy.put("update", this::updateEventStrategy);
        this.sourceEventHandlerStrategy.put("delete", this::deleteEventStrategy);
    }

    @Override
    public void handleEvent(ScheduleSourceEvent event) {
        // 只处理定时任务相关的事件
        if (!StringUtils.equals(Enums.toString(SourceType.SCHEDULE), event.data().getType())) {
            return;
        }
        Optional.ofNullable(event.getType()).map(this.sourceEventHandlerStrategy::get).orElseThrow(() -> {
            log.error("Cannot find corresponding consumer of event. [Event type is {}]", event.getType());
            return new BadRequestException(CANNOT_FIND_CORRESPONDING_CONSUMER);
        }).accept(event.data());
    }

    private void createEventStrategy(SourceMetaData metaData) {
        TaskEntity taskEntity = taskService.retrieve(metaData.getTaskId(), OperationContext.custom().build());
        staticDataEngine.create(
                this.constructStaticMetaDataTaskDTO(taskEntity, metaData.getTaskSourceId(), metaData.getTypeId()));
    }

    private void updateEventStrategy(SourceMetaData metaData) {
        TaskEntity taskEntity = taskService.retrieve(metaData.getTaskId(), OperationContext.custom().build());
        staticDataEngine.update(
                this.constructStaticMetaDataTaskDTO(taskEntity, metaData.getTaskSourceId(), metaData.getTypeId()));
    }

    private void deleteEventStrategy(SourceMetaData metaData) {
        try {
            this.staticDataEngine.delete(metaData.getTaskSourceId());
        } catch (JobberException ex) {
            if (ex.getCode() == ENTITY_NOT_FOUND.getErrorCode()) {
                return;
            }
            log.error("Catch exception when invoke StaticDataEngine to delete task. "
                    + "Exception class is {}, exception message is {}", ex.getClass().getName(), ex.getMessage());
            throw ex;
        }
    }

    private StaticMetaDataTaskDTO constructStaticMetaDataTaskDTO(TaskEntity taskEntity, String sourceId,
            String taskTypeId) {
        SourceEntity sourceEntity = taskEntity.getSources()
                .stream()
                .filter(source -> StringUtils.equals(source.getId(), sourceId))
                .findAny()
                .orElseThrow(() -> {
                    log.error("Cannot get specify source in taskEntity, taskId is {}, sourceId is {}.",
                            taskEntity.getId(),
                            sourceId);
                    return new ServerInternalException("Cannot get specify source in taskEntity.");
                });
        ScheduleSourceEntity scheduleSourceEntity = Validation.isInstanceOf(sourceEntity, ScheduleSourceEntity.class,
                () -> {
                    log.error("Specify sourceEntity is not instance of ScheduleSourceEntity, taskId is {}, "
                            + "sourceId is {}", taskEntity.getId(), sourceId);
                    return new ServerInternalException("Specify sourceEntity is not instance of ScheduleSourceEntity.");
                });
        List<String> returnField = taskEntity.getProperties()
                .stream()
                .map(TaskProperty::name)
                .collect(Collectors.toList());
        Map<String, String> stringFilter = new HashMap<>();
        scheduleSourceEntity.getFilter().forEach((key, value) -> {
            if (value instanceof String) {
                stringFilter.put(key, (String) value);
            }
        });
        stringFilter.put("dataFetchType", scheduleSourceEntity.getFitableId());
        stringFilter.put("schedulerInterval", String.valueOf(scheduleSourceEntity.getInterval()));
        return StaticMetaDataTaskDTO.builder()
                .taskSourceId(sourceId)
                .taskDefinitionId(taskEntity.getId())
                .taskTypeId(taskTypeId)
                .sourceApp(sourceEntity.getApp())
                .filter(new Filter(null, null,
                        deserializePropertyValue(stringFilter, SOURCE_METADATA_KEY, String.class),
                        scheduleSourceEntity.getName(), deserializePropertyValue(stringFilter, SOURCE_STATUS_KEY,
                        new TypeReference<List<String>>() {}), returnField, null))
                .properties(stringFilter)
                .build();
    }

    private <T> T deserializePropertyValue(Map<String, String> filter, String key, Class<T> valueType) {
        String value = filter.get(key);
        if (Objects.isNull(value)) {
            return null;
        }
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        return objectSerializer.deserialize(bytes, StandardCharsets.UTF_8, valueType);
    }

    private <T> T deserializePropertyValue(Map<String, String> filter, String key, TypeReference<T> valueType) {
        String value = filter.get(key);
        if (Objects.isNull(value)) {
            return null;
        }
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        return objectSerializer.deserialize(bytes, StandardCharsets.UTF_8, valueType.getType());
    }
}
