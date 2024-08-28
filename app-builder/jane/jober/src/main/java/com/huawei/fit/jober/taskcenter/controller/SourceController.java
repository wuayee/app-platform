/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.declaration.SourceDeclaration;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.service.SourceService;

import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.DocumentIgnored;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PatchMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fitframework.annotation.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 为任务数据源提供 REST 风格 API。
 * TODO 待删除，使用 TaskSourceController 替代。
 *
 * @author 梁济时
 * @since 2023-08-09
 * @deprecated 废下个版本删除
 */
@Component
@RequestMapping(value = AbstractController.URI_PREFIX + "/tasks/{task_id}/sources", group = "Task Source Management")
@Deprecated
@DocumentIgnored
public class SourceController extends AbstractController {
    private final SourceService sourceService;

    /**
     * 构造函数
     *
     * @param authenticator {@link Authenticator}认证器实例
     * @param sourceService {@link SourceService}实例
     */
    public SourceController(Authenticator authenticator, SourceService sourceService) {
        super(authenticator);
        this.sourceService = sourceService;
    }

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
    @PostMapping
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> create(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestBody Map<String, Object> request) {
        SourceDeclaration declaration = Views.declareSource(request);
        SourceEntity entity =
                this.sourceService.create(taskId, null, declaration, this.contextOf(httpRequest, tenantId));
        return Views.viewOf(entity);
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
     */
    @PatchMapping("/{source_id}")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patch(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("source_id") String sourceId, @RequestBody Map<String, Object> request) {
        SourceDeclaration declaration = Views.declareSource(request);
        this.sourceService.patch(taskId, null, sourceId, declaration, this.contextOf(httpRequest, tenantId));
    }

    /**
     * delete
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param taskId taskId
     * @param sourceId sourceId
     */
    @DeleteMapping("/{source_id}")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void delete(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("source_id") String sourceId) {
        this.sourceService.delete(taskId, null, sourceId, this.contextOf(httpRequest, tenantId));
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
    @GetMapping("/{source_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieve(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("source_id") String sourceId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        SourceEntity entity = this.sourceService.retrieve(taskId, null, sourceId, context);
        return Views.viewOf(entity);
    }

    /**
     * list
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param taskId taskId
     * @return List<Map < String, Object>>
     */
    @GetMapping
    @ResponseStatus(HttpResponseStatus.OK)
    public List<Map<String, Object>> list(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId) {
        List<SourceEntity> entities =
                this.sourceService.list(Collections.singletonList(taskId), this.contextOf(httpRequest, tenantId))
                        .getOrDefault(taskId, Collections.emptyList());
        return Views.viewOf(entities, Views::viewOf);
    }
}
