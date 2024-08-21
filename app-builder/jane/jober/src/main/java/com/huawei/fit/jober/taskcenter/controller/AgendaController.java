/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import static com.huawei.fit.jober.taskcenter.controller.Views.filterOfInstances;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.Pagination;
import com.huawei.fit.jane.task.util.PaginationResult;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.service.TaskAgendaService;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.util.sql.OrderBy;
import modelengine.fitframework.annotation.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author 罗书强
 * @since 2024-01-22
 */
@Component
@RequestMapping(value = "/v1/task-templates/{task_template_id}/instances", group = "个人待办管理接口")
public class AgendaController extends AbstractController {
    private final TaskAgendaService taskagendaService;

    private final TaskService taskService;

    /**
     * 全参构造函数
     *
     * @param authenticator 校验器
     * @param taskAgendaService 任务排列服务
     * @param taskService 任务服务
     */
    public AgendaController(Authenticator authenticator, TaskAgendaService taskAgendaService, TaskService taskService) {
        super(authenticator);
        this.taskagendaService = taskAgendaService;
        this.taskService = taskService;
    }

    /**
     * list
     *
     * @param httpRequest httpRequest
     * @param offset offset
     * @param limit limit
     * @param deleted deleted
     * @param templateId templateId
     * @return Map<String, Object>
     */
    @GetMapping(summary = "根据模板和筛选条件查询对应待办")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> listAgenda(HttpClassicServerRequest httpRequest, @RequestParam("offset") long offset,
            @RequestParam("limit") int limit, @RequestParam(name = "deleted", required = false) String deleted,
            @PathVariable("task_template_id") String templateId) {
        OperationContext context = this.contextOf(httpRequest, null);
        TaskInstance.Filter filter = filterOfInstances(httpRequest, Boolean.parseBoolean(deleted));
        List<OrderBy> orderBys = httpRequest.queries()
                .all("order_by")
                .stream()
                .map(OrderBy::parse)
                .collect(Collectors.toList());
        List<String> taskIds = this.taskagendaService.listTaskIds(filter, Pagination.create(offset, limit), templateId,
                context, orderBys);
        if (taskIds.isEmpty()) {
            return buildMultiTaskInstanceView(
                    PagedResultSet.create(Collections.emptyList(), PaginationResult.create(offset, limit, 0)),
                    Collections.emptyList());
        }
        List<TaskEntity> taskEntityList = taskService.listTaskEntities(taskIds, context);
        PagedResultSet<TaskInstance> results = taskagendaService.listAllAgenda(filter, Pagination.create(offset, limit),
                templateId, context, taskEntityList, orderBys);
        return buildMultiTaskInstanceView(results, taskEntityList);
    }
}
