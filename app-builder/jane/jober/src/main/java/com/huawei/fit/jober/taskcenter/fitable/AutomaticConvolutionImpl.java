/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.fitable;

import com.huawei.fit.jane.task.domain.PropertyCategory;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.Pagination;
import com.huawei.fit.jober.OnInstancesCategoryChanged;
import com.huawei.fit.jober.entity.InstanceCategoryChanged;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.ViewMode;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.util.sql.OrderBy;
import com.huawei.fitframework.annotation.Alias;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 自动卷积实现。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-30
 */
@Alias("自动卷积")
@Component
public class AutomaticConvolutionImpl implements OnInstancesCategoryChanged {
    private static final Logger log = Logger.get(AutomaticConvolutionImpl.class);

    private static final int LIMIT = 200;

    private final TaskService taskService;

    private final TaskInstance.Repo repo;

    public AutomaticConvolutionImpl(TaskService taskService, TaskInstance.Repo repo) {
        this.taskService = taskService;
        this.repo = repo;
    }

    private static boolean hasInstanceNotOwningSpecifyCategory(String category,
            PagedResultSet<TaskInstance> results) {
        return results.results().stream()
                .anyMatch(instance -> !instance.categories().contains(category));
    }

    @Override
    @Fitable(id = "2f7f803699714567ba58dc1719c4b346")
    public void process(List<InstanceCategoryChanged> messages) {
        messages.forEach(this::handlerCategoryChanged);
    }

    private void handlerCategoryChanged(InstanceCategoryChanged changed) {
        String taskId = changed.getTaskId();
        OperationContext context = OperationContext.custom().tenantId(changed.getTenant())
                .operator(changed.getOperator()).build();
        TaskEntity task = this.taskService.retrieve(taskId, context);
        TaskInstance instance = this.repo.retrieve(task, changed.getInstanceId(), false, context);
        Optional<String> parentId = Optional.ofNullable(instance.info().get("decomposed_from")).map(Object::toString);
        if (!parentId.isPresent()) {
            return;
        }

        // 查询兄弟姐妹 如果兄弟姐妹都是这个category，父亲不是
        TaskInstance.Filter instanceFilter = filterOfInfo("decomposed_from", parentId.get());
        List<OrderBy> orderBys = Collections.singletonList(OrderBy.of("id"));
        String newCategory = changed.getNewCategory();
        if (checkInstanceNotOwningCategories(task, context, newCategory, instanceFilter, orderBys)) {
            return;
        }
        instanceFilter = filterOfInfo("id", parentId.get());
        // 所有兄弟姐妹都是这个category,查询父亲是否为该category
        PagedResultSet<TaskInstance> parentResultSet = this.repo.list(task, instanceFilter, Pagination.create(0, 1),
                orderBys, ViewMode.LIST, context);
        if (parentResultSet.results().isEmpty()) {
            log.error("Cannot find parent instance by id {}", parentId);
            return;
        }
        TaskInstance parentInstance = parentResultSet.results().get(0);
        if (parentInstance.categories().contains(newCategory)) {
            log.info("Parent instance already have category {}", newCategory);
            return;
        }
        instance.info().forEach((key, value) -> {
            TaskProperty property = task.getPropertyByName(key);
            if (property == null) {
                return;
            }
            PropertyCategory category = property.categories().stream()
                    .filter(current -> current.getValue().equals(value))
                    .filter(current -> Objects.equals(current.getCategory(), newCategory))
                    .findAny().orElse(null);
            if (category == null) {
                return;
            }
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom()
                    .info(Collections.singletonMap(key, category.getValue())).build();
            this.repo.patch(task, parentInstance.id(), declaration, context);
        });
    }

    private boolean checkInstanceNotOwningCategories(TaskEntity task, OperationContext context,
            String newCategory, TaskInstance.Filter instanceFilter, List<OrderBy> orderBys) {
        PagedResultSet<TaskInstance> results = this.repo.list(task, instanceFilter, Pagination.create(0, LIMIT),
                orderBys, ViewMode.LIST, context);
        if (hasInstanceNotOwningSpecifyCategory(newCategory, results)) {
            return true;
        }
        long total = results.pagination().total();
        for (long offset = LIMIT; offset < total; offset += LIMIT) {
            Pagination pagination = Pagination.create(offset, LIMIT);
            results = this.repo.list(task, instanceFilter, pagination, orderBys, ViewMode.LIST, context);
            if (hasInstanceNotOwningSpecifyCategory(newCategory, results)) {
                return true;
            }
        }
        return false;
    }

    private static TaskInstance.Filter filterOfInfo(String key, String value) {
        Map<String, List<String>> infos = Collections.singletonMap(key, Collections.singletonList(value));
        return TaskInstance.Filter.custom().infos(infos).build();
    }
}
