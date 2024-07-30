/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

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
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplate;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.model.RangedResultSet;

import java.util.Map;

/**
 * 为任务模板提供 REST 风格API
 *
 * @author yWX1299574
 * @since 2023-12-12
 */
@Component
@RequestMapping(value = AbstractController.URI_PREFIX + "/task-templates", group = "任务模板管理接口")
public class TaskTemplateController extends AbstractController {
    private final TaskTemplate.Repo repo;

    /**
     * 构造函数
     *
     * @param authenticator 认证器
     * @param repo 任务模板repo
     */
    public TaskTemplateController(Authenticator authenticator, TaskTemplate.Repo repo) {
        super(authenticator);
        this.repo = repo;
    }

    /**
     * create
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param request request
     * @return Map<String, Object>
     */
    @PostMapping(summary = "创建任务模板")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> create(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> request) {
        TaskTemplate.Declaration declaration = Views.declareTaskTemplate(request);
        TaskTemplate taskTemplate = this.repo.create(declaration, this.contextOf(httpRequest, tenantId));
        return Views.viewOf(taskTemplate);
    }

    /**
     * patch
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param id id
     * @param request request
     */
    @PatchMapping(value = "/{task_template_id}", summary = "根据任务定义ID更新任务模板")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patch(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_template_id") String id,
            @RequestBody Map<String, Object> request) {
        TaskTemplate.Declaration declaration = Views.declareTaskTemplate(request);
        this.repo.patch(id, declaration, this.contextOf(httpRequest, tenantId));
    }

    /**
     * delete
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param templateId taskId
     */
    @DeleteMapping(value = "/{task_template_id}", summary = "根据任务模板ID删除任务模板")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void delete(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_template_id") String templateId) {
        this.repo.delete(templateId, this.contextOf(httpRequest, tenantId));
    }

    /**
     * retrieve
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param templateId templateId
     * @return Map<String, Object>
     */
    @GetMapping(value = "/{task_template_id}", summary = "根据任务模板ID查询任务模板详情")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieve(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_template_id") String templateId) {
        return Views.viewOf(this.repo.retrieve(templateId, this.contextOf(httpRequest, tenantId)));
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
    @GetMapping(summary = "分页查询任务模板列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> list(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestParam("offset") long offset,
            @RequestParam("limit") int limit) {
        RangedResultSet<TaskTemplate> entities = this.repo.list(Views.filterOfTaskTemplates(httpRequest),
                offset,
                limit,
                this.contextOf(httpRequest, tenantId));
        return Views.viewOf(entities, "tasks", Views::viewOf);
    }
}
