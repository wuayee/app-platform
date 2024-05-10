/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.eventhandler;

import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.Pagination;
import com.huawei.fit.jober.common.event.TaskInstanceMetaDataEvent;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.ViewMode;
import com.huawei.fit.jober.taskcenter.domain.util.PrimaryValue;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.event.EventHandler;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 通用任务数据源事件Handler
 *
 * @author d30022216
 * @since 2023-08-29
 */
@Component
public class InstanceEventHandlerImpl implements EventHandler<TaskInstanceMetaDataEvent> {
    private static final Logger log = Logger.get(InstanceEventHandlerImpl.class);

    private final TaskService taskService;

    private final TaskInstance.Repo taskInstanceRepo;

    public InstanceEventHandlerImpl(TaskService taskService, TaskInstance.Repo taskInstanceRepo) {
        this.taskService = taskService;
        this.taskInstanceRepo = taskInstanceRepo;
    }

    @Override
    public void handleEvent(TaskInstanceMetaDataEvent event) {
        OperationContext context = nullIf(event.context(), OperationContext.empty());
        TaskEntity task = this.taskService.retrieve(event.data().getTaskDefinitionId(), context);
        Map<String, Object> info = new HashMap<>(event.data().getTaskEntity().getProps().size());
        event.data().getTaskEntity().getProps().forEach(prop -> {
            TaskProperty property = task.getPropertyByName(prop.getKey());
            if (property == null) {
                info.put(prop.getKey(), prop.getValue());
                return;
            }
            PropertyDataType propertyDataType = property.dataType();
            Object value1 = prop.getValue();
            Object value = propertyDataType.fromExternal(value1);
            info.put(property.name(), value);
        });
        PrimaryValue primary = task.computePrimaryValue(info);
        TaskInstance instance = this.obtainTaskInstance(task, primary, context);

        String state = StringUtils.trim(cast(info.remove("state")));
        if (StringUtils.equalsIgnoreCase(state, "deleted")) {
            if (instance == null) {
                log.info("Skip to delete task instance because does not exist. [primary={}]", primary);
                return;
            }
            this.taskInstanceRepo.delete(task, instance.id(), context);
            return;
        }

        TaskInstance.Declaration.Builder builder = TaskInstance.Declaration.custom();
        declare(event.data().getTaskTypeId(), builder::type);
        declare(event.data().getTaskSourceId(), builder::source);
        declare(cast(info.remove("tag")), tag -> builder.tags(Collections.singletonList(tag)));

        if (instance == null) {
            builder.info(info);
            log.info("Task instance does not exist. Create new.");
            this.taskInstanceRepo.create(task, builder.build(), context);
        } else {
            log.info("Task instance exists. Patch current.");
            builder.info(this.filterPatchInfo(instance.info(), info));
            this.taskInstanceRepo.patch(task, instance.id(), builder.build(), context);
        }
    }

    private Map<String, Object> filterPatchInfo(Map<String, Object> instanceInfo, Map<String, Object> newInfo) {
        if (MapUtils.isEmpty(instanceInfo)) {
            return newInfo;
        }
        return newInfo.entrySet()
                .stream()
                .filter(entry -> !Objects.equals(instanceInfo.get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static void declare(String value, Consumer<String> consumer) {
        Optional.ofNullable(value).map(StringUtils::trim).filter(StringUtils::isNotEmpty).ifPresent(consumer);
    }

    private TaskInstance obtainTaskInstance(TaskEntity task, PrimaryValue primary, OperationContext context) {
        Map<String, List<String>> infos = new HashMap<>(primary.values().size());
        for (Map.Entry<String, Object> entry : primary.values().entrySet()) {
            TaskProperty property = task.getPropertyByName(entry.getKey());
            if (property == null) {
                continue;
            }
            String value = property.dataType().toString(entry.getValue());
            infos.put(property.name(), Collections.singletonList(value));
        }
        TaskInstance.Filter filter = TaskInstance.Filter.custom().infos(infos).build();
        PagedResultSet<TaskInstance> instances = this.taskInstanceRepo.list(task, filter, Pagination.create(0L, 1),
                Collections.emptyList(), ViewMode.LIST, context);
        if (instances.pagination().total() > 0) {
            return instances.results().get(0);
        } else {
            return null;
        }
    }
}
