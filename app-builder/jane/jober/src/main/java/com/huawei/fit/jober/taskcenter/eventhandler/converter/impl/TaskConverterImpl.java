/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.eventhandler.converter.impl;

import com.huawei.fit.jane.task.domain.PropertyCategory;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.entity.task.Task;
import com.huawei.fit.jober.entity.task.TaskCategoryTrigger;
import com.huawei.fit.jober.entity.task.TaskProperty;
import com.huawei.fit.jober.entity.task.TaskPropertyCategory;
import com.huawei.fit.jober.entity.task.TaskSource;
import com.huawei.fit.jober.entity.task.TaskTrigger;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskCategoryTriggerEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.domain.TriggerEntity;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.TaskConverter;
import com.huawei.fit.jober.taskcenter.util.Enums;
import modelengine.fitframework.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link TaskConverter}的默认实现。
 *
 * @author 陈镕希
 * @since 2023-09-08
 */
@Component
public class TaskConverterImpl implements TaskConverter {
    @Override
    public Task convert(TaskEntity entity, OperationContext context) {
        Task task = new Task();
        task.setId(entity.getId());
        task.setName(entity.getName());
        task.setProperties(entity.getProperties().stream().map(this::convert).collect(Collectors.toList()));
        List<TaskType> taskTypes = new ArrayList<>();
        TaskType.traverse(entity.getTypes(), taskTypes::add);
        task.setTypes(taskTypes.stream().map(this::convert).collect(Collectors.toList()));
        task.setSources(entity.getSources().stream().map(this::convert).collect(Collectors.toList()));
        task.setCategoryTriggers(entity.getCategoryTriggers().stream().map(this::convert).collect(Collectors.toList()));
        task.setCreator(entity.getCreator());
        task.setCreationTime(entity.getCreationTime());
        task.setLastModifier(entity.getLastModifier());
        task.setLastModificationTime(entity.getLastModificationTime());
        task.setTenant(context.tenantId());
        return task;
    }

    @Override
    public TaskProperty convert(com.huawei.fit.jane.task.domain.TaskProperty entity) {
        TaskProperty taskProperty = new TaskProperty();
        taskProperty.setId(entity.id());
        taskProperty.setName(entity.name());
        taskProperty.setDataType(Enums.toString(entity.dataType()));
        taskProperty.setSequence(entity.sequence());
        taskProperty.setDescription(entity.description());
        taskProperty.setRequired(entity.required());
        taskProperty.setIdentifiable(entity.identifiable());
        taskProperty.setScope(Enums.toString(entity.scope()));
        taskProperty.setAppearance(entity.appearance());
        taskProperty.setCategories(entity.categories().stream().map(this::convert).collect(Collectors.toList()));
        return taskProperty;
    }

    private com.huawei.fit.jober.entity.task.TaskType convert(TaskType entity) {
        com.huawei.fit.jober.entity.task.TaskType taskType = new com.huawei.fit.jober.entity.task.TaskType();
        taskType.setId(entity.id());
        taskType.setName(entity.name());
        taskType.setParentId(entity.parentId());
        return taskType;
    }

    private TaskSource convert(SourceEntity entity) {
        TaskSource taskSource = new TaskSource();
        taskSource.setId(entity.getId());
        taskSource.setName(entity.getName());
        taskSource.setApp(entity.getApp());
        taskSource.setType(Enums.toString(entity.getType()));
        taskSource.setTriggers(entity.getTriggers().stream().map(this::convert).collect(Collectors.toList()));
        return taskSource;
    }

    private TaskTrigger convert(TriggerEntity entity) {
        TaskTrigger taskTrigger = new TaskTrigger();
        taskTrigger.setId(entity.getId());
        taskTrigger.setPropertyId(entity.getPropertyId());
        taskTrigger.setFitableId(entity.getFitableId());
        return taskTrigger;
    }

    private TaskPropertyCategory convert(PropertyCategory category) {
        TaskPropertyCategory taskPropertyCategory = new TaskPropertyCategory();
        taskPropertyCategory.setValue(category.getValue());
        taskPropertyCategory.setCategory(category.getCategory());
        return taskPropertyCategory;
    }

    private TaskCategoryTrigger convert(TaskCategoryTriggerEntity entity) {
        TaskCategoryTrigger taskCategoryTrigger = new TaskCategoryTrigger();
        taskCategoryTrigger.setCategory(entity.getCategory());
        taskCategoryTrigger.setFitableIds(entity.getFitableIds());
        return taskCategoryTrigger;
    }
}
