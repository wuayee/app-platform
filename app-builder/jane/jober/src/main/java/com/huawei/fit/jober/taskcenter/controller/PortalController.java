/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import static com.huawei.fit.jober.taskcenter.controller.Views.declareProperty;
import static com.huawei.fit.jober.taskcenter.controller.Views.declareSource;
import static com.huawei.fit.jober.taskcenter.controller.Views.declareTask;
import static com.huawei.fit.jober.taskcenter.controller.Views.declareTaskType;
import static com.huawei.fit.jober.taskcenter.controller.Views.filterOfInstances;
import static com.huawei.fit.jober.taskcenter.controller.Views.viewOf;

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
import com.huawei.fit.jane.task.domain.File;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.domain.TaskRelation;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.Pagination;
import com.huawei.fit.jober.taskcenter.declaration.SourceDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.TaskDeclaration;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.domain.portal.TaskNode;
import com.huawei.fit.jober.taskcenter.service.PortalService;
import com.huawei.fit.jober.taskcenter.service.TaskAgendaService;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.util.sql.OrderBy;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.model.RangedResultSet;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 为前端页面提供 REST 风格 API。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-17
 */
@Component("JanePortalController")
@RequestMapping(value = AbstractController.URI_PREFIX + "/portal", group = "门户管理接口")
public class PortalController extends AbstractController {
    private final PortalService portalService;

    private final AuthorizationController authorizationController;

    private final InstanceController instanceController;

    private final TaskRelation.Repo taskRelationRepo;

    private final TaskAgendaService taskagendaService;

    private final TaskService taskService;

    private final File.Repo fileRepo;

    private final String janeEndpoint;

    private final String defaultTemplateId;

    /**
     * 构造函数
     *
     * @param authenticator {@link Authenticator}认证器实例
     * @param portalService 门户服务
     * @param authorizationController 授权控制器
     * @param instanceController 实例控制器
     * @param taskRelationRepo 任务关系数据库操作接口
     * @param taskagendaService 任务调度服务
     * @param taskService 任务服务
     * @param fileRepo 文件数据库操作接口
     * @param janeEndpoint 接口前缀
     * @param defaultTemplateId 默认模板ID
     */
    public PortalController(Authenticator authenticator, PortalService portalService,
            AuthorizationController authorizationController, InstanceController instanceController,
            TaskRelation.Repo taskRelationRepo, TaskAgendaService taskagendaService, TaskService taskService,
            File.Repo fileRepo, @Value("${jane.endpoint}") String janeEndpoint,
            @Value("${jane.default.template.id}") String defaultTemplateId) {
        super(authenticator);
        this.portalService = portalService;
        this.authorizationController = authorizationController;
        this.instanceController = instanceController;
        this.taskRelationRepo = taskRelationRepo;
        this.taskagendaService = taskagendaService;
        this.taskService = taskService;
        this.fileRepo = fileRepo;
        this.janeEndpoint = janeEndpoint;
        this.defaultTemplateId = defaultTemplateId;
    }

    /**
     * titles
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @return List<Map < String, Object>>
     * @deprecated 下个版本删除
     */
    @GetMapping("/groups")
    @ResponseStatus(HttpResponseStatus.OK)
    @Deprecated
    public List<Map<String, Object>> titles(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId) {
        List<String> owners = httpRequest.queries().all("owner");
        List<String> creators = httpRequest.queries().all("created_by");
        List<String> categories = httpRequest.queries().all("category");
        List<String> departments = httpRequest.queries().all("department");
        List<String> tags = new ArrayList<>(httpRequest.queries().all("tag"));
        tags.addAll(departments);
        OperationContext context = this.contextOf(httpRequest, tenantId);
        List<PortalService.TaskGroup> groups =
                this.portalService.listTaskGroups(owners, creators, tags, categories, Collections.emptyList(), context);
        return groups.stream().map(group -> {
            Map<String, Object> view = new LinkedHashMap<>();
            view.put("treeId", group.getTreeId());
            view.put("treeName", group.getTreeName());
            view.put("taskId", group.getTaskId());
            view.put("numberOfTasks", group.getNumberOfTasks());
            return view;
        }).collect(Collectors.toList());
    }

