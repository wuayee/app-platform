/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

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
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.taskcenter.declaration.SourceTriggersDeclaration;
import com.huawei.fit.jober.taskcenter.domain.TriggerEntity;
import com.huawei.fit.jober.taskcenter.service.TriggerService;
import modelengine.fitframework.annotation.Component;

import java.util.List;
import java.util.Map;

/**
 * 任务属性触发器相关Controller类。
 *
 * @author 王伟
 * @since 2023-08-08
 */
@Component
@RequestMapping(value = AbstractController.URI_PREFIX + "/tasks/{task_id}", group = "任务属性触发器管理接口")
public class TriggerController extends AbstractController {
    private final TriggerService triggerService;

    /**
     * 构造函数
     *
     * @param authenticator 认证器
     * @param triggerService 触发器服务
     */
    public TriggerController(Authenticator authenticator, TriggerService triggerService) {
        super(authenticator);
        this.triggerService = triggerService;
    }

    /**
     * 添加任务属性触发器
     *
     * @param tenantId tenantId
     * @param taskId taskId
     * @param sourceId sourceId
     * @param request request
     * @param httpRequest httpRequest
     * @return 任务触发器
     */
    @PostMapping(path = "/sources/{source_id}/triggers", summary = "创建任务属性触发器")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> create(@PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("source_id") String sourceId,
            @RequestBody Map<String, Object> request, HttpClassicServerRequest httpRequest) {
        TriggerEntity triggerEntity = triggerService.create(taskId, sourceId, Views.declareTrigger(request),
                this.contextOf(httpRequest, tenantId));
        return Views.viewOf(triggerEntity);
    }

    /**
     * patch修改任务属性触发器
     *
     * @param tenantId tenantId
     * @param taskId taskId
     * @param sourceId sourceId
     * @param triggerId triggerId
     * @param request request
     * @param httpRequest httpRequest
     */
    @PatchMapping(path = "/sources/{source_id}/triggers/{trigger_id}",
            summary = "根据数据源ID和任务属性触发器ID修改任务属性触发器")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patch(@PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("source_id") String sourceId,
            @PathVariable("trigger_id") String triggerId, @RequestBody Map<String, Object> request,
            HttpClassicServerRequest httpRequest) {
        triggerService.patch(taskId, sourceId, triggerId, Views.declareTrigger(request),
                this.contextOf(httpRequest, tenantId));
    }

    /**
     * 删除任务属性触发器
     *
     * @param tenantId tenantId
     * @param taskId taskId
     * @param httpRequest httpRequest
     */
    @DeleteMapping(path = "/triggers", summary = "删除任务属性触发器")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void delete(@PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, HttpClassicServerRequest httpRequest) {
        triggerService.delete(taskId, Views.filterOfTriggers(httpRequest), this.contextOf(httpRequest, tenantId));
    }

    /**
     * 获取任务属性触发器
     *
     * @param tenantId tenantId
     * @param taskId taskId
     * @param sourceId sourceId
     * @param triggerId triggerId
     * @param httpRequest httpRequest
     * @return data
     */
    @GetMapping(path = "/sources/{source_id}/triggers/{trigger_id}",
            summary = "根据数据源ID和任务属性触发器ID获取任务属性触发器详情")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieve(@PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("source_id") String sourceId,
            @PathVariable("trigger_id") String triggerId, HttpClassicServerRequest httpRequest) {
        TriggerEntity triggerEntity = triggerService.retrieve(taskId, sourceId, triggerId,
                this.contextOf(httpRequest, tenantId));
        return Views.viewOf(triggerEntity);
    }

    /**
     * 获取多任务属性触发器
     *
     * @param tenantId tenantId
     * @param taskId taskId
     * @param httpRequest httpRequest
     * @return list
     */
    @GetMapping(path = "/triggers", summary = "获取多任务属性触发器")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> list(@PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            HttpClassicServerRequest httpRequest) {
        Map<String, List<TriggerEntity>> entities = triggerService.list(Views.filterOfTriggers(httpRequest),
                this.contextOf(httpRequest, tenantId));
        return Views.viewOf(entities);
    }

    /**
     * 批量添加任务属性触发器
     *
     * @param tenantId tenantId
     * @param taskId taskId
     * @param declarations declarations
     * @param httpRequest httpRequest
     */
    @PostMapping(path = "/triggers/batch-creations", summary = "批量创建任务属性触发器")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public void batchSave(@PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestBody List<SourceTriggersDeclaration> declarations, HttpClassicServerRequest httpRequest) {
        triggerService.batchSave(taskId, declarations, this.contextOf(httpRequest, tenantId));
    }
}
