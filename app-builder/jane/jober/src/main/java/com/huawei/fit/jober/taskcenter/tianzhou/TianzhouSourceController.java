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
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.jober.taskcenter.controller.SourceController;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.plugin.Plugin;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * 为任务数据源提供 REST 风格 API。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-09
 */
@Component
@RequestMapping(value = TianzhouAbstractController.URI_PREFIX + "/tasks/{task_id}/sources",
        group = "天舟任务数据源管理接口")
@RequiredArgsConstructor
public class TianzhouSourceController extends TianzhouAbstractController {
    private final SourceController sourceController;

    private final Plugin plugin;

    /**
     * create
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param taskId taskId
     * @param request request
     * @return Map<String, Object>
     */
    @PostMapping(summary = "创建任务来源")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> create(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> sourceController.create(httpRequest, httpResponse, tenantId, taskId, request), plugin,
                httpRequest);
    }

    /**
     * patch
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param taskId taskId
     * @param sourceId sourceId
     * @param request request
     * @return Map<String, Object>
     */
    @PatchMapping(value = "/{source_id}", summary = "根据任务来源ID更新任务来源")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> patch(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("source_id") String sourceId, @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> sourceController.patch(httpRequest, httpResponse, tenantId, taskId, sourceId, request),
                plugin, httpRequest);
    }

    /**
     * delete
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param taskId taskId
     * @param sourceId sourceId
     * @return Map<String, Object>
     */
    @DeleteMapping(value = "/{source_id}", summary = "根据任务来源ID删除任务来源")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> delete(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("source_id") String sourceId) {
        return View.viewOf(() -> sourceController.delete(httpRequest, httpResponse, tenantId, taskId, sourceId), plugin,
                httpRequest);
    }

    /**
     * retrieve
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param taskId taskId
     * @param sourceId sourceId
     * @return Map<String, Object>
     */
    @GetMapping(value = "/{source_id}", summary = "根据任务来源ID查询任务来源详情")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieve(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("source_id") String sourceId) {
        return View.viewOf(() -> sourceController.retrieve(httpRequest, httpResponse, tenantId, taskId, sourceId),
                plugin, httpRequest);
    }

    /**
     * list
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param taskId taskId
     * @return Map<String, Object>
     */
    @GetMapping(summary = "查询任务来源列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> list(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId) {
        return View.viewOf(() -> sourceController.list(httpRequest, httpResponse, tenantId, taskId), plugin,
                httpRequest);
    }
}
