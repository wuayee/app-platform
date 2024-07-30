/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import static com.huawei.fit.jober.taskcenter.controller.Views.viewOf;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.jane.task.domain.TaskRelation;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.model.RangedResultSet;

import java.util.Map;

/**
 * 任务关联相关控制器类
 *
 * @author lWX1291633
 * @since 2023-12-28
 */
@Component
@RequestMapping(path = "/v1/relations", group = "任务关联相关接口")
public class TaskRelationsController extends AbstractController {
    private final TaskRelation.Repo repo;

    /**
     * 构造函数
     *
     * @param authenticator 构造器
     * @param repo 提供数据库持久化能力的实例
     */
    public TaskRelationsController(Authenticator authenticator, TaskRelation.Repo repo) {
        super(authenticator);
        this.repo = repo;
    }

    /**
     * create
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param request request
     * @return Map<String, Object>
     */
    @PostMapping(summary = "创建关联")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> create(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @RequestBody Map<String, Object> request) {
        TaskRelation.Declaration declaration = Views.declareTaskRelation(request);
        TaskRelation taskRelation = repo.create(declaration, this.contextOf(httpRequest, null));
        return Views.viewOf(taskRelation);
    }

    /**
     * delete
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param relationId relationId
     */
    @DeleteMapping(value = "/{relation_id}", summary = "根据关联ID删除关联关系")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void delete(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("relation_id") String relationId) {
        repo.delete(relationId, this.contextOf(httpRequest, null));
    }

    /**
     * retrieve
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param relationId relationId
     * @return Map<String, Object>
     */
    @GetMapping(value = "/{relation_id}", summary = "根据关联ID获取关联任务信息")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieve(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("relation_id") String relationId) {
        TaskRelation taskRelation = repo.retrieve(relationId, this.contextOf(httpRequest, null));
        return Views.viewOf(taskRelation);
    }

    /**
     * list
     *
     * @param httpRequest httpRequest
     * @param offset offset
     * @param limit limit
     * @return Map<String, Object>
     */
    @GetMapping(summary = "分页查询关联任务列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> list(HttpClassicServerRequest httpRequest, @RequestParam("offset") long offset,
            @RequestParam("limit") int limit) {
        TaskRelation.Filter filter = Views.filterOfTaskRelation(httpRequest);
        RangedResultSet<TaskRelation> taskRelations =
                repo.list(filter, offset, limit, this.contextOf(httpRequest, null));
        return viewOf(taskRelations, "relations", Views::viewOf);
    }
}
