/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import static com.huawei.fit.jober.taskcenter.controller.Views.declareAuthorization;
import static com.huawei.fit.jober.taskcenter.controller.Views.filterOfAuthorization;
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
import com.huawei.fit.jane.task.domain.Authorization;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.model.RangedResultSet;

import java.util.Map;

/**
 * 授权校验控制器
 *
 * @author l00498867
 * @since 2024/7/27
 */
@Component
@RequestMapping(path = AbstractController.URI_PREFIX + "/authorizations", group = "三方系统授权管理")
public class AuthorizationController extends AbstractController {
    private final Authorization.Repo repo;

    /**
     * 授权校验
     *
     * @param authenticator 校验器
     * @param repo 权限数据层
     */
    public AuthorizationController(Authenticator authenticator, Authorization.Repo repo) {
        super(authenticator);
        this.repo = repo;
    }

    /**
     * 创建授权对象
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param request 请求
     * @return 授权对象
     */
    @PostMapping(summary = "创建三方授权")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> create(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> request) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        Authorization.Declaration declaration = declareAuthorization(request);
        Authorization authorization = this.repo.create(declaration, context);
        return viewOf(authorization);
    }

    /**
     * 修改授权
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param authorizationId 授权id
     * @param request 请求
     */
    @PatchMapping(path = "/{authorization_id}", summary = "修改三方授权")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patch(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("authorization_id") String authorizationId,
            @RequestBody Map<String, Object> request) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        Authorization.Declaration declaration = declareAuthorization(request);
        this.repo.patch(authorizationId, declaration, context);
    }

    /**
     * 删除授权
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param authorizationId 授权id
     */
    @DeleteMapping(path = "/{authorization_id}", summary = "删除三方授权")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void delete(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("authorization_id") String authorizationId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        this.repo.delete(authorizationId, context);
    }

    /**
     * 查询授权
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param authorizationId 授权id
     * @return 授权对象
     */
    @GetMapping(path = "/{authorization_id}", summary = "检索三方授权")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieve(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("authorization_id") String authorizationId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        Authorization authorization = this.repo.retrieve(authorizationId, context);
        return viewOf(authorization);
    }

    /**
     * 批量查询授权
     *
     * @param httpRequest http请求
     * @param tenantId 租户id
     * @param offset 偏移量
     * @param limit 查询个数
     * @return 授权对象map
     */
    @GetMapping(summary = "查询三方授权")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> list(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestParam("offset") long offset, @RequestParam("limit") int limit) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        Authorization.Filter filter = filterOfAuthorization(httpRequest);
        RangedResultSet<Authorization> authorizations = this.repo.list(filter, offset, limit, context);
        return viewOf(authorizations, "authorizations", Views::viewOf);
    }
}
