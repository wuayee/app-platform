/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.tianzhou;

import com.huawei.fit.jober.taskcenter.controller.TaskController;

import lombok.RequiredArgsConstructor;
import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PatchMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.plugin.Plugin;

import java.util.Map;

/**
 * 为任务定义提供 REST 风格 API。
 *
 * @author 梁济时
 * @since 2023-08-08
 */
@Component
@RequestMapping(value = TianzhouAbstractController.URI_PREFIX + "/tasks", group = "天舟任务定义管理接口")
@RequiredArgsConstructor
public class TianzhouTaskController extends TianzhouAbstractController {
    private final TaskController taskController;

    private final Plugin plugin;

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
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> create(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> taskController.create(httpRequest, httpResponse, tenantId, request), plugin,
                httpRequest);
    }

    /**
     * patch
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param taskId taskId
     * @param request request
     * @return Map<String, Object>
     */
    @PatchMapping(value = "/{task_id}", summary = "根据任务定义ID更新任务定义")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> patch(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> taskController.patch(httpRequest, httpResponse, tenantId, taskId, request), plugin,
                httpRequest);
    }

    /**
     * delete
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param taskId taskId
     * @return Map<String, Object>
     */
    @DeleteMapping(value = "/{task_id}", summary = "根据任务定义ID删除任务定义")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> delete(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId) {
        return View.viewOf(() -> taskController.delete(httpRequest, httpResponse, tenantId, taskId), plugin,
                httpRequest);
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
    @GetMapping(value = "/{task_id}", summary = "根据任务定义ID查询任务定义详情")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieve(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId) {
        return View.viewOf(() -> taskController.retrieve(httpRequest, httpResponse, tenantId, taskId), plugin,
                httpRequest);
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
        return View.viewOf(() -> taskController.list(httpRequest, httpResponse, tenantId, offset, limit), plugin,
                httpRequest);
    }
}
