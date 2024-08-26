/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.RangeInfo;
import com.huawei.fit.jane.RangeResultInfo;
import com.huawei.fit.jane.RangedResultSetInfo;
import com.huawei.fit.jane.task.SourcedTaskInstanceService;
import com.huawei.fit.jane.task.TaskInfo;
import com.huawei.fit.jane.task.TaskInstanceFilterInfo;
import com.huawei.fit.jane.task.TaskInstanceInfo;
import com.huawei.fit.jane.task.TaskPropertyInfo;
import com.huawei.fit.jane.task.TaskSourceInfo;
import com.huawei.fit.jane.task.TaskTypeInfo;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.PaginationResult;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.domain.RefreshInTimeSourceEntity;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.util.Enums;

import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ParsingResult;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 实时刷新任务repo
 *
 * @author 姚江
 * @since 2024/7/26
 */
public class RefreshInTimeTaskInstanceRepo {
    private static final Logger log = Logger.get(RefreshInTimeTaskInstanceRepo.class);

    private final BrokerClient client;

    private final TaskEntity task;

    private RefreshInTimeSourceEntity sourceEntity;

    private String typeId;

    private boolean hasLoaded;

    public RefreshInTimeTaskInstanceRepo(BrokerClient client, TaskEntity task) {
        this.client = client;
        this.task = task;
        this.sourceEntity = null;
        this.hasLoaded = false;
    }

    private void loadRefreshInTimeSourceEntity() {
        Queue<TaskType> types = new LinkedList<>(this.task.getTypes());
        while (!types.isEmpty()) {
            TaskType type = types.poll();
            List<SourceEntity> sources = type.sources();
            if (CollectionUtils.isEmpty(sources)) {
                continue;
            }
            List<RefreshInTimeSourceEntity> actualSources = sources.stream()
                    .filter(RefreshInTimeSourceEntity.class::isInstance)
                    .map(RefreshInTimeSourceEntity.class::cast)
                    .collect(Collectors.toList());
            if (actualSources.isEmpty()) {
                continue;
            }
            if (actualSources.size() > 1 || this.sourceEntity != null) {
                log.error("More than 1 source of REFRESH_IN_TIME defined in task. [task={}]", this.task.getName());
                throw new ServerInternalException(StringUtils.format(
                        "Too many REFRESH_IN_TIME sources exists in task '{0}'.", this.task.getName()));
            }
            this.sourceEntity = actualSources.get(0);
            this.typeId = type.id();
        }
    }

    public RefreshInTimeSourceEntity getSourceEntity() {
        if (!this.hasLoaded) {
            this.loadRefreshInTimeSourceEntity();
            this.hasLoaded = true;
        }
        return this.sourceEntity;
    }

    public String getTypeId() {
        if (!this.hasLoaded) {
            this.loadRefreshInTimeSourceEntity();
            this.hasLoaded = true;
        }
        return this.typeId;
    }

    /**
     * 判断该任务是否是实时刷新任务
     *
     * @return 是否为实时刷新
     */
    public boolean processable() {
        return this.getSourceEntity() != null;
    }

    /**
     * 创建任务实例
     *
     * @param declaration 任务实例定义
     * @param context 上下文
     * @return 任务实例
     */
    public TaskInstance create(TaskInstance.Declaration declaration, OperationContext context) {
        final String genericableId = "ddaa2216ed8a4366af8fa6cf6e8bacf9";
        RefreshInTimeSourceEntity source = this.getSourceEntity();
        String fitableId = fitableId(source.getCreateFitableId());
        if (fitableId == null) {
            log.error("Task instance cannot be created in the task source. [task={}, source={}]",
                    this.task.getId(), source.getId());
            throw new BadRequestException(ErrorCodes.SOURCE_NOT_SUPPORT);
        }
        TaskSourceInfo taskSource = convert(this.task, this.getTypeId(), source);
        Map<String, Object> info = UndefinableValue.withDefault(declaration.info(), Collections.emptyMap());
        TaskInstanceInfo taskInstanceInfo;
        try {
            taskInstanceInfo = this.client.getRouter(SourcedTaskInstanceService.class, genericableId)
                    .route(new FitableIdFilter(fitableId))
                    .invoke(taskSource, convertInfo4Genericable(this.task, info), convert(context));
        } catch (FitException exception) {
            log.error(exception.getClass().getName(), exception);
            throw new ServerInternalException(StringUtils.format(
                    "Failed to invoke fitable to create task instance. [genericable={0}, fitable={1}]",
                    genericableId, fitableId));
        }
        return convert(this.task, source, taskInstanceInfo);
    }