    /**
     * count
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @return List<Map < String, Object>>
     */
    @GetMapping("/count")
    @ResponseStatus(HttpResponseStatus.OK)
    public List<Map<String, Object>> count(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId) {
        List<String> owners = httpRequest.queries().all("owner");
        List<String> creators = httpRequest.queries().all("created_by");
        List<String> departments = httpRequest.queries().all("department");
        List<String> tags = new ArrayList<>(httpRequest.queries().all("tag"));
        tags.addAll(departments);
        List<String> taskIds = httpRequest.queries().all("taskId");
        OperationContext context = this.contextOf(httpRequest, tenantId);
        return this.portalService.count(owners, creators, tags, taskIds, context).stream().map(entity -> {
            Map<String, Object> view = new LinkedHashMap<>();
            view.put("status", entity.getStatus());
            view.put("value", entity.getValue());
            return view;
        }).collect(Collectors.toList());
    }

    /**
     * trees
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @return List<Map < String, Object>>
     */
    @GetMapping("/trees")
    @ResponseStatus(HttpResponseStatus.OK)
    public List<Map<String, Object>> trees(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId) {
        List<TaskNode> roots = this.portalService.getTree(this.contextOf(httpRequest, tenantId));
        return viewOf(roots, Views::viewOf);
    }

    /**
     * createTask
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param request request
     * @return Map<String, Object>
     */
    @PostMapping("/tasks")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> createTask(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> request) {
        TaskDeclaration declaration = declareTask(request);
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskEntity task = this.portalService.createTask(declaration, context);
        return viewOf(task);
    }

    /**
     * patchTask
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param request request
     */
    @PatchMapping("/tasks/{task_id}")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patchTask(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @RequestBody Map<String, Object> request) {
        TaskDeclaration declaration = declareTask(request);
        OperationContext context = this.contextOf(httpRequest, tenantId);
        this.portalService.patchTask(taskId, declaration, context);
    }

    /**
     * deleteTask
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     */
    @DeleteMapping("/tasks/{task_id}")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void deleteTask(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        this.portalService.deleteTask(taskId, context);
    }

    /**
     * retrieveTask
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @return Map<String, Object>
     */
    @GetMapping("/tasks/{task_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieveTask(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskEntity task = this.portalService.retrieveTask(taskId, context);
        return viewOf(task);
    }

    /**
     * createTaskProperty
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param request request
     * @return Map<String, Object>
     */
    @PostMapping("/tasks/{task_id}/properties")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> createTaskProperty(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestBody Map<String, Object> request) {
        TaskProperty.Declaration declaration = declareProperty(request);
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskProperty property = this.portalService.createTaskProperty(taskId, declaration, context);
        return viewOf(property);
    }

    /**
     * patchTaskProperty
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param propertyId propertyId
     * @param request request
     */
    @PatchMapping("/tasks/{task_id}/properties/{property_id}")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patchTaskProperty(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("property_id") String propertyId,
            @RequestBody Map<String, Object> request) {
        TaskProperty.Declaration declaration = declareProperty(request);
        OperationContext context = this.contextOf(httpRequest, tenantId);
        this.portalService.patchTaskProperty(taskId, propertyId, declaration, context);
    }

