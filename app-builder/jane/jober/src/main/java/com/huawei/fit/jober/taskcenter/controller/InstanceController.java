/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import static com.huawei.fit.jober.common.ErrorCodes.FILTER_IS_EMPTY;
import static com.huawei.fit.jober.taskcenter.controller.Views.declareInstance;
import static com.huawei.fit.jober.taskcenter.controller.Views.filterOfInstances;
import static com.huawei.fit.jober.taskcenter.controller.Views.viewOf;
import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PatchMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.Pagination;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.ViewMode;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fit.jober.taskcenter.util.sql.OrderBy;
import com.huawei.fitframework.annotation.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 为任务实例提供 REST 风格 API。
 *
 * @author 梁济时
 * @since 2023-08-14
 */
@Component
@RequestMapping(value = AbstractController.URI_PREFIX + "/tasks/{task_id}/instances1", group = "任务实例管理接口")
public class InstanceController extends AbstractController {
    private final TaskInstance.Repo repo;

    private final TaskService taskService;

    /**
     * 构造函数
     *
     * @param authenticator 为系统提供认证器
     * @param repo {@link TaskInstance.Repo}实例
     * @param taskService {@link TaskService}为任务定义提供管理
     */
    public InstanceController(Authenticator authenticator, TaskInstance.Repo repo, TaskService taskService) {
        super(authenticator);
        this.repo = repo;
        this.taskService = taskService;
    }

    private static void putInfoDefaultValue(Map<String, Object> request, String key, Object value) {
        Map<String, Object> info = cast(request.get("info"));
        if (info != null && !info.containsKey(key)) {
            info.put(key, value);
        }
    }

    private static boolean hasAnyFilter(TaskInstance.Filter filter) {
        return !filter.ids().isEmpty() || !filter.typeIds().isEmpty() || !filter.sourceIds().isEmpty()
                || !filter.infos().isEmpty() || !filter.tags().isEmpty();
    }

    /**
     * create
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param request request
     * @return Map<String, Object>
     */
    @PostMapping(summary = "保存单个任务实例")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> create(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @RequestBody Map<String, Object> request) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskEntity task = this.taskService.retrieve(taskId, context);
        LocalDateTime now = LocalDateTime.now();
        putInfoDefaultValue(request, "created_by", context.operator());
        putInfoDefaultValue(request, "created_date", now);
        putInfoDefaultValue(request, "modified_by", context.operator());
        putInfoDefaultValue(request, "modified_date", now);
        TaskInstance.Declaration declaration = declareInstance(request, task);
        TaskInstance instance = this.repo.create(task, declaration, context);
        if (instance.info().get("owner") != null) {
            if (instance.info().get("owner") instanceof ArrayList) {
                instance.info().put("owner", String.join(",", (ArrayList) instance.info().get("owner")));
            }
        }
        return viewOf(instance);
    }

    /**
     * patch
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param instanceId instanceId
     * @param request request
     */
    @PatchMapping(value = "/{instance_id}", summary = "根据任务实例ID更新任务实例")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patch(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("instance_id") String instanceId,
            @RequestBody Map<String, Object> request) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskEntity task = this.taskService.retrieve(taskId, context);
        LocalDateTime now = LocalDateTime.now();
        putInfoDefaultValue(request, "modified_by", context.operator());
        putInfoDefaultValue(request, "modified_date", now);
        TaskInstance.Declaration declaration = declareInstance(request, task);
        this.repo.patch(task, instanceId, declaration, context);
    }

    /**
     * delete
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param instanceId instanceId
     */
    @DeleteMapping(value = "/{instance_id}", summary = "根据任务实例ID删除任务实例")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void delete(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("instance_id") String instanceId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskEntity task = this.taskService.retrieve(taskId, context);
        this.repo.delete(task, instanceId, context);
    }

    /**
     * retrieve
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param instanceId instanceId
     * @param deleted 表示是否查询删除表，true则代表要查询的是删除表。
     * @return Map<String, Object>
     */
    @GetMapping(value = "/{instance_id}", summary = "根据任务实例ID查询任务实例详情")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieve(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("instance_id") String instanceId,
            @RequestParam(name = "deleted", required = false) String deleted) {
        boolean isDeleted = Boolean.parseBoolean(deleted);
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskEntity task = this.taskService.retrieve(taskId, context);
        TaskInstance instance = this.repo.retrieve(task, instanceId, isDeleted, context);
        if (instance.info().get("owner") != null) {
            if (instance.info().get("owner") instanceof ArrayList) {
                instance.info().put("owner", String.join(",", (ArrayList) instance.info().get("owner")));
            }
        }
        return viewOf(instance);
    }

    /**
     * list
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param viewType viewType
     * @param offset offset
     * @param limit limit
     * @param deleted 表示是否查询删除表，true则代表要查询的是删除表。
     * @return Map<String, Object>
     */
    @GetMapping(summary = "分页查询任务实例列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> list(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @RequestParam(name = "viewType", required = false) String viewType,
            @RequestParam("offset") long offset, @RequestParam("limit") int limit,
            @RequestParam(name = "deleted", required = false) String deleted) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        ViewMode viewMode = Enums.parse(ViewMode.class, nullIf(viewType, ViewMode.LIST.name()));
        TaskInstance.Filter filter = filterOfInstances(httpRequest, Boolean.parseBoolean(deleted));
        if (viewMode == ViewMode.TREE) {
            if (!hasAnyFilter(filter)) {
                throw new BadRequestException(FILTER_IS_EMPTY);
            }
        }
        List<OrderBy> orderBys = httpRequest.queries()
                .all("order_by")
                .stream()
                .map(OrderBy::parse)
                .collect(Collectors.toList());
        TaskEntity task = this.taskService.retrieve(taskId, context);
        PagedResultSet<TaskInstance> results = this.repo.list(task, filter, Pagination.create(offset, limit), orderBys,
                viewMode, context);
        List<TaskInstance> instanceList = results.results()
                .stream()
                .peek(this::convertListOwner)
                .collect(Collectors.toList());
        Map<String, Object> view = viewOf(PagedResultSet.create(instanceList, results.pagination()), "instances",
                Views::viewOf);
        Map<String, Object> actualView = new LinkedHashMap<>(view.size() + 1);
        Map<String, Object> taskView = viewOf(task);
        if (viewMode == ViewMode.TREE) {
            appendTypeProperty(taskView);
        }
        actualView.put("task", taskView);
        actualView.putAll(view);
        return actualView;
    }

    /**
     * recover
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param instanceId instanceId
     */
    @PostMapping(value = "/{instance_id}/recoveries", summary = "根据任务实例ID恢复被删除的任务实例")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void recover(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("instance_id") String instanceId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskEntity task = this.taskService.retrieve(taskId, context);
        this.repo.recover(task, instanceId, context);
    }

    private void convertListOwner(TaskInstance instance) {
        if (instance.info().get("owner") != null) {
            if (instance.info().get("owner") instanceof ArrayList) {
                instance.info().put("owner", String.join(",", (ArrayList) instance.info().get("owner")));
            }
        }
    }
}
