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
import modelengine.fitframework.annotation.Component;

import java.util.List;
import java.util.Map;

/**
 * 为管理索引提供接口。
 *
 * @author 梁济时
 * @since 2024-01-08
 */
@Component
@RequestMapping(path = AbstractController.URI_PREFIX + "/tasks/{task_id}/indexes", group = "索引管理接口")
public class IndexController extends AbstractController {
    private final TaskService taskService;

    private final Index.Repo indexRepo;

    /**
     * 构造函数
     *
     * @param authenticator 授权校验器
     * @param taskService 任务服务
     * @param indexRepo 索引数据层
     */
    public IndexController(Authenticator authenticator, TaskService taskService, Index.Repo indexRepo) {
        super(authenticator);
        this.taskService = taskService;
        this.indexRepo = indexRepo;
    }

    /**
     * 创建索引
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param request 请求
     * @return 索引对象
     */
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

    /**
     * 修改索引
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param indexId 索取id
     * @param request 请求
     */
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

    /**
     * 删除索引
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param indexId 索取id
     */
    @DeleteMapping(path = "/{index_id}", summary = "删除索引")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void delete(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("index_id") String indexId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskEntity task = this.taskService.retrieve(taskId, context);
        this.indexRepo.delete(task, indexId, context);
    }

    /**
     * 查询索引
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param indexId 索取id
     * @return 索引
     */
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

    /**
     * 批量查询索引
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return 索引列表
     */
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
