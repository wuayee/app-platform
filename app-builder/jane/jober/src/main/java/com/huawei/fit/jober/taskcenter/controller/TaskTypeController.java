/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.domain.TaskType;

import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PatchMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;

import java.util.Map;

/**
 * 为任务类型的管理提供 REST 风格 API。
 *
 * @author 梁济时
 * @since 2023-09-13
 */
@Component
@RequestMapping(value = AbstractController.URI_PREFIX + "/tasks/{task_id}/types", group = "任务类型管理接口")
public class TaskTypeController extends AbstractController {
    private final TaskType.Repo repo;

    /**
     * 构造函数
     *
     * @param authenticator 认证器
     * @param repo 任务类型repo
     */
    public TaskTypeController(Authenticator authenticator, TaskType.Repo repo) {
        super(authenticator);
        this.repo = repo;
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
    @PostMapping(summary = "创建任务类型")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> create(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @RequestBody Map<String, Object> request) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskType.Declaration declaration = Views.declareTaskType(request);
        TaskType type = this.repo.create(taskId, declaration, context);
        return Views.viewOf(type);
    }

    /**
     * patch
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param typeId typeId
     * @param request request
     */
    @PatchMapping(value = "/{type_id}", summary = "根据任务类型ID更新任务类型")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patch(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("type_id") String typeId,
            @RequestBody Map<String, Object> request) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskType.Declaration declaration = Views.declareTaskType(request);
        this.repo.patch(taskId, typeId, declaration, context);
    }

    /**
     * delete
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param typeId typeId
     */
    @DeleteMapping(value = "/{type_id}", summary = "根据任务类型ID删除任务类型")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void delete(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("type_id") String typeId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        this.repo.delete(taskId, typeId, context);
    }

    /**
     * retrieve
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param typeId typeId
     * @return Map<String, Object>
     */
    @GetMapping(value = "/{type_id}", summary = "根据任务类型ID查询任务类型")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieve(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("type_id") String typeId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskType type = this.repo.retrieve(taskId, typeId, context);
        return Views.viewOf(type);
    }
}
