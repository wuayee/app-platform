/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.tianzhou.controller;

import static com.huawei.fit.waterflow.tianzhou.Constant.TIANZHOU_URL_PREFIX;

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
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.waterflow.biz.util.Views;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.plugin.Plugin;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * 流程定义相关controller
 *
 * @author 杨祥宇
 * @since 2023/9/7
 */
@Component
@RequestMapping(value = TIANZHOU_URL_PREFIX + "/flows", group = "天舟流程定义管理接口")
@RequiredArgsConstructor
public class TianzhouFlowsController {
    private final FlowsController flowsController;

    private final Plugin plugin;

    /**
     * 创建流程定义
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param graphData 流程定义图json字符串
     * @return 流程定义
     */
    @PostMapping(summary = "创建流程定义")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> createFlows(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> graphData) {
        return Views.viewOf(() -> flowsController.createFlows(httpRequest, httpResponse, tenantId, graphData), plugin,
                httpRequest);
    }

    /**
     * 更新流程定义
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param flowId 流程定义图json字符串
     * @param graphData graphData
     * @return 流程定义
     */
    @PatchMapping(value = "/{flowId}", summary = "根据流程定义ID更新流程定义")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public Map<String, Object> updateFlows(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("flowId") String flowId,
            @RequestBody Map<String, Object> graphData) {
        return Views.viewOf(() -> flowsController.updateFlows(httpRequest, httpResponse, tenantId, flowId, graphData),
                plugin, httpRequest);
    }

    /**
     * 根据流程定义id查询流程定义
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param flowId 流程定义id标识
     * @return 流程定义
     */
    @GetMapping(value = "/{flowId}", summary = "根据流程定义ID查询流程定义")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findFlowsById(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId,
            @PathVariable("flowId") String flowId) {
        return Views.viewOf(() -> flowsController.findFlowsById(httpRequest, httpResponse, tenantId, flowId), plugin,
                httpRequest);
    }

    /**
     * 删除流程定义
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param flowId 流程定义ID
     */
    @DeleteMapping(value = "/{flowId}", summary = "根据流程定义ID删除流程定义")
    @ResponseStatus(HttpResponseStatus.OK)
    public void deleteFlows(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("flowId") String flowId) {
        flowsController.deleteFlows(httpRequest, httpResponse, tenantId, flowId);
    }

    /**
     * 查询租户下流程定义列表
     * 返回流程名称以及最新版本号
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @return 流程定义
     */
    @GetMapping(summary = "查询租户下流程定义列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findFlowsByTenant(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId) {
        return Views.viewOf(() -> flowsController.findFlowsByTenant(httpRequest, httpResponse, tenantId), plugin,
                httpRequest);
    }

    /**
     * 查询当前流程版本的流程定义
     * 返回对应版本流程定义id，metaId
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param name 流程定义名称
     * @param version 流程定义版本
     * @return 流程定义
     */
    @GetMapping(value = "/versions/{version}/names", summary = "根据流程定义的version和name查询流程定义")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findFlowsByName(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId,
            @PathVariable("name") String name, @PathVariable("version") String version) {
        return Views.viewOf(() -> flowsController.findFlowsByName(httpRequest, httpResponse, tenantId, name, version),
                plugin, httpRequest);
    }

    /**
     * findFlowsByMetaIdAndVersion
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId tenantId
     * @param metaId metaId
     * @param version version
     * @return Map<String, Object>
     */
    @GetMapping(value = "/meta-ids/{metaId}/versions/{version}", summary = "根据流程定义的metaId和version查询流程定义")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findFlowsByMetaIdAndVersion(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId,
            @PathVariable("metaId") String metaId, @PathVariable("version") String version) {
        return Views.viewOf(
                () -> flowsController.findFlowsByMetaIdAndVersion(httpRequest, httpResponse, tenantId, metaId, version),
                plugin, httpRequest);
    }
}
