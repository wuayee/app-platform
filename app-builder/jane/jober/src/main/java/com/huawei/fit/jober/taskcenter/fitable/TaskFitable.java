/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.fitable;

import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.TaskService;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.common.util.ParamUtils;
import com.huawei.fit.jober.entity.OperationContext;
import com.huawei.fit.jober.entity.TaskFilter;
import com.huawei.fit.jober.entity.task.Task;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.TaskConverter;
import com.huawei.fitframework.annotation.Alias;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.util.CollectionUtils;

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
    private final com.huawei.fit.jober.taskcenter.service.TaskService taskService;

    private final TaskConverter taskConverter;

    public TaskFitable(com.huawei.fit.jober.taskcenter.service.TaskService taskService, TaskConverter taskConverter) {
        this.taskService = taskService;
        this.taskConverter = taskConverter;
    }

    @Override
    @Fitable(id = "3cfd2c76e00943b9abc3c2d2cbf7ad49")
    public RangedResultSet<Task> list(TaskFilter filter, long offset, int limit, OperationContext context) {
        com.huawei.fit.jober.taskcenter.filter.TaskFilter taskFilter =
                new com.huawei.fit.jober.taskcenter.filter.TaskFilter();
        taskFilter.setIds(valueOf(filter.getIds(), CollectionUtils::isEmpty));
        taskFilter.setNames(valueOf(filter.getNames(), CollectionUtils::isEmpty));
        taskFilter.setTemplateIds(valueOf(filter.getTemplateIds(), CollectionUtils::isEmpty));
        taskFilter.setCategories(valueOf(filter.getCategories(), CollectionUtils::isEmpty));
        taskFilter.setCreators(valueOf(filter.getCreators(), CollectionUtils::isEmpty));
        taskFilter.setOrderBys(valueOf(filter.getOrderBys(), CollectionUtils::isEmpty));
        com.huawei.fitframework.model.RangedResultSet<com.huawei.fit.jober.taskcenter.domain.TaskEntity>
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
        com.huawei.fit.jane.task.util.OperationContext operationContext = getOperationContext(context);
        return entityList.stream()
                .map(entity -> taskConverter.convert(entity, operationContext))
                .collect(Collectors.toList());
    }

    private Task convert(TaskEntity entity, OperationContext context) {
        return taskConverter.convert(entity, getOperationContext(context));
    }

    private com.huawei.fit.jane.task.util.OperationContext getOperationContext(OperationContext context) {
        return com.huawei.fit.jane.task.util.OperationContext.custom()
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
