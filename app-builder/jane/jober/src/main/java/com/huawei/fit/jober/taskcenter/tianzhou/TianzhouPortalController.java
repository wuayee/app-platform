/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.tianzhou;

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
import com.huawei.fit.jober.taskcenter.controller.PortalController;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.plugin.Plugin;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * 为前端页面提供 REST 风格 API。
 *
 * @author 陈镕希
 * @since 2023-08-17
 */
@Component
@RequestMapping(value = TianzhouAbstractController.URI_PREFIX + "/portal", group = "天舟门户管理接口")
@RequiredArgsConstructor
public class TianzhouPortalController extends TianzhouAbstractController {
    private final PortalController portalController;

    private final Plugin plugin;

    /**
     * 查询任务group
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @return 查询结果
     */
    @GetMapping("/groups")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> titles(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId) {
        return View.viewOf(() -> portalController.titles(httpRequest, tenantId), plugin, httpRequest);
    }

    /**
     * 查询任务实例数量
     *
     * @param httpRequest http请求上下文
     * @param httpResponse http响应上下文
     * @param tenantId 租户Id
     * @return 查询结果
     */
    @GetMapping("/count")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> count(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId) {
        return View.viewOf(() -> this.portalController.count(httpRequest, httpResponse, tenantId), plugin, httpRequest);
    }

    /**
     * 查询任务树形结构
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @return 查询结果
     */
    @GetMapping("/trees")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> trees(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId) {
        return View.viewOf(() -> this.portalController.trees(httpRequest, tenantId), plugin, httpRequest);
    }

