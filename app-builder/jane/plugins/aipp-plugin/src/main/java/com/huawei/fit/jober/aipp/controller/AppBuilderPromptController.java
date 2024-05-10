/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.dto.AppBuilderPromptCategoryDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderPromptDto;
import com.huawei.fit.jober.aipp.service.AppBuilderPromptService;
import com.huawei.fitframework.annotation.Component;

import java.util.List;

/**
 * 为灵感大全提供查询接口
 *
 * @author 姚江 yWX1299574
 * @since 2024-04-25
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/app/{app_id}/prompt")
public class AppBuilderPromptController extends AbstractController {
    private final AppBuilderPromptService service;

    public AppBuilderPromptController(Authenticator authenticator, AppBuilderPromptService service) {
        super(authenticator);
        this.service = service;
    }

    @GetMapping
    public Rsp<List<AppBuilderPromptCategoryDto>> listCategories(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId) {
        return service.listPromptCategories(appId, this.contextOf(httpRequest, tenantId));
    }

    @GetMapping("/{category_id}")
    public Rsp<AppBuilderPromptDto> queryInspirations(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @PathVariable("category_id") String categoryId) {
        return service.queryInspirations(appId, categoryId, this.contextOf(httpRequest, tenantId));
    }
}