    /**
     * 为任务定义的属性打补丁
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param request 请求
     */
    @PatchMapping("/tasks/{task_id}/properties")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patchProperties(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @RequestBody Map<String, Map<String, Object>> request) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        Map<String, TaskProperty.Declaration> declarations = new HashMap<>(request.size());
        for (Map.Entry<String, Map<String, Object>> entry : request.entrySet()) {
            declarations.put(entry.getKey(), declareProperty(entry.getValue()));
        }
        this.portalService.patchProperties(taskId, declarations, context);
    }

    /**
     * deleteTaskProperty
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param propertyId propertyId
     */
    @DeleteMapping("/tasks/{task_id}/properties/{property_id}")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void deleteTaskProperty(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("property_id") String propertyId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        this.portalService.deleteTaskProperty(taskId, propertyId, context);
    }

    /**
     * createTaskType
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param request request
     * @return Map<String, Object>
     */
    @PostMapping("/tasks/{task_id}/types")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> createTaskType(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestBody Map<String, Object> request) {
        TaskType.Declaration declaration = declareTaskType(request);
        OperationContext context = this.contextOf(httpRequest, tenantId);
        TaskType type = this.portalService.createTaskType(taskId, declaration, context);
        return viewOf(type);
    }

    /**
     * patchTaskType
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param typeId typeId
     * @param request request
     */
    @PatchMapping("/tasks/{task_id}/types/{type_id}")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patchTaskType(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("type_id") String typeId,
            @RequestBody Map<String, Object> request) {
        TaskType.Declaration declaration = declareTaskType(request);
        OperationContext context = this.contextOf(httpRequest, tenantId);
        this.portalService.patchTaskType(taskId, typeId, declaration, context);
    }

    /**
     * deleteTaskType
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param typeId typeId
     */
    @DeleteMapping("/tasks/{task_id}/types/{type_id}")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void deleteTaskType(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("type_id") String typeId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        this.portalService.deleteTaskType(taskId, typeId, context);
    }

    /**
     * createTaskSource
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param typeId typeId
     * @param request request
     * @return Map<String, Object>
     */
    @PostMapping("/tasks/{task_id}/types/{type_id}/sources")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> createTaskSource(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("type_id") String typeId, @RequestBody Map<String, Object> request) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        SourceDeclaration declaration = declareSource(request);
        SourceEntity source = this.portalService.createTaskSource(taskId, typeId, declaration, context);
        return viewOf(source);
    }

    /**
     * patchTaskSource
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param typeId typeId
     * @param sourceId sourceId
     * @param request request
     */
    @PatchMapping("/tasks/{task_id}/types/{type_id}/sources/{source_id}")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patchTaskSource(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("type_id") String typeId,
            @PathVariable("source_id") String sourceId, @RequestBody Map<String, Object> request) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        SourceDeclaration declaration = declareSource(request);
        this.portalService.patchTaskSource(taskId, typeId, sourceId, declaration, context);
    }

    /**
     * deleteTaskSource
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param typeId typeId
     * @param sourceId sourceId
     */
    @DeleteMapping("/tasks/{task_id}/types/{type_id}/sources/{source_id}")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void deleteTaskSource(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("task_id") String taskId, @PathVariable("type_id") String typeId,
            @PathVariable("source_id") String sourceId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        this.portalService.deleteTaskSource(taskId, typeId, sourceId, context);
    }

    /**
     * listTaskSources
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param typeId typeId
     * @return List<Map < String, Object>>
     */
    @GetMapping("/tasks/{task_id}/types/{type_id}/sources")
    @ResponseStatus(HttpResponseStatus.OK)
    public List<Map<String, Object>> listTaskSources(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @PathVariable("type_id") String typeId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        List<SourceEntity> sources = this.portalService.listTaskSources(taskId, typeId, context);
        return viewOf(sources, Views::viewOf);
    }

    /**
     * 创建三方授权
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param request 请求
     * @return 认证信息
     */
    @PostMapping(path = "/authorizations", summary = "创建三方授权")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> createAuthorization(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> request) {
        return this.authorizationController.create(httpRequest, tenantId, request);
    }

    /**
     * 修改三方授权
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param authorizationId 认证id
     * @param request 请求数据
     */
    @PatchMapping(path = "/authorizations/{authorization_id}", summary = "修改三方授权")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patchAuthorization(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("authorization_id") String authorizationId, @RequestBody Map<String, Object> request) {
        this.authorizationController.patch(httpRequest, tenantId, authorizationId, request);
    }

    /**
     * 删除认证信息
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param authorizationId 认证id
     */
    @DeleteMapping(path = "/authorizations/{authorization_id}", summary = "删除授权")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void deleteAuthorization(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("authorization_id") String authorizationId) {
        this.authorizationController.delete(httpRequest, tenantId, authorizationId);
    }

    /**
     * 检索三方授权
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param authorizationId 认证id
     * @return 认证信息
     */
    @GetMapping(path = "/authorizations/{authorization_id}", summary = "检索三方授权")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieveAuthorization(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("authorization_id") String authorizationId) {
        return this.authorizationController.retrieve(httpRequest, tenantId, authorizationId);
    }

    /**
     * 查询认证信息列表
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param offset 偏移量
     * @param limit 限制条数
     * @return 认证信息列表
     */
    @GetMapping(path = "/authorizations", summary = "查询三方授权")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> listAuthorizations(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestParam("offset") long offset,
            @RequestParam("limit") int limit) {
        return this.authorizationController.list(httpRequest, tenantId, offset, limit);
    }

    /**
     * list
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param taskId taskId
     * @param viewType viewType
     * @param offset offset
     * @param limit limit
     * @param deleted 表示是否查询删除表，true则代表要查询的是删除表。
     * @return Map<String, Object>
     */
    @GetMapping(path = "/tasks/{task_id}/instances", summary = "分页查询任务实例列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> listInstances(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("task_id") String taskId,
            @RequestParam(name = "viewType", required = false) String viewType, @RequestParam("offset") long offset,
            @RequestParam("limit") int limit, @RequestParam(name = "deleted", required = false) String deleted) {
        Map<String, Object> originResult =
                this.instanceController.list(httpRequest, tenantId, taskId, viewType, offset, limit, deleted);
        List<Object> properties = Optional.ofNullable(originResult)
                .map(originRes -> ObjectUtils.<Map<String, Object>>cast(originRes.get("task")))
                .map(task -> ObjectUtils.<List<Object>>cast(task.get("properties")))
                .orElse(Collections.emptyList());
        List<Object> instances = Optional.ofNullable(originResult)
                .map(originRes -> ObjectUtils.<List<Object>>cast(originRes.get("instances")))
                .orElse(Collections.emptyList());
        if (properties.isEmpty() || instances.isEmpty()) {
            return originResult;
        }
        this.updateFileResponse(httpRequest, tenantId, properties, instances);
        return originResult;
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
        TaskRelation.Filter filter =
                TaskRelation.Filter.custom().objectId1s(Collections.singletonList(instanceId)).build();
        OperationContext context = this.contextOf(httpRequest, null);
        RangedResultSet<TaskRelation> resultSet = taskRelationRepo.list(filter, offset, limit, context);
        if (resultSet.getResults().isEmpty()) {
            return viewOf(resultSet, "relations", Views::viewOf);
        }
        List<String> relationInstanceIds = resultSet.getResults()
                .stream()
                .filter(result -> "instance".equals(result.objectType2()))
                .map(TaskRelation::objectId2)
                .collect(Collectors.toList());
        if (relationInstanceIds.isEmpty()) {
            return viewOf(RangedResultSet.create(new ArrayList<TaskRelation>(), (int) offset, limit, 0), "relations",
                    Views::viewOf);
        }

        TaskInstance.Filter originInstanceFilter = filterOfInstances(httpRequest, false);
        TaskInstance.Filter instanceFilter = TaskInstance.Filter.custom()
                .ids(relationInstanceIds)
                .typeIds(originInstanceFilter.typeIds())
                .sourceIds(originInstanceFilter.sourceIds())
                .infos(originInstanceFilter.infos())
                .tags(originInstanceFilter.tags())
                .categories(originInstanceFilter.categories())
                .deleted(originInstanceFilter.deleted())
                .build();
        List<OrderBy> orderBys =
                httpRequest.queries().all("order_by").stream().map(OrderBy::parse).collect(Collectors.toList());
        List<String> taskIds = this.taskagendaService.listTaskIds(instanceFilter,
                Pagination.create(offset, limit),
                this.defaultTemplateId,
                context,
                orderBys);
        Map<String, String> instanceRelationMap =
                resultSet.getResults().stream().collect(Collectors.toMap(TaskRelation::objectId2, TaskRelation::id));
        List<TaskEntity> taskEntityList = taskService.listTaskEntities(taskIds, context);
        PagedResultSet<TaskInstance> results = taskagendaService.listAllAgenda(instanceFilter,
                Pagination.create(offset, limit),
                this.defaultTemplateId,
                context,
                taskEntityList,
                orderBys);
        Map<String, Object> resultMap = buildMultiTaskInstanceView(results, taskEntityList);
        resultMap.put("instanceRelationMap", instanceRelationMap);
        return resultMap;
    }

    private void updateFileResponse(HttpClassicServerRequest httpRequest, String tenantId, List<Object> properties,
            List<Object> instances) {
        List<String> filePropertyNames = properties.stream()
                .filter(property -> "uploadFile".equalsIgnoreCase(ObjectUtils.<Map<String, Object>>cast(ObjectUtils
                        .<Map<String, Object>>cast(
                        property).get("appearance")).get("displayType").toString()))
                .map(property -> ObjectUtils.<Map<String, Object>>cast(property).get("name").toString())
                .collect(Collectors.toList());
        List<Object> allLayerInstances = fillAllLayer(instances);
        this.replaceFileStructOfInfo(httpRequest, tenantId, filePropertyNames, allLayerInstances);
    }

    private void replaceFileStructOfInfo(HttpClassicServerRequest httpRequest, String tenantId,
            List<String> filePropertyNames, List<Object> allLayerInstances) {
        List<Map<String, Object>> infos = allLayerInstances.stream()
                .map(instance -> (Map<String, Object>) ((Map<String, Object>) instance).get("info"))
                .collect(Collectors.toList());
        List<String> fileIds = new ArrayList<>();
        infos.forEach(info -> {
            filePropertyNames.forEach(filePropertyName -> {
                Object fileId = info.get(filePropertyName);
                String fileIdString = String.valueOf(fileId);
                if (StringUtils.isNotBlank(fileIdString) && !StringUtils.equalsIgnoreCase("null", fileIdString)) {
                    fileIds.add(fileIdString);
                }
            });
        });
        Map<String, String> fileIdNameMap = fileRepo.fileInfo(fileIds, this.contextOf(httpRequest, tenantId));
        infos.forEach(info -> {
            filePropertyNames.forEach(filePropertyName -> {
                Object fileId = info.get(filePropertyName);
                String fileIdString = String.valueOf(fileId);
                if (StringUtils.isNotBlank(fileIdString) && !StringUtils.equalsIgnoreCase("null", fileIdString)) {
                    info.put(filePropertyName,
                            FileInfo.builder()
                                    .id(fileId.toString())
                                    .name(fileIdNameMap.get(fileId.toString()))
                                    .url(janeEndpoint + "/api/jober/v1/jane/files/" + fileId)
                                    .build());
                } else {
                    info.put(filePropertyName, null);
                }
            });
        });
    }

    private static List<Object> fillAllLayer(List<Object> instances) {
        List<Object> allLayerInstances = new ArrayList<>(instances);
        List<Object> sameLayerInstances = new ArrayList<>(instances);
        while (!sameLayerInstances.isEmpty()) {
            List<Object> nextLayerInstances = sameLayerInstances.stream()
                    .map(instance -> (List<Object>) ((Map<String, Object>) instance).get("children"))
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            allLayerInstances.addAll(nextLayerInstances);
            sameLayerInstances = nextLayerInstances;
        }
        return allLayerInstances;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class FileInfo {
        private String id;

        private String name;

        private String url;
    }
}
