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
import com.huawei.fit.jober.taskcenter.controller.PropertyController;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.plugin.Plugin;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * 任务属性Controller
 *
 * @author 董建华
 * @since 2023-08-09
 */
@Component
@RequestMapping(value = TianzhouAbstractController.URI_PREFIX + "/tasks/{task_id}/properties",
        group = "天舟任务属性管理接口")
@RequiredArgsConstructor
public class TianzhouPropertyController extends TianzhouAbstractController {
    private final PropertyController propertyController;

    private final Plugin plugin;

    /**
     * 添加任务属性
     *
     * @param tenantId tenantId
     * @param taskId taskId
     * @param request request
     * @param httpRequest httpRequest
     * @return 任务属性 map
     */
    @PostMapping(summary = "添加任务属性")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> createProperty(@PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @RequestBody Map<String, Object> request,
            HttpClassicServerRequest httpRequest) {
        return View.viewOf(() -> propertyController.create(tenantId, taskId, request, httpRequest), plugin,
                httpRequest);
    }

    /**
     * 选择性修改任务属性
     *
     * @param tenantId tenantId
     * @param taskId taskId
     * @param propertyId the property id
     * @param request request
     * @param httpRequest httpRequest
     * @return jober response
     */
    @PatchMapping(path = "/{property_id}", summary = "根据任务属性ID更新任务属性")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> patchPropertyById(@PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("property_id") String propertyId,
            @RequestBody Map<String, Object> request, HttpClassicServerRequest httpRequest) {
        return View.viewOf(() -> propertyController.patch(tenantId, taskId, propertyId, request, httpRequest), plugin,
                httpRequest);
    }

    /**
     * 删除任务定义的某个属性
     *
     * @param tenantId the tenant id
     * @param taskId the task id
     * @param propertyId the property id
     * @param httpRequest the http request
     * @return Void
     */
    @DeleteMapping(path = "/{property_id}", summary = "根据任务属性ID删除任务属性")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> deletePropertyById(@PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("property_id") String propertyId,
            HttpClassicServerRequest httpRequest) {
        return View.viewOf(() -> propertyController.delete(tenantId, taskId, propertyId, httpRequest), plugin,
                httpRequest);
    }

    /**
     * Select property by id map.
     *
     * @param tenantId the tenant id
     * @param taskId the task id
     * @param propertyId the property id
     * @param httpRequest the http request
     * @return View Map
     */
    @GetMapping(path = "/{property_id}", summary = "根据任务属性ID获取任务属性详情")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> selectPropertyById(@PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("property_id") String propertyId,
            HttpClassicServerRequest httpRequest) {
        return View.viewOf(() -> propertyController.retrieve(tenantId, taskId, propertyId, httpRequest), plugin,
                httpRequest);
    }

    /**
     * Select properties map.
     *
     * @param tenantId the tenant id
     * @param taskId the task id
     * @param httpRequest the http request
     * @return view map
     */
    @GetMapping(summary = "查询任务属性列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> selectProperties(@PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, HttpClassicServerRequest httpRequest) {
        return View.viewOf(() -> propertyController.list(tenantId, taskId, httpRequest), plugin, httpRequest);
    }
}