    /**
     * 创建任务定义
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param request 请求体
     * @return 创建结果
     */
    @PostMapping("/tasks")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> createTask(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> this.portalController.createTask(httpRequest, tenantId, request), plugin, httpRequest);
    }

    /**
     * 修改任务定义
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param taskId 任务定义Id
     * @param request 请求体
     * @return 更新结果
     */
    @PatchMapping("/tasks/{task_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> patchTask(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> this.portalController.patchTask(httpRequest, tenantId, taskId, request), plugin,
                httpRequest);
    }

    /**
     * 删除任务定义
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param taskId 任务定义Id
     * @return 删除结果
     */
    @DeleteMapping("/tasks/{task_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> deleteTask(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId) {
        return View.viewOf(() -> this.portalController.deleteTask(httpRequest, tenantId, taskId), plugin, httpRequest);
    }

    /**
     * 查询任务定义
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param taskId 任务定义Id
     * @return 查询结果
     */
    @GetMapping("/tasks/{task_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieveTask(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId) {
        return View.viewOf(() -> this.portalController.retrieveTask(httpRequest, tenantId, taskId), plugin,
                httpRequest);
    }

    /**
     * 创建任务属性
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param taskId 任务定义Id
     * @param request 请求体
     * @return 创建结果
     */
    @PostMapping("/tasks/{task_id}/properties")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> createTaskProperty(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> this.portalController.createTaskProperty(httpRequest, tenantId, taskId, request),
                plugin, httpRequest);
    }

    /**
     * 修改任务属性
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param taskId 任务定义Id
     * @param propertyId 任务属性Id
     * @param request 请求体
     * @return 修改结果
     */
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

    /**
     * 批量修改任务属性
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param taskId 任务定义Id
     * @param request 请求体
     * @return 修改结果
     */
    @PatchMapping("/tasks/{task_id}/properties")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> patchTaskProperties(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestBody Map<String, Map<String, Object>> request) {
        return View.viewOf(() -> this.portalController.patchProperties(httpRequest, tenantId, taskId, request), plugin,
                httpRequest);
    }

    /**
     * 删除任务属性
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param taskId 任务定义Id
     * @param propertyId 任务属性Id
     * @return 删除结果
     */
    @DeleteMapping("/tasks/{task_id}/properties/{property_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> deleteTaskProperty(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("property_id") String propertyId) {
        return View.viewOf(() -> this.portalController.deleteTaskProperty(httpRequest, tenantId, taskId, propertyId),
                plugin, httpRequest);
    }

    /**
     * 创建任务类型
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param taskId 任务定义Id
     * @param request 请求体
     * @return 创建结果
     */
    @PostMapping("/tasks/{task_id}/types")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> createTaskType(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> this.portalController.createTaskType(httpRequest, tenantId, taskId, request), plugin,
                httpRequest);
    }

    /**
     * 修改任务类型
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param taskId 任务定义Id
     * @param typeId 任务类型Id
     * @param request 请求体
     * @return 修改结果
     */
    @PatchMapping("/tasks/{task_id}/types/{type_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> patchTaskType(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("type_id") String typeId, @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> this.portalController.patchTaskType(httpRequest, tenantId, taskId, typeId, request),
                plugin, httpRequest);
    }

    /**
     * 删除任务类型
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param taskId 任务定义Id
     * @param typeId 任务类型Id
     * @return 删除结果
     */
    @DeleteMapping("/tasks/{task_id}/types/{type_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> deleteTaskType(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("type_id") String typeId) {
        return View.viewOf(() -> this.portalController.deleteTaskType(httpRequest, tenantId, taskId, typeId), plugin,
                httpRequest);
    }

    /**
     * 创建任务数据源
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param taskId 任务定义Id
     * @param typeId 任务类型Id
     * @param request 请求体
     * @return 创建结果
     */
    @PostMapping("/tasks/{task_id}/types/{type_id}/sources")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> createTaskSource(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("type_id") String typeId, @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> this.portalController.createTaskSource(httpRequest, tenantId, taskId, typeId, request),
                plugin, httpRequest);
    }

    /**
     * 修改任务数据源
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param taskId 任务定义Id
     * @param typeId 任务类型Id
     * @param sourceId 数据源Id
     * @param request 请求体
     * @return 修改结果
     */
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

    /**
     * 删除任务数据源
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param taskId 任务定义Id
     * @param typeId 任务类型Id
     * @param sourceId 数据源Id
     * @return 删除结果
     */
    @DeleteMapping("/tasks/{task_id}/types/{type_id}/sources/{source_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> deleteTaskSource(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("type_id") String typeId, @PathVariable("source_id") String sourceId) {
        return View.viewOf(
                () -> this.portalController.deleteTaskSource(httpRequest, tenantId, taskId, typeId, sourceId), plugin,
                httpRequest);
    }

    /**
     * 查询任务数据源
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param taskId 任务定义Id
     * @param typeId 任务类型Id
     * @return Map<String, Object>
     */
    @GetMapping("/tasks/{task_id}/types/{type_id}/sources")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> listTaskSources(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("type_id") String typeId) {
        return View.viewOf(() -> this.portalController.listTaskSources(httpRequest, tenantId, taskId, typeId), plugin,
                httpRequest);
    }

    /**
     * 创建三方授权
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param request 请求体
     * @return 创建结果
     */
    @PostMapping(path = "/authorizations", summary = "创建三方授权")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> createAuthorization(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> this.portalController.createAuthorization(httpRequest, tenantId, request),
                plugin, httpRequest);
    }

    /**
     * 修改三方授权
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param authorizationId 授权Id
     * @param request 请求体
     * @return 修改结果
     */
    @PatchMapping(path = "/authorizations/{authorization_id}", summary = "修改三方授权")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public Map<String, Object> patchAuthorization(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("authorization_id") String authorizationId,
            @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> this.portalController.patchAuthorization(httpRequest, tenantId, authorizationId,
                request), plugin, httpRequest);
    }

    /**
     * 删除三方授权
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param authorizationId 授权Id
     * @return 删除结果
     */
    @DeleteMapping(path = "/authorizations/{authorization_id}", summary = "修改三方授权")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public Map<String, Object> deleteAuthorization(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("authorization_id") String authorizationId) {
        return View.viewOf(() -> this.portalController.deleteAuthorization(httpRequest, tenantId, authorizationId),
                plugin, httpRequest);
    }

    /**
     * 查询三方授权
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param authorizationId 授权Id
     * @return 查询结果
     */
    @GetMapping(path = "/authorizations/{authorization_id}", summary = "检索三方授权")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieveAuthorization(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("authorization_id") String authorizationId) {
        return View.viewOf(() -> this.portalController.retrieveAuthorization(httpRequest, tenantId, authorizationId),
                plugin, httpRequest);
    }

    /**
     * 批量查询三方授权
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param offset offset
     * @param limit limit
     * @return 查询结果
     */
    @GetMapping(path = "/authorizations", summary = "查询三方授权")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> listAuthorizations(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId,
            @RequestParam("offset") long offset, @RequestParam("limit") int limit) {
        return View.viewOf(() -> this.portalController.listAuthorizations(httpRequest, tenantId, offset, limit),
                plugin, httpRequest);
    }

    /**
     * 分页查询任务实例列表
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param taskId 任务定义Id
     * @param viewType 视图类型
     * @param deleted 是否查询删除表
     * @param offset offset
     * @param limit limit
     * @return 查询结果
     */
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
     * 分页查询关联任务列表
     *
     * @param httpRequest http请求上下文
     * @param tenantId 租户Id
     * @param instanceId 任务实例Id
     * @param offset offset
     * @param limit limit
     * @return 查询结果
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
