/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import static com.huawei.fit.jober.taskcenter.controller.Views.declareIndex;
import static com.huawei.fit.jober.taskcenter.controller.Views.viewOf;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PatchMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.ResponseBody;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.domain.Index;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fitframework.annotation.Component;

import java.util.List;
import java.util.Map;

/**
 * 为管理索引提供接口。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-08
 */
@Component
@RequestMapping(path = AbstractController.URI_PREFIX + "/tasks/{task_id}/indexes", group = "索引管理接口")
public class IndexController extends AbstractController {
    private final TaskService taskService;

    private final Index.Repo indexRepo;

    public IndexController(Authenticator authenticator, TaskService taskService, Index.Repo indexRepo) {
        super(authenticator);
        this.taskService = taskService;
        this.indexRepo = indexRepo;
    }

    @PostMapping(summary = "创建索引")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> create(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @RequestBody Map<String, Object> request) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskEntity task = this.taskService.retrieve(taskId, context);
        Index.Declaration declaration = declareIndex(request);
        Index index = this.indexRepo.create(task, declaration, context);
        return viewOf(index);
    }

    @PatchMapping(path = "/{index_id}", summary = "修改索引")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patch(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("index_id") String indexId,
            @RequestBody Map<String, Object> request) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskEntity task = this.taskService.retrieve(taskId, context);
        Index.Declaration declaration = declareIndex(request);
        this.indexRepo.patch(task, indexId, declaration, context);
    }

    @DeleteMapping(path = "/{index_id}", summary = "删除索引")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void delete(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("index_id") String indexId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskEntity task = this.taskService.retrieve(taskId, context);
        this.indexRepo.delete(task, indexId, context);
    }

    @GetMapping(path = "/{index_id}", summary = "检索索引")
    @ResponseStatus(HttpResponseStatus.OK)
    @ResponseBody
    public Map<String, Object> retrieve(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("index_id") String indexId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskEntity task = this.taskService.retrieve(taskId, context);
        Index index = this.indexRepo.retrieve(task, indexId, context);
        return viewOf(index);
    }

    @GetMapping(summary = "查询索引")
    @ResponseStatus(HttpResponseStatus.OK)
    public List<Map<String, Object>> list(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskEntity task = this.taskService.retrieve(taskId, context);
        List<Index> indexes = this.indexRepo.list(task, context);
        return viewOf(indexes, Views::viewOf);
    }
}
