/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.tianzhou;

import com.huawei.fit.jober.taskcenter.controller.TriggerController;
import com.huawei.fit.jober.taskcenter.declaration.SourceTriggersDeclaration;

import lombok.RequiredArgsConstructor;
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
import modelengine.fitframework.plugin.Plugin;

import java.util.List;
import java.util.Map;

/**
 * 任务属性触发器相关Controller类。
 *
 * @author 王伟
 * @since 2023-08-08
 */
@Component
@RequestMapping(value = TianzhouAbstractController.URI_PREFIX + "/tasks/{task_id}",
        group = "天舟任务属性触发器管理接口")
@RequiredArgsConstructor
public class TianzhouTriggerController extends TianzhouAbstractController {
    private final TriggerController triggerController;

    private final Plugin plugin;

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
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> create(@PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("source_id") String sourceId,
            @RequestBody Map<String, Object> request, HttpClassicServerRequest httpRequest) {
        return View.viewOf(() -> triggerController.create(tenantId, taskId, sourceId, request, httpRequest), plugin,
                httpRequest);
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
     * @return 属性触发器
     */
    @PatchMapping(path = "/sources/{source_id}/triggers/{trigger_id}",
            summary = "根据数据源ID和任务属性触发器ID修改任务属性触发器")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> patch(@PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("source_id") String sourceId, @PathVariable("trigger_id") String triggerId,
            @RequestBody Map<String, Object> request, HttpClassicServerRequest httpRequest) {
        return View.viewOf(() -> triggerController.patch(tenantId, taskId, sourceId, triggerId, request, httpRequest),
                plugin, httpRequest);
    }

    /**
     * 删除任务属性触发器
     *
     * @param tenantId tenantId
     * @param taskId taskId
     * @param httpRequest httpRequest
     * @return 删除结果
     */
    @DeleteMapping(path = "/triggers", summary = "删除任务属性触发器")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> delete(@PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, HttpClassicServerRequest httpRequest) {
        return View.viewOf(() -> triggerController.delete(tenantId, taskId, httpRequest), plugin, httpRequest);
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
        return View.viewOf(() -> triggerController.retrieve(tenantId, taskId, sourceId, triggerId, httpRequest), plugin,
                httpRequest);
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
        return View.viewOf(() -> triggerController.list(tenantId, taskId, httpRequest), plugin, httpRequest);
    }

    /**
     * 批量添加任务属性触发器
     *
     * @param tenantId tenantId
     * @param taskId taskId
     * @param declarations declarations
     * @param httpRequest httpRequest
     * @return 任务触发器
     */
    @PostMapping(path = "/triggers/batch-creations", summary = "批量创建任务属性触发器")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> batchSave(@PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @RequestBody List<SourceTriggersDeclaration> declarations,
            HttpClassicServerRequest httpRequest) {
        return View.viewOf(() -> triggerController.batchSave(tenantId, taskId, declarations, httpRequest), plugin,
                httpRequest);
    }
}
