/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.tianzhou;

import com.huawei.fit.jober.taskcenter.controller.TenantController;

import lombok.RequiredArgsConstructor;
import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PatchMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.plugin.Plugin;

import java.util.Map;

/**
 * 为租户提供 REST 风格 API。
 *
 * @author 陈镕希
 * @since 2023-10-12
 */
@Component
@RequestMapping(value = "/v1/jane/tenants", group = "天舟租户管理接口")
@RequiredArgsConstructor
public class TianzhouTenantController {
    private final TenantController tenantController;

    private final Plugin plugin;

    /**
     * create
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param request request
     * @return Map<String, Object>
     */
    @PostMapping(summary = "创建租户")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> create(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> tenantController.create(httpRequest, httpResponse, request), plugin, httpRequest);
    }

    /**
     * patch
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param request request
     * @return Map<String, Object>
     */
    @PatchMapping(value = "/{tenant_id}", summary = "根据租户ID修改租户信息")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> patch(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> tenantController.patch(httpRequest, httpResponse, tenantId, request), plugin,
                httpRequest);
    }

    /**
     * delete
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @return Map<String, Object>
     */
    @DeleteMapping(value = "/{tenant_id}", summary = "根据租户ID删除租户")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> delete(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId) {
        return View.viewOf(() -> tenantController.delete(httpRequest, httpResponse, tenantId), plugin, httpRequest);
    }

    /**
     * retrieve
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @return Map<String, Object>
     */
    @GetMapping(value = "/{tenant_id}", summary = "根据租户ID查询租户详情")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> retrieve(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId) {
        return View.viewOf(() -> tenantController.retrieve(httpRequest, httpResponse, tenantId), plugin, httpRequest);
    }

    /**
     * listMyTenants
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param offset offset
     * @param limit limit
     * @return Map<String, Object>
     */
    @GetMapping(value = "/my", summary = "查询我的租户列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> listMyTenants(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @RequestParam("offset") long offset,
            @RequestParam("limit") int limit) {
        return View.viewOf(() -> tenantController.listMyTenants(httpRequest, httpResponse, offset, limit), plugin,
                httpRequest);
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
        return View.viewOf(() -> tenantController.list(httpRequest, httpResponse, offset, limit), plugin, httpRequest);
    }

    /**
     * addMember
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param request request
     * @param tenantId tenantId
     * @return Map<String, Object>
     */
    @PostMapping(value = "/{tenant_id}/members", summary = "在租户下添加成员")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> addMember(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> request) {
        return View.viewOf(() -> tenantController.addMember(httpRequest, httpResponse, tenantId, request), plugin,
                httpRequest);
    }

    /**
     * deleteMembers
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @return Map<String, Object>
     */
    @DeleteMapping(value = "/{tenant_id}/members", summary = "删除租户下的某个成员")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> deleteMembers(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId) {
        return View.viewOf(() -> tenantController.deleteMembers(httpRequest, httpResponse, tenantId), plugin,
                httpRequest);
    }

    /**
     * listMember
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param offset offset
     * @param limit limit
     * @param tenantId tenantId
     * @return Map<String, Object>
     */
    @GetMapping(value = "/{tenant_id}/members", summary = "分页查询租户下的成员列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> listMember(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestParam("offset") long offset,
            @RequestParam("limit") int limit) {
        return View.viewOf(() -> tenantController.listMember(httpRequest, httpResponse, tenantId, offset, limit),
                plugin, httpRequest);
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
        return tenantController.checkPermission(httpRequest, httpResponse, tenantId);
    }
}