    /**
     * 修改任务实例
     *
     * @param instanceId 任务实例Id
     * @param declaration 任务实例定义
     * @param context 上下文
     */
    public void patch(String instanceId, TaskInstance.Declaration declaration, OperationContext context) {
        final String genericableId = "314757dfb09e47c4b613f98cd086cb25";
        RefreshInTimeSourceEntity source = this.getSourceEntity();
        String fitableId = fitableId(source.getPatchFitableId());
        if (fitableId == null) {
            log.error("Task instance cannot be patched in the task source. [task={}, source={}]",
                    this.task.getId(), source.getId());
            throw new BadRequestException(ErrorCodes.SOURCE_NOT_SUPPORT);
        }
        TaskSourceInfo taskSource = convert(this.task, this.getTypeId(), source);
        Map<String, Object> info = UndefinableValue.withDefault(declaration.info(), Collections.emptyMap());
        try {
            this.client.getRouter(SourcedTaskInstanceService.class, genericableId)
                    .route(new FitableIdFilter(fitableId))
                    .invoke(taskSource, instanceId, convertInfo4Genericable(this.task, info), convert(context));
        } catch (FitException exception) {
            log.error(exception.getClass().getName(), exception);
            throw new ServerInternalException(StringUtils.format(
                    "Failed to invoke fitable to patch task instance. [genericable={}, fitable={}]",
                    genericableId, fitableId));
        }
    }

    /**
     * 删除任务实例
     *
     * @param instanceId 任务实例Id
     * @param context 上下文
     */
    public void delete(String instanceId, OperationContext context) {
        final String genericableId = "667bc18d3528473c8510b34829c80ce9";
        RefreshInTimeSourceEntity source = this.getSourceEntity();
        String fitableId = fitableId(source.getDeleteFitableId());
        if (fitableId == null) {
            log.error("Task instance cannot be deleted in the task source. [task={}, source={}]",
                    this.task.getId(), source.getId());
            throw new BadRequestException(ErrorCodes.SOURCE_NOT_SUPPORT);
        }
        TaskSourceInfo taskSource = convert(this.task, this.getTypeId(), source);
        try {
            this.client.getRouter(SourcedTaskInstanceService.class, genericableId)
                    .route(new FitableIdFilter(fitableId))
                    .invoke(taskSource, instanceId, convert(context));
        } catch (FitException exception) {
            log.error(exception.getClass().getName(), exception);
            throw new ServerInternalException(StringUtils.format(
                    "Failed to invoke fitable to delete task instance. [genericable={}, fitable={}]",
                    genericableId, fitableId));
        }
    }

    /**
     * 检索任务实例
     *
     * @param instanceId 任务实例Id
     * @param context 上下文
     * @return 任务实例
     */
    public TaskInstance retrieve(String instanceId, OperationContext context) {
        final String genericableId = "fefe9bc6358642a4ac997832db549920";
        RefreshInTimeSourceEntity source = this.getSourceEntity();
        String fitableId = fitableId(source.getRetrieveFitableId());
        if (fitableId == null) {
            log.error("Task instance cannot be retrieved in the task source. [task={}, source={}]",
                    this.task.getId(), source.getId());
            throw new BadRequestException(ErrorCodes.SOURCE_NOT_SUPPORT);
        }
        TaskSourceInfo taskSource = convert(this.task, this.getTypeId(), source);
        TaskInstanceInfo taskInstanceInfo;
        try {
            taskInstanceInfo = this.client.getRouter(SourcedTaskInstanceService.class, genericableId)
                    .route(new FitableIdFilter(fitableId))
                    .invoke(taskSource, instanceId, convert(context));
        } catch (FitException exception) {
            log.error(exception.getClass().getName(), exception);
            throw new ServerInternalException(StringUtils.format(
                    "Failed to invoke fitable to retrieve task instance. [genericable={}, fitable={}]",
                    genericableId, fitableId));
        }
        return convert(task, source, taskInstanceInfo);
    }

    /**
     * 查询任务实例列表
     *
     * @param filter 过滤器
     * @param offset 偏移量
     * @param limit 查询条数
     * @param context 上下文
     * @return 查询结果集
     */
    public PagedResultSet<TaskInstance> list(TaskInstance.Filter filter, long offset, int limit,
            OperationContext context) {
        final String genericableId = "805d46f4137e41909d81a7e469e2534a";
        RefreshInTimeSourceEntity source = this.getSourceEntity();
        String fitableId = fitableId(source.getListFitableId());
        if (fitableId == null) {
            log.error("Task instances cannot be listed in the task source. [task={}, source={}]",
                    this.task.getId(), source.getId());
            throw new BadRequestException(ErrorCodes.SOURCE_NOT_SUPPORT);
        }
        TaskSourceInfo taskSource = convert(this.task, this.getTypeId(), source);
        RangedResultSetInfo<TaskInstanceInfo> results;
        try {
            results = this.client.getRouter(SourcedTaskInstanceService.class, genericableId)
                    .route(new FitableIdFilter(fitableId))
                    .invoke(taskSource, convert(filter), new RangeInfo(offset, limit), convert(context));
        } catch (FitException exception) {
            log.error(exception.getClass().getName());
            throw new ServerInternalException(StringUtils.format(
                    "Failed to invoke fitable to list task instances. [genericable={0}, fitable={1}]",
                    genericableId, fitableId));
        }
        return convert(this.task, source, results);
    }

