/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.tianzhou;

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
import com.huawei.fit.jober.taskcenter.controller.InstanceController;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.plugin.Plugin;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * 为任务实例提供 REST 风格 API。
 *
 * @author 梁济时
 * @since 2023-08-14
 */
@Component
@RequestMapping(value = TianzhouAbstractController.URI_PREFIX + "/tasks/{task_id}/instances1",
        group = "天舟任务实例管理接口")
@RequiredArgsConstructor
public class TianzhouInstanceController extends TianzhouAbstractController {
    private final InstanceController instanceController;

    private final Plugin plugin;

    /**
     * create
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param request request
     * @return Map<String, Object>
     */
    @PostMapping(summary = "创建任务实例")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> create(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> instanceController.create(httpRequest, tenantId, taskId, request), plugin,
                httpRequest);
    }

    /**
     * patch
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param instanceId instanceId
     * @param request request
     * @return Map<String, Object>
     */
    @PatchMapping(value = "/{instance_id}", summary = "根据任务实例ID更新任务实例")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> patch(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("instance_id") String instanceId,
            @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> instanceController.patch(httpRequest, tenantId, taskId, instanceId, request), plugin,
                httpRequest);
    }

    /**
     * retrieve
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param instanceId instanceId
     * @param isDeleted 表示是否查询删除表，true则代表要查询的是删除表。
     * @return Map<String, Object>
     */
    @GetMapping(value = "/{instance_id}", summary = "根据任务实例ID查询任务实例")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieve(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("instance_id") String instanceId,
            @RequestParam(name = "deleted", required = false) String isDeleted) {
        return View.viewOf(() -> instanceController.retrieve(httpRequest, tenantId, taskId, instanceId, isDeleted),
                plugin, httpRequest);
    }

    /**
     * list
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param offset offset
     * @param limit limit
     * @param isDeleted 表示是否查询删除表，true则代表要查询的是删除表。
     * @param viewType viewType
     * @return Map<String, Object>
     */
    @GetMapping(summary = "分页查询任务实例列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> list(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @RequestParam(required = false, value = "viewType") String viewType,
            @RequestParam("offset") long offset, @RequestParam("limit") int limit,
            @RequestParam(name = "deleted", required = false) String isDeleted) {
        return View.viewOf(
                () -> instanceController.list(httpRequest, tenantId, taskId, viewType, offset, limit, isDeleted),
                plugin, httpRequest);
    }

    /**
     * delete
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param instanceId instanceId
     * @return Map<String, Object>
     */
    @DeleteMapping(value = "/{instance_id}", summary = "根据任务实例ID删除任务实例")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> delete(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("instance_id") String instanceId) {
        return View.viewOf(() -> instanceController.delete(httpRequest, tenantId, taskId, instanceId), plugin,
                httpRequest);
    }

    /**
     * recover
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param instanceId instanceId
     * @return Map<String, Object>
     */
    @PostMapping(value = "/{instance_id}", summary = "根据任务实例ID恢复被删除的任务实例")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> recover(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("instance_id") String instanceId) {
        return View.viewOf(() -> instanceController.recover(httpRequest, tenantId, taskId, instanceId), plugin,
                httpRequest);
    }
}
