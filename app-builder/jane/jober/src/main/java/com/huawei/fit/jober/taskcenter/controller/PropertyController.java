/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import static com.huawei.fit.jober.taskcenter.controller.Views.viewOf;

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
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.util.OperationContext;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.model.RangedResultSet;

import java.util.List;
import java.util.Map;

/**
 * 任务属性Controller
 *
 * @author 董建华
 * @since 2023-08-09
 */
@Component
@RequestMapping(value = AbstractController.URI_PREFIX + "/tasks/{task_id}/properties", group = "任务属性管理接口")
public class PropertyController extends AbstractController {
    private final TaskProperty.Repo repo;

    PropertyController(Authenticator authenticator, TaskProperty.Repo repo) {
        super(authenticator);
        this.repo = repo;
    }

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
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> create(@PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @RequestBody Map<String, Object> request,
            HttpClassicServerRequest httpRequest) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskProperty.Declaration declaration = Views.declareProperty(request);
        TaskProperty property = this.repo.create(taskId, declaration, context);
        return viewOf(property);
    }

    /**
     * 选择性修改任务属性
     *
     * @param tenantId tenantId
     * @param taskId taskId
     * @param propertyId the property id
     * @param request request
     * @param httpRequest httpRequest
     */
    @PatchMapping(path = "/{property_id}", summary = "根据任务属性ID修改任务属性")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patch(@PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("property_id") String propertyId, @RequestBody Map<String, Object> request,
            HttpClassicServerRequest httpRequest) {
        this.repo.patch(taskId, propertyId, Views.declareProperty(request),
                this.contextOf(httpRequest, tenantId));
    }

    /**
     * 删除任务定义的某个属性
     *
     * @param tenantId the tenant id
     * @param taskId the task id
     * @param propertyId the property id
     * @param httpRequest the http request
     */
    @DeleteMapping(path = "/{property_id}", summary = "根据任务属性ID删除任务属性")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void delete(@PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("property_id") String propertyId, HttpClassicServerRequest httpRequest) {
        this.repo.delete(taskId, propertyId, this.contextOf(httpRequest, tenantId));
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
    @GetMapping(path = "/{property_id}", summary = "根据任务属性ID查询任务属性详情")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieve(@PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("property_id") String propertyId,
            HttpClassicServerRequest httpRequest) {
        return viewOf(this.repo.retrieve(taskId, propertyId, this.contextOf(httpRequest, tenantId)));
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
    public Map<String, Object> list(@PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, HttpClassicServerRequest httpRequest) {
        List<TaskProperty> properties = this.repo.list(taskId, this.contextOf(httpRequest, tenantId));
        return Views.viewOf(RangedResultSet.create(properties, 0, properties.size(), properties.size()),
                "properties", Views::viewOf);
    }
}
