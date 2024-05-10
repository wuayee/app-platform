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
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.jober.taskcenter.controller.PortalController;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.plugin.Plugin;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * 为前端页面提供 REST 风格 API。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-17
 */
@Component
@RequestMapping(value = TianzhouAbstractController.URI_PREFIX + "/portal", group = "天舟门户管理接口")
@RequiredArgsConstructor
public class TianzhouPortalController extends TianzhouAbstractController {
    private final PortalController portalController;

    private final Plugin plugin;

    /**
     * titles
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @return Map<String, Object>
     */
    @GetMapping("/groups")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> titles(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId) {
        return View.viewOf(() -> portalController.titles(httpRequest, tenantId), plugin, httpRequest);
    }

    /**
     * count
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @return Map<String, Object>
     */
    @GetMapping("/count")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> count(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId) {
        return View.viewOf(() -> this.portalController.count(httpRequest, httpResponse, tenantId), plugin, httpRequest);
    }

    /**
     * trees
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @return Map<String, Object>
     */
    @GetMapping("/trees")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> trees(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId) {
        return View.viewOf(() -> this.portalController.trees(httpRequest, tenantId), plugin, httpRequest);
    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> createTask(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> this.portalController.createTask(httpRequest, tenantId, request), plugin, httpRequest);
    }

    @PatchMapping("/tasks/{task_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> patchTask(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> this.portalController.patchTask(httpRequest, tenantId, taskId, request), plugin,
                httpRequest);
    }

    @DeleteMapping("/tasks/{task_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> deleteTask(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId) {
        return View.viewOf(() -> this.portalController.deleteTask(httpRequest, tenantId, taskId), plugin, httpRequest);
    }

    @GetMapping("/tasks/{task_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieveTask(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId) {
        return View.viewOf(() -> this.portalController.retrieveTask(httpRequest, tenantId, taskId), plugin,
                httpRequest);
    }

    @PostMapping("/tasks/{task_id}/properties")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> createTaskProperty(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> this.portalController.createTaskProperty(httpRequest, tenantId, taskId, request),
                plugin, httpRequest);
    }

    @PatchMapping("/tasks/{task_id}/properties/{property_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> patchTaskProperty(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("property_id") String propertyId, @RequestBody Map<String, Object> request) {
        return View.viewOf(
                () -> this.portalController.patchTaskProperty(httpRequest, tenantId, taskId, propertyId, request),
                plugin,
                httpRequest);
    }

    @PatchMapping("/tasks/{task_id}/properties")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> patchTaskProperties(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestBody Map<String, Map<String, Object>> request) {
        return View.viewOf(() -> this.portalController.patchProperties(httpRequest, tenantId, taskId, request), plugin,
                httpRequest);
    }

    @DeleteMapping("/tasks/{task_id}/properties/{property_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> deleteTaskProperty(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("property_id") String propertyId) {
        return View.viewOf(() -> this.portalController.deleteTaskProperty(httpRequest, tenantId, taskId, propertyId),
                plugin, httpRequest);
    }

    @PostMapping("/tasks/{task_id}/types")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> createTaskType(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> this.portalController.createTaskType(httpRequest, tenantId, taskId, request), plugin,
                httpRequest);
    }

    @PatchMapping("/tasks/{task_id}/types/{type_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> patchTaskType(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("type_id") String typeId, @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> this.portalController.patchTaskType(httpRequest, tenantId, taskId, typeId, request),
                plugin, httpRequest);
    }

    @DeleteMapping("/tasks/{task_id}/types/{type_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> deleteTaskType(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("type_id") String typeId) {
        return View.viewOf(() -> this.portalController.deleteTaskType(httpRequest, tenantId, taskId, typeId), plugin,
                httpRequest);
    }

    @PostMapping("/tasks/{task_id}/types/{type_id}/sources")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> createTaskSource(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("type_id") String typeId, @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> this.portalController.createTaskSource(httpRequest, tenantId, taskId, typeId, request),
                plugin, httpRequest);
    }

    @PatchMapping("/tasks/{task_id}/types/{type_id}/sources/{source_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> patchTaskSource(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("type_id") String typeId, @PathVariable("source_id") String sourceId,
            @RequestBody Map<String, Object> request) {
        return View.viewOf(
                () -> this.portalController.patchTaskSource(httpRequest, tenantId, taskId, typeId, sourceId, request),
                plugin, httpRequest);
    }

    @DeleteMapping("/tasks/{task_id}/types/{type_id}/sources/{source_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> deleteTaskSource(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("type_id") String typeId, @PathVariable("source_id") String sourceId) {
        return View.viewOf(
                () -> this.portalController.deleteTaskSource(httpRequest, tenantId, taskId, typeId, sourceId), plugin,
                httpRequest);
    }

    @GetMapping("/tasks/{task_id}/types/{type_id}/sources")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> listTaskSources(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("type_id") String typeId) {
        return View.viewOf(() -> this.portalController.listTaskSources(httpRequest, tenantId, taskId, typeId), plugin,
                httpRequest);
    }

    @PostMapping(path = "/authorizations", summary = "创建三方授权")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> createAuthorization(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> this.portalController.createAuthorization(httpRequest, tenantId, request),
                plugin, httpRequest);
    }

    @PatchMapping(path = "/authorizations/{authorization_id}", summary = "修改三方授权")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public Map<String, Object> patchAuthorization(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("authorization_id") String authorizationId,
            @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> this.portalController.patchAuthorization(httpRequest, tenantId, authorizationId,
                request), plugin, httpRequest);
    }

    @DeleteMapping(path = "/authorizations/{authorization_id}", summary = "修改三方授权")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public Map<String, Object> deleteAuthorization(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("authorization_id") String authorizationId) {
        return View.viewOf(() -> this.portalController.deleteAuthorization(httpRequest, tenantId, authorizationId),
                plugin, httpRequest);
    }

    @GetMapping(path = "/authorizations/{authorization_id}", summary = "检索三方授权")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieveAuthorization(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("authorization_id") String authorizationId) {
        return View.viewOf(() -> this.portalController.retrieveAuthorization(httpRequest, tenantId, authorizationId),
                plugin, httpRequest);
    }

    @GetMapping(path = "/authorizations", summary = "查询三方授权")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> listAuthorizations(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId,
            @RequestParam("offset") long offset, @RequestParam("limit") int limit) {
        return View.viewOf(() -> this.portalController.listAuthorizations(httpRequest, tenantId, offset, limit),
                plugin, httpRequest);
    }

    @GetMapping(path = "/tasks/{task_id}/instances", summary = "分页查询任务实例列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> listInstances(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestParam(name = "viewType", required = false) String viewType, @RequestParam("offset") long offset,
            @RequestParam("limit") int limit, @RequestParam(name = "deleted", required = false) String deleted) {
        return View.viewOf(
                () -> this.portalController.listInstances(httpRequest, tenantId, taskId, viewType, offset, limit,
                        deleted), plugin, httpRequest);
    }

    /**
     * list
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param instanceId instanceId
     * @param offset offset
     * @param limit limit
     * @return Map<String, Object>
     */
    @GetMapping(path = "/instances/{instance_id}/relations", summary = "分页查询关联任务列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> listInstanceRelations(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("instance_id") String instanceId,
            @RequestParam("offset") long offset, @RequestParam("limit") int limit) {
        return View.viewOf(
                () -> this.portalController.listInstanceRelations(httpRequest, tenantId, instanceId, offset, limit),
                plugin, httpRequest);
    }
}
