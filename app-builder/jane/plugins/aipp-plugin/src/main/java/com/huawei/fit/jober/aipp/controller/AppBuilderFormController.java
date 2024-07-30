/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jober.aipp.dto.AppBuilderFormDto;
import com.huawei.fit.jober.aipp.service.AppBuilderFormService;
import com.huawei.fitframework.annotation.Component;

import java.util.List;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-19
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/form")
public class AppBuilderFormController {
    private final AppBuilderFormService formService;

    /**
     * 构造函数，初始化表单服务.
     *
     * @param formService 表单服务对象
     */
    public AppBuilderFormController(AppBuilderFormService formService) {
        this.formService = formService;
    }

    /**
     * 根据 type 查询表单。
     *
     * @param httpRequest 请求对象。
     * @param type 表单类型。
     * @param tenantId 租户 ID。
     * @return 返回查询结果。
     */
    @GetMapping(value = "/type/{type}", description = "查询指定 type 的表单")
    public Rsp<List<AppBuilderFormDto>> queryByType(HttpClassicServerRequest httpRequest,
            @PathVariable("type") String type, @PathVariable("tenant_id") String tenantId) {
        return this.formService.queryByType(httpRequest, type, tenantId);
    }
}
