/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.dto.AppBuilderPromptCategoryDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderPromptDto;
import com.huawei.fit.jober.aipp.service.AppBuilderPromptService;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;

import java.util.List;

/**
 * 为灵感大全提供查询接口
 *
 * @author 姚江
 * @since 2024-04-25
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/app/{app_id}/prompt")
public class AppBuilderPromptController extends AbstractController {
    private final AppBuilderPromptService service;

    /**
     * 表示构造函数，初始化服务对象。
     *
     * @param authenticator 表示权限校验认的证器对象的 {@link Authenticator}。
     * @param service 表示处理业务逻辑的服务对象的 {@link AppBuilderPromptService}。
     */
    public AppBuilderPromptController(Authenticator authenticator, AppBuilderPromptService service) {
        super(authenticator);
        this.service = service;
    }

    /**
     * 查询所有的灵感类别。
     *
     * @param httpRequest 表示http请求对象的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户id的 {@link String}。
     * @param appId 表示应用id的 {@link String}。
     * @return 返回所有的灵感类别。
     */
    @GetMapping
    public Rsp<List<AppBuilderPromptCategoryDto>> listCategories(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId) {
        return service.listPromptCategories(appId, this.contextOf(httpRequest, tenantId));
    }

    /**
     * 查询指定类别的所有灵感。
     *
     * @param httpRequest 表示http请求对象的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户id的 {@link String}。
     * @param appId 表示应用id的 {@link String}。
     * @param categoryId 表示类别id的 {@link String}。
     * @return 返回指定类别的所有灵感 {@link Rsp}{@code <}{@link AppBuilderPromptDto}{@code >}。
     */
    @GetMapping("/{category_id}")
    public Rsp<AppBuilderPromptDto> queryInspirations(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @PathVariable("category_id") String categoryId) {
        return service.queryInspirations(appId, categoryId, this.contextOf(httpRequest, tenantId));
    }
}
