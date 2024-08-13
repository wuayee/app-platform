/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import static com.huawei.fit.jober.common.ErrorCodes.TENANT_MEMBERS_REQUIRED;

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
import com.huawei.fit.jane.task.domain.Tenant;
import com.huawei.fit.jane.task.domain.TenantAccessLevel;
import com.huawei.fit.jane.task.domain.TenantMember;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.model.RangedResultSet;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * 为租户的管理提供 REST 风格 API。
 *
 * @author 陈镕希
 * @since 2023-09-28
 */
@Component
@RequestMapping(value = "/v1/tenants", group = "租户管理接口")
public class TenantController extends AbstractController {
    private final Tenant.Repo repo;

    /**
     * 构造函数
     *
     * @param authenticator 认证器
     * @param repo 租户repo
     */
    public TenantController(Authenticator authenticator, Tenant.Repo repo) {
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
    @PostMapping(summary = "创建租户")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> create(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @RequestBody Map<String, Object> request) {
        Tenant.Declaration declaration = Views.declareTenant(request);
        Tenant entity = repo.create(declaration, this.contextOf(httpRequest, null));
        return Views.viewOf(entity);
    }

    /**
     * patch
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param request request
     */
    @PatchMapping(value = "/{tenant_id}", summary = "根据租户ID更新租户信息")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void patch(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> request) {
        Tenant.Declaration declaration = Views.declareTenant(request);
        repo.patch(tenantId, declaration, this.contextOf(httpRequest, tenantId));
    }

    /**
     * delete
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     */
    @DeleteMapping(value = "/{tenant_id}", summary = "根据租户ID删除租户")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void delete(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId) {
        repo.delete(tenantId, this.contextOf(httpRequest, tenantId));
    }

    /**
     * retrieve
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @return Map<String, Object>
     */
    @GetMapping(value = "/{tenant_id}", summary = "根据租户ID获取租户信息")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieve(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId) {
        Tenant entity = repo.retrieve(tenantId, this.contextOf(httpRequest, tenantId));
        return Views.viewOf(entity);
    }

    /**
     * listMyTenants
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param offset 偏移量
     * @param limit 数量限制
     * @return Map<String, Object>
     */
    @GetMapping(value = "/my", summary = "查询我的租户列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> listMyTenants(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @RequestParam("offset") long offset,
            @RequestParam("limit") int limit) {
        OperationContext context = this.contextOf(httpRequest, null);
        List<String> tenantIdsByUserId = repo.listTenantIdsByUserId(context.operator(), context);
        RangedResultSet<Tenant> results = repo.listMy(tenantIdsByUserId, offset, limit, context);
        return Views.viewOf(results, "tenants", Views::viewOf);
    }

    /**
     * list
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param offset offset
     * @param limit limit
     * @return Map<String, Object>
     */
    @GetMapping(summary = "分页查询租户列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> list(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @RequestParam("offset") long offset, @RequestParam("limit") int limit) {
        Tenant.Filter filter = Views.filterOfTenants(httpRequest);
        RangedResultSet<Tenant> results = repo.list(filter, offset, limit, this.contextOf(httpRequest, null));
        return Views.viewOf(results, "tenants", Views::viewOf);
    }

    /**
     * addMember
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户id
     * @param request request
     */
    @PostMapping(value = "/{tenant_id}/members", summary = "在租户下添加成员")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public void addMember(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> request) {
        if (!request.containsKey("members")) {
            throw new BadRequestException(TENANT_MEMBERS_REQUIRED);
        }
        List<String> members = ObjectUtils.cast(request.get("members"));
        repo.insertMembers(tenantId, members, this.contextOf(httpRequest, null));
    }

    /**
     * deleteMemberByTenant
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     */
    @DeleteMapping(value = "/{tenant_id}/members", summary = "删除租户下的指定成员")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void deleteMembers(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId) {
        UndefinableValue<List<String>> members = Views.defineList(httpRequest.queries().all("member"));
        if (!members.defined()) {
            throw new BadRequestException(TENANT_MEMBERS_REQUIRED);
        }
        repo.deleteMembersById(tenantId, members.get(), this.contextOf(httpRequest, tenantId));
    }

    /**
     * listMember
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户id
     * @param offset offset
     * @param limit limit
     * @return Map<String, Object>成员列表
     */
    @GetMapping(value = "/{tenant_id}/members", summary = "分页查询租户下的成员列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> listMember(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestParam("offset") long offset,
            @RequestParam("limit") int limit) {
        TenantMember.Filter filter = Views.filterOfTenantMembers(httpRequest, tenantId);
        RangedResultSet<TenantMember> results =
                repo.listMember(filter, offset, limit, this.contextOf(httpRequest, null));
        return Views.viewOf(results, "members", Views::viewOf);
    }

    /**
     * checkPermission
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @return true表示用户有权限 false表示用户无权限
     */
    @GetMapping(value = "/{tenant_id}/permission", summary = "查询用户在该租户是否带有权限")
    @ResponseStatus(HttpResponseStatus.OK)
    public boolean checkPermission(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        Tenant tenant = repo.retrieve(tenantId, context);
        return TenantAccessLevel.PUBLIC.equals(tenant.accessLevel()) || tenant.isPermitted(repo,
                context.operator(),
                context);
    }
}