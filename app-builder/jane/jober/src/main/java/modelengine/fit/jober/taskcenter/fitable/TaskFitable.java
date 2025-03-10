/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.fitable;

import modelengine.fit.jane.task.util.UndefinableValue;
import modelengine.fit.jober.common.util.ParamUtils;

import modelengine.fit.jober.entity.OperationContext;
import modelengine.fit.jober.entity.TaskFilter;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.eventhandler.converter.TaskConverter;

import modelengine.fit.jober.TaskService;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.entity.task.Task;
import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * {@link TaskService}的实现类
 *
 * @author 陈镕希
 * @since 2023-08-29
 */
@Alias("Jane-Task")
@Component
public class TaskFitable implements TaskService {
    private final modelengine.fit.jober.taskcenter.service.TaskService taskService;

    private final TaskConverter taskConverter;

    public TaskFitable(modelengine.fit.jober.taskcenter.service.TaskService taskService, TaskConverter taskConverter) {
        this.taskService = taskService;
        this.taskConverter = taskConverter;
    }

    @Override
    @Fitable(id = "3cfd2c76e00943b9abc3c2d2cbf7ad49")
    public RangedResultSet<Task> list(TaskFilter filter, long offset, int limit, OperationContext context) {
        modelengine.fit.jober.taskcenter.filter.TaskFilter taskFilter =
                new modelengine.fit.jober.taskcenter.filter.TaskFilter();
        taskFilter.setIds(valueOf(filter.getIds(), CollectionUtils::isEmpty));
        taskFilter.setNames(valueOf(filter.getNames(), CollectionUtils::isEmpty));
        taskFilter.setTemplateIds(valueOf(filter.getTemplateIds(), CollectionUtils::isEmpty));
        taskFilter.setCategories(valueOf(filter.getCategories(), CollectionUtils::isEmpty));
        taskFilter.setCreators(valueOf(filter.getCreators(), CollectionUtils::isEmpty));
        taskFilter.setOrderBys(valueOf(filter.getOrderBys(), CollectionUtils::isEmpty));
        modelengine.fitframework.model.RangedResultSet<TaskEntity>
                taskEntityRangedResultSet = taskService.list(taskFilter, offset, limit,
                ParamUtils.convertOperationContext(context));
        return RangedResultSet.create(this.convert(taskEntityRangedResultSet.getResults(), context),
                taskEntityRangedResultSet.getRange().getOffset(), taskEntityRangedResultSet.getRange().getLimit(),
                taskEntityRangedResultSet.getRange().getTotal());
    }

    @Override
    @Fitable(id = "e3c57537f76d44c3b1a6fffedbec297b")
    public Task retrieve(String taskId, OperationContext context) {
        TaskEntity entity = taskService.retrieve(taskId, ParamUtils.convertOperationContext(context));
        return this.convert(entity, context);
    }

    private List<Task> convert(List<TaskEntity> entityList, OperationContext context) {
        modelengine.fit.jane.task.util.OperationContext operationContext = getOperationContext(context);
        return entityList.stream()
                .map(entity -> taskConverter.convert(entity, operationContext))
                .collect(Collectors.toList());
    }

    private Task convert(TaskEntity entity, OperationContext context) {
        return taskConverter.convert(entity, getOperationContext(context));
    }

    private modelengine.fit.jane.task.util.OperationContext getOperationContext(OperationContext context) {
        return modelengine.fit.jane.task.util.OperationContext.custom()
                .tenantId(context.getTenantId())
                .operator(context.getOperator())
                .operatorIp(context.getOperatorIp())
                .build();
    }

    /**
     * 将给定的属性值转换为{@link UndefinableValue}对象。
     *
     * @param attribute 属性值
     * @param emptyPredicate 判断属性值是否为空的断言函数
     * @param <T> 属性值的类型
     * @return 转换完成的 {@link UndefinableValue}
     */
    public static <T> UndefinableValue<T> valueOf(T attribute, Predicate<T> emptyPredicate) {
        if (emptyPredicate.test(attribute)) {
            return UndefinableValue.undefined();
        }
        return UndefinableValue.defined(attribute);
    }
}
