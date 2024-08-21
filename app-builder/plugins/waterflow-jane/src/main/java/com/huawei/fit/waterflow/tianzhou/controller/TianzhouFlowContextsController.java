/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.tianzhou.controller;

import static com.huawei.fit.waterflow.tianzhou.Constant.TIANZHOU_URL_PREFIX;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.waterflow.biz.common.vo.FlowDataVO;
import com.huawei.fit.waterflow.biz.util.Views;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.plugin.Plugin;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * 流程实例相关controller
 *
 * @author 杨祥宇
 * @since 2023/9/7
 */
@Component
@RequestMapping(value = TIANZHOU_URL_PREFIX + "/flow-contexts", group = "天舟流程实例管理接口")
@RequiredArgsConstructor
public class TianzhouFlowContextsController {
    private final FlowContextsController flowContextsController;

    private final Plugin plugin;

    /**
     * 启动最新版本的流程实例
     *
     * @param httpRequest httpRequest
     * @param tenantId 租户ID
     * @param flowId 流程定义ID
     * @param flowData 流程执行的数据
     * @return 流程执行结果
     */
    @PostMapping(value = "/{flowId}/start", summary = "根据流程定义ID启动流程实例")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> startFlows(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("flowId") String flowId,
            @RequestBody FlowDataVO flowData) {
        return Views.viewOf(() -> flowContextsController.startFlows(httpRequest, tenantId, flowId, flowData), plugin,
                httpRequest);
    }

    /**
     * 查询当前流程定义版本的start节点流程实例列表
     * start节点的context(执行人，最新位置，创建时间)
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param metaId 流程定义metaId
     * @param version 流程定义版本号
     * @return 流程实例context
     */
    @GetMapping(value = "/meta-ids/{metaId}/versions/{version}",
            summary = "根据流程定义的metaId和version查询start节点流程实例列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findStartNodeContexts(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId,
            @PathVariable("metaId") String metaId, @PathVariable("version") String version) {
        return Views.viewOf(
                () -> flowContextsController.findStartNodeContexts(httpRequest, httpResponse, tenantId, metaId,
                        version), plugin, httpRequest);
    }

    /**
     * 查询当前节点所有context列表
     *
     * @param httpRequest httpRequest
     * @param tenantId 租户ID
     * @param metaId 流程定义metaId
     * @param version 流程定义版本号
     * @param nodeId 流程节点Id
     * @return 流程实例context
     */
    @GetMapping(value = "/meta-ids/{metaId}/versions/{version}/node-ids/{nodeId}",
            summary = "根据流程定义的metaId和version，以及所在节点的nodeId查询当前所有的流程实例列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findNodeContexts(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("metaId") String metaId,
            @PathVariable("version") String version, @PathVariable("nodeId") String nodeId) {
        return Views.viewOf(
                () -> flowContextsController.findNodeContexts(httpRequest, tenantId, metaId, version, nodeId), plugin,
                httpRequest);
    }

    /**
     * 查询当前流程定义版本节点上实例状态统计视图
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param metaId 流程定义metaId
     * @param version 流程定义版本号
     * @return 流程实例context
     */
    @GetMapping(value = "/meta-ids/{metaId}/versions/{version}/count",
            summary = "根据流程定义的metaId和version查询节点上实例状态统计视图")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findContextStatusViewCount(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId,
            @PathVariable("metaId") String metaId, @PathVariable("version") String version) {
        return Views.viewOf(
                () -> flowContextsController.findContextStatusViewCount(httpRequest, httpResponse, tenantId, metaId,
                        version), plugin, httpRequest);
    }
}
