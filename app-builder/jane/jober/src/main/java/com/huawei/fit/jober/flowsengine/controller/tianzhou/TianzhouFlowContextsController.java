/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.controller.tianzhou;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.jober.flowsengine.controller.FlowContextsController;
import com.huawei.fit.jober.taskcenter.tianzhou.TianzhouAbstractController;
import com.huawei.fit.jober.taskcenter.tianzhou.View;
import com.huawei.fit.waterflow.biz.common.vo.FlowDataVO;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.plugin.Plugin;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * 流程实例相关controller
 *
 * @author y00679285
 * @since 2023/9/7
 */
@Component
@RequestMapping(value = TianzhouAbstractController.URI_PREFIX + "/flow-contexts", group = "天舟流程实例管理接口")
@RequiredArgsConstructor
public class TianzhouFlowContextsController extends TianzhouAbstractController {
    private final FlowContextsController flowContextsController;

    private final Plugin plugin;

    /**
     * 启动最新版本的流程实例
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param flowId 流程定义ID
     * @param flowData 流程执行的数据
     * @return 流程执行结果
     */
    @PostMapping(value = "/{flowId}/start", summary = "根据流程定义ID启动流程实例")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> startFlows(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("flowId") String flowId,
            @RequestBody FlowDataVO flowData) {
        // Todo 弄个结构体吃掉这块flowData
        return View.viewOf(
                () -> flowContextsController.startFlows(httpRequest, httpResponse, tenantId, flowId, flowData), plugin,
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
        return View.viewOf(
                () -> flowContextsController.findStartNodeContexts(httpRequest, httpResponse, tenantId, metaId,
                        version), plugin, httpRequest);
    }

    /**
     * 查询当前节点所有context列表
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
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
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId,
            @PathVariable("metaId") String metaId, @PathVariable("version") String version,
            @PathVariable("nodeId") String nodeId) {
        return View.viewOf(
                () -> flowContextsController.findNodeContexts(httpRequest, httpResponse, tenantId, metaId, version,
                        nodeId), plugin, httpRequest);
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
        return View.viewOf(
                () -> flowContextsController.findContextStatusViewCount(httpRequest, httpResponse, tenantId, metaId,
                        version), plugin, httpRequest);
    }
}
