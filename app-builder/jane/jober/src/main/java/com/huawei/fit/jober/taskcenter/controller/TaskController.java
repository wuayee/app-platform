/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

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
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.taskcenter.declaration.TaskDeclaration;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.model.RangedResultSet;

import java.util.Map;

/**
 * 为任务定义提供 REST 风格 API。
 *
 * @author 梁济时
 * @since 2023-08-08
 */
@Component
@RequestMapping(value = AbstractController.URI_PREFIX + "/tasks", group = "任务定义管理接口")
public class TaskController extends AbstractController {
    private final TaskService taskService;

    /**
     * 构造函数
     *
     * @param authenticator 认证器
     * @param taskService 任务服务
     */
    public TaskController(Authenticator authenticator, TaskService taskService) {
        super(authenticator);
        this.taskService = taskService;
    }

    /**
     * create
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param request request
     * @return Map<String, Object>
     */
    @PostMapping(summary = "创建任务定义")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> create(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> request) {
        TaskDeclaration declaration = Views.declareTask(request);
        TaskEntity entity = this.taskService.create(declaration, this.contextOf(httpRequest, tenantId));
        return Views.viewOf(entity);
    }

    /**
     * patch
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param taskId taskId
     * @param request request
     */
    @PatchMapping(value = "/{task_id}", summary = "根据任务定义ID更新任务定义")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patch(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestBody Map<String, Object> request) {
        TaskDeclaration declaration = Views.declareTask(request);
        this.taskService.patch(taskId, declaration, this.contextOf(httpRequest, tenantId));
    }

    /**
     * delete
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param taskId taskId
     */
    @DeleteMapping(value = "/{task_id}", summary = "根据任务定义ID删除任务定义")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void delete(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId) {
        this.taskService.delete(taskId, this.contextOf(httpRequest, tenantId));
    }

    /**
     * retrieve
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param taskId taskId
     * @return Map<String, Object>
     */
    @GetMapping(value = "/{task_id}", summary = "根据任务ID查询任务定义详情")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieve(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId) {
        TaskEntity entity = this.taskService.retrieve(taskId, this.contextOf(httpRequest, tenantId));
        return Views.viewOf(entity);
    }

    /**
     * list
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param offset offset
     * @param limit limit
     * @return Map<String, Object>
     */
    @GetMapping(summary = "分页查询任务定义列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> list(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestParam("offset") long offset,
            @RequestParam("limit") int limit) {
        // 下游应用切换租户期间需要做特殊处理，暂不加租户相关查询
        RangedResultSet<TaskEntity> entities = this.taskService.listForApplication(Views.filterOfTasks(httpRequest),
                offset,
                limit,
                this.contextOf(httpRequest, tenantId));
        return Views.viewOf(entities, "tasks", Views::viewOf);
    }
}