    private static String fitableId(String value) {
        return Optional.ofNullable(value)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .map(Entities::ignoreEmpty)
                .orElse(null);
    }

    private static TaskSourceInfo convert(TaskEntity task, String typeId, RefreshInTimeSourceEntity source) {
        TaskSourceInfo info = new TaskSourceInfo();
        info.setOwningTask(convert(task));
        info.setTypeId(typeId);
        info.setMetadata(source.getMetadata());
        return info;
    }

    private static <T, R> List<R> convert(List<T> list, Function<T, R> mapper) {
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    private static TaskInfo convert(TaskEntity task) {
        TaskInfo info = new TaskInfo();
        info.setId(task.getId());
        info.setName(task.getName());
        info.setProperties(convert(task.getProperties(), RefreshInTimeTaskInstanceRepo::convert));
        info.setTypes(convert(task.getTypes(), RefreshInTimeTaskInstanceRepo::convert));
        return info;
    }

    private static TaskPropertyInfo convert(TaskProperty property) {
        TaskPropertyInfo info = new TaskPropertyInfo();
        info.setId(property.id());
        info.setName(property.name());
        info.setDescription(property.description());
        info.setDataType(Enums.toString(property.dataType()));
        info.setIsRequired(property.required());
        info.setIsIdentifiable(property.identifiable());
        info.setScope(Enums.toString(property.scope()));
        return info;
    }

    private static TaskTypeInfo convert(TaskType type) {
        TaskTypeInfo info = new TaskTypeInfo();
        info.setId(type.id());
        info.setName(type.name());
        info.setChildren(convert(type.children(), RefreshInTimeTaskInstanceRepo::convert));
        return info;
    }

    private static com.huawei.fit.jober.entity.OperationContext convert(OperationContext context) {
        return new com.huawei.fit.jober.entity.OperationContext(context.tenantId(), context.operator(),
                context.operatorIp(), context.sourcePlatform(), context.language());
    }

    private static TaskInstance convert(TaskEntity task, SourceEntity source, TaskInstanceInfo info) {
        return TaskInstance.custom()
                .id(info.getId())
                .task(task)
                .type(TaskType.lookup(task.getTypes(), info.getTypeId()))
                .source(SourceEntity.lookup(task.getTypes(), info.getSourceId()))
                .info(convertInfo4Domain(task, info.getInfo()))
                .tags(nullIf(info.getTags(), Collections.emptyList()))
                .categories(nullIf(info.getCategories(), Collections.emptyList()))
                .build();
    }

    private static PagedResultSet<TaskInstance> convert(TaskEntity task, SourceEntity source,
            RangedResultSetInfo<TaskInstanceInfo> infos) {
        List<TaskInstance> instances = convert(infos.getResults(), info -> convert(task, source, info));
        return PagedResultSet.create(instances, convert(infos.getRange()));
    }

    private static PaginationResult convert(RangeResultInfo info) {
        return PaginationResult.create(info.getOffset(), info.getLimit(), info.getTotal());
    }

    private static TaskInstanceFilterInfo convert(TaskInstance.Filter filter) {
        TaskInstanceFilterInfo info = new TaskInstanceFilterInfo();
        info.setInfos(filter.infos());
        info.setCategories(filter.categories());
        info.setTypeIds(filter.typeIds());
        return info;
    }

    private static Map<String, Object> convertInfo4Domain(TaskEntity task, Map<String, String> info) {
        Map<String, Object> results = new LinkedHashMap<>(task.getProperties().size());
        for (TaskProperty property : task.getProperties()) {
            String value = info.get(property.name());
            if (property.dataType().listable()) {
                results.put(property.name(), value);
                continue;
            }
            ParsingResult<Object> result = property.dataType().parse(value);
            if (!result.isParsed()) {
                log.error("Invalid value of property. [task={}, property={}, dataType={}, value={}]",
                        task.getName(), property.name(), Enums.toString(property.dataType()), value);
                throw new ServerInternalException("Invalid value of property.");
            }
            results.put(property.name(), result.getResult());
        }
        return results;
    }

    private static Map<String, String> convertInfo4Genericable(TaskEntity task, Map<String, Object> info) {
        Map<String, String> results = new LinkedHashMap<>(task.getProperties().size());
        for (TaskProperty property : task.getProperties()) {
            if (!info.containsKey(property.name())) {
                continue;
            }
            Object value = info.get(property.name());
            value = property.dataType().fromExternal(value);
            String text = property.dataType().toString(value);
            results.put(property.name(), text);
        }
        return results;
    }
}
