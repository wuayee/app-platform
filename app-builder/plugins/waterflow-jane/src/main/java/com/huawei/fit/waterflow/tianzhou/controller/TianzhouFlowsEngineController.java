/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.tianzhou.controller;

import static com.huawei.fit.waterflow.tianzhou.Constant.TIANZHOU_URL_PREFIX;

import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.waterflow.biz.util.Views;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.plugin.Plugin;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * bff层流程引擎天舟定制接口
 *
 * @author 杨祥宇
 * @since 2024/1/3
 */
@Component
@RequestMapping(value = TIANZHOU_URL_PREFIX + "/flows-bff", group = "天舟流程定义封装接口")
@RequiredArgsConstructor
public class TianzhouFlowsEngineController {
    private final FlowsEngineController flowsEngineController;

    private final Plugin plugin;

    /**
     * 查询流程定义列表
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户id标识
     * @param data 查询所用数据
     * @return 流程定义列表
     */
    @PostMapping(summary = "web查询流程定义列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findFlowDefinitionsPage(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId,
            @RequestBody Map<String, Object> data) {
        return Views.viewOf(
                () -> flowsEngineController.findFlowDefinitionsPage(httpRequest, httpResponse, tenantId, data), plugin,
                httpRequest);
    }

    /**
     * 流程发布
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户id标识
     * @param data 发布流程json数据
     * @return Map<String, Object>
     */
    @PostMapping(value = "/create", summary = "web发布流程定义")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> createFlows(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> data) {
        return Views.viewOf(() -> flowsEngineController.createFlows(httpRequest, httpResponse, tenantId, data), plugin,
                httpRequest);
    }
}
