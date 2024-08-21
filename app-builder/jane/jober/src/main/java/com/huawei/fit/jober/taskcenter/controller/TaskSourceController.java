/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import static com.huawei.fit.jober.taskcenter.controller.Views.declareSource;
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
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.taskcenter.declaration.SourceDeclaration;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.service.SourceService;
import modelengine.fitframework.annotation.Component;

import java.util.Map;

/**
 * 为管理
 *
 * @author 梁济时
 * @since 2023-09-15
 */
@Component
@RequestMapping(value = TaskSourceController.BASE_URI, group = "任务来源管理接口")
public class TaskSourceController extends AbstractController {
    /**
     * 基础url
     */
    public static final String BASE_URI = AbstractController.URI_PREFIX + "/tasks/{task_id}/types/{type_id}/sources";

    private final SourceService service;

    private final TaskType.Repo taskTypeRepo;

    /**
     * 构造函数
     *
     * @param authenticator 认证器
     * @param service 数据源服务
     * @param taskTypeRepo 任务类型repo实例
     */
    public TaskSourceController(Authenticator authenticator, SourceService service, TaskType.Repo taskTypeRepo) {
        super(authenticator);
        this.service = service;
        this.taskTypeRepo = taskTypeRepo;
    }

    /**
     * 创建任务来源
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param typeId 类型id
     * @param request 请求数据
     * @return 任务来源
     */
    @PostMapping(summary = "创建任务来源")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> create(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("type_id") String typeId,
            @RequestBody Map<String, Object> request) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        SourceDeclaration declaration = declareSource(request);
        // 兼容逻辑，使用任务类型的名称设置数据源的名称，后续应删除数据源名称属性，统一使用任务类型名称。
        TaskType type = this.taskTypeRepo.retrieve(taskId, typeId, context);
        declaration.setName(UndefinableValue.defined(type.name()));

        SourceEntity source = this.service.create(taskId, typeId, declaration, context);
        Map<String, Object> view = viewOf(source);
        // 兼容逻辑，后续直接删除 view 中的该属性，不需要额外处理。
        view.remove("name");
        return view;
    }

    /**
     * 根据任务来源ID更新任务来源
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param typeId 类型id
     * @param sourceId 来源id
     * @param request 请求数据
     */
    @PatchMapping(value = "/{source_id}", summary = "根据任务来源ID更新任务来源")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patch(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("type_id") String typeId,
            @PathVariable("source_id") String sourceId, @RequestBody Map<String, Object> request) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        SourceDeclaration declaration = declareSource(request);
        declaration.setName(UndefinableValue.undefined());
        this.service.patch(taskId, typeId, sourceId, declaration, context);
    }

    /**
     * 删除任务来源
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param typeId 类型id
     * @param sourceId 来源id
     */
    @DeleteMapping(value = "/{source_id}", summary = "根据任务来源ID删除任务来源")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void delete(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("type_id") String typeId,
            @PathVariable("source_id") String sourceId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        this.service.delete(taskId, typeId, sourceId, context);
    }

    /**
     * 检索任务来源
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param typeId 类型id
     * @param sourceId 来源id
     * @return 任务来源
     */
    @GetMapping(value = "/{source_id}", summary = "根据任务来源ID获取任务来源")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieve(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("type_id") String typeId, @PathVariable("source_id") String sourceId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        SourceEntity source = this.service.retrieve(taskId, typeId, sourceId, context);
        return viewOf(source);
    }
}
