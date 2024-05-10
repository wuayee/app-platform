/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.fitable;

import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.Pagination;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.InstanceService;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.entity.InstanceInfo;
import com.huawei.fit.jober.entity.InstanceQueryFilter;
import com.huawei.fit.jober.entity.OperationContext;
import com.huawei.fit.jober.entity.instance.Instance;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.ViewMode;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.InstanceConverter;
import com.huawei.fit.jober.taskcenter.fitable.util.ParamUtils;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.util.sql.OrderBy;
import com.huawei.fitframework.annotation.Alias;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 提供实例相关Fitable实现。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-28
 */
@Alias("Jane-Instance")
@Component
public class InstanceFitable implements InstanceService {
    private final TaskService taskService;

    private final TaskInstance.Repo repo;

    private final InstanceConverter converter;

    public InstanceFitable(TaskService taskService, TaskInstance.Repo repo, InstanceConverter converter) {
        this.taskService = taskService;
        this.repo = repo;
        this.converter = converter;
    }

    private static TaskInstance.Declaration convertInstanceInfo2Declaration(InstanceInfo instanceInfo) {
        TaskInstance.Declaration.Builder builder = TaskInstance.Declaration.custom();
        Optional.ofNullable(instanceInfo.getSourceId()).filter(StringUtils::isNotBlank).ifPresent(builder::source);
        Optional.ofNullable(instanceInfo.getTaskTypeId()).filter(StringUtils::isNotEmpty).ifPresent(builder::type);
        Optional.ofNullable(instanceInfo.getInfo()).filter(MapUtils::isNotEmpty).ifPresent(builder::info);
        Optional.ofNullable(instanceInfo.getTags()).filter(CollectionUtils::isNotEmpty).ifPresent(builder::tags);
        return builder.build();
    }

    private static UndefinableValue<Map<String, List<String>>> convertMap2UndefinableValueMap(
            Map<String, List<String>> map) {
        return Objects.isNull(map) || map.isEmpty() ? UndefinableValue.undefined() : UndefinableValue.defined(map);
    }

    private static UndefinableValue<List<String>> convertList2UndefinableValueList(List<String> list) {
        return CollectionUtils.isEmpty(list) ? UndefinableValue.undefined() : UndefinableValue.defined(list);
    }

    @Override
    @Fitable(id = "afd6771a02704266b0a825d70be21ef6")
    public Instance createTaskInstance(String taskId, InstanceInfo instanceInfo, OperationContext context) {
        Validation.notNull(taskId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));
        Validation.notNull(instanceInfo,
                () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "instanceInfo"));
        com.huawei.fit.jane.task.util.OperationContext actualContext = ParamUtils.convertOperationContext(context);
        TaskEntity taskEntity = taskService.retrieve(taskId, actualContext);
        TaskInstance.Declaration declaration = convertInstanceInfo2Declaration(instanceInfo);
        TaskInstance instance = this.repo.create(taskEntity, declaration, actualContext);
        return this.converter.convert(taskEntity, instance);
    }

    @Override
    @Fitable(id = "50f31d39777c4dee86b572735c2f57cb")
    public void patchTaskInstance(String taskId, String instanceId, InstanceInfo instanceInfo,
            OperationContext context) {
        Validation.notNull(taskId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));
        Validation.notNull(instanceId, () -> new BadRequestException(ErrorCodes.INSTANCE_ID_INVALID));
        Validation.notNull(instanceInfo,
                () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "instanceInfo"));
        com.huawei.fit.jane.task.util.OperationContext actualContext = ParamUtils.convertOperationContext(context);
        TaskEntity task = this.taskService.retrieve(taskId, actualContext);
        this.repo.patch(task, instanceId, convertInstanceInfo2Declaration(instanceInfo), actualContext);
    }

    @Override
    @Fitable(id = "bb650c31702049caaf76863f318b199e")
    public void deleteTaskInstance(String taskId, String instanceId, OperationContext context) {
        Validation.notNull(taskId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));
        Validation.notNull(instanceId, () -> new BadRequestException(ErrorCodes.INSTANCE_ID_INVALID));
        com.huawei.fit.jane.task.util.OperationContext actualContext = ParamUtils.convertOperationContext(context);
        this.repo.delete(this.taskService.retrieve(taskId, actualContext), instanceId, actualContext);
    }

    @Override
    @Fitable(id = "93ee0c74b1ac481eb8202116a143f1d7")
    public RangedResultSet<Instance> list(String taskId, InstanceQueryFilter filter, long offset, int limit,
            boolean isDeleted, OperationContext context) {
        Validation.notNull(taskId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));
        Validation.notNull(filter, () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "filter"));
        com.huawei.fit.jane.task.util.OperationContext actualContext = ParamUtils.convertOperationContext(context);
        TaskEntity task = taskService.retrieve(taskId, actualContext);
        TaskInstance.Filter actualFilter = this.convert(filter, isDeleted);
        Pagination pagination = Pagination.create(offset, limit);
        List<OrderBy> orderBys = Optional.ofNullable(filter.getOrderBy()).map(Collection::stream)
                .orElseGet(Stream::empty).map(StringUtils::trim).filter(StringUtils::isNotEmpty).map(OrderBy::parse)
                .collect(Collectors.toList());
        PagedResultSet<TaskInstance> instances = this.repo.list(task, actualFilter, pagination, orderBys, ViewMode.LIST,
                actualContext);
        return RangedResultSet.create(this.convert(task, instances.results()),
                instances.pagination().offset(), instances.pagination().limit(), instances.pagination().total());
    }

    @Override
    @Fitable(id = "7d35f6543ab4460087c1d2e437bc0c27")
    public void recoverTaskInstance(String taskId, String instanceId, OperationContext context) {
        Validation.notNull(taskId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));
        Validation.notNull(instanceId, () -> new BadRequestException(ErrorCodes.INSTANCE_ID_INVALID));
        com.huawei.fit.jane.task.util.OperationContext actualContext = ParamUtils.convertOperationContext(context);
        this.repo.recover(this.taskService.retrieve(taskId, actualContext), instanceId, actualContext);
    }

    private TaskInstance.Filter convert(InstanceQueryFilter filter, boolean deleted) {
        TaskInstance.Filter.Builder builder = TaskInstance.Filter.custom().deleted(deleted);
        Optional.ofNullable(filter.getIds()).filter(CollectionUtils::isNotEmpty).ifPresent(builder::ids);
        Optional.ofNullable(filter.getTypeIds()).filter(CollectionUtils::isNotEmpty).ifPresent(builder::typeIds);
        Optional.ofNullable(filter.getSourceIds()).filter(CollectionUtils::isNotEmpty).ifPresent(builder::sourceIds);
        Optional.ofNullable(filter.getTags()).filter(CollectionUtils::isNotEmpty).ifPresent(builder::tags);
        Optional.ofNullable(filter.getCategories()).filter(CollectionUtils::isNotEmpty).ifPresent(builder::categories);
        Optional.ofNullable(filter.getInfos()).filter(MapUtils::isNotEmpty).ifPresent(builder::infos);
        return builder.build();
    }

    private List<Instance> convert(TaskEntity task, List<TaskInstance> instances) {
        return instances.stream()
                .map(instance -> this.converter.convert(task, instance))
                .collect(Collectors.toList());
    }
}
