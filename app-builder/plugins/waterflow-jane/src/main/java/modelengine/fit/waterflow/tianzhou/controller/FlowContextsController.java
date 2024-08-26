/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.tianzhou.controller;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import modelengine.fit.waterflow.biz.common.vo.FlowDataVO;
import modelengine.fit.waterflow.biz.util.ControllerUtil;
import modelengine.fit.waterflow.biz.util.Views;
import modelengine.fit.waterflow.flowsengine.biz.service.FlowContextsService;
import modelengine.fit.waterflow.flowsengine.biz.service.entity.FlowsErrorInfo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowOfferId;
import modelengine.fit.waterflow.tianzhou.Constant;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;

import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.Map;

/**
 * 流程实例相关controller
 * 如果定义了transID作为参数的，且有人使用的，同步增加一个traceID的接口，并标记transID的接口即将废弃
 *
 * @author 杨祥宇
 * @since 2023/9/1
 */
@Component
@RequestMapping(value = Constant.BASE_URI_PREFIX + "/flow-contexts", group = "流程实例管理接口")
public class FlowContextsController {
    private final FlowContextsService flowContextsService;

    private final Authenticator authenticator;

    public FlowContextsController(Authenticator authenticator, FlowContextsService flowContextsService) {
        this.authenticator = authenticator;
        this.flowContextsService = flowContextsService;
    }

    /**
     * 根据流程定义UUID启动流程实例
     *
     * @param httpRequest httpRequest
     * @param tenantId 租户ID
     * @param flowId 流程定义ID
     * @param flowData 流程执行的数据
     * @return 流程执行结果
     */
    @PostMapping(value = "/{flowId}/start", summary = "根据流程定义ID启动流程实例")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> startFlows(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("flowId") String flowId,
            @RequestBody FlowDataVO flowData) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notBlank(flowId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowId"));
        Validation.notNull(flowData, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowData"));
        FlowOfferId flowOfferId = flowContextsService.startFlows(flowId, JSON.toJSONString(flowData),
                this.contextOf(httpRequest, tenantId));
        return Views.viewOfFlowRunResult(flowOfferId);
    }

    /**
     * 根据流程定义UUID恢复流程执行
     * 参数外层map的key为contextId，value为有更新的业务数据map
     * 内存map的key为businessData和operator，后续可以补充passData
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param flowId 流程定义ID
     * @param request 变更的上下文业务数据列集合
     */
    @PostMapping(value = "/{flowId}/resumes", summary = "根据流程定义ID启动流程实例")
    @ResponseStatus(HttpResponseStatus.OK)
    public void resumeFlows(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("flowId") String flowId,
            @RequestBody Map<String, Map<String, Object>> request) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notBlank(flowId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowId"));
        flowContextsService.resumeFlows(flowId, request);
    }

    /**
     * 启动流程实例
     *
     * @param tenantId 租户ID
     * @param metaId 流程定义metaId
     * @param version 流程定义对应版本
     * @param flowData 流程执行的数据
     * @return 流程执行结果
     */
    @PostMapping(value = "/meta-ids/{metaId}/versions/{version}/start", summary = "根据流程定义ID启动流程实例")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> startFlows(@PathVariable("tenant_id") String tenantId,
            @PathVariable("metaId") String metaId, @PathVariable("version") String version,
            @RequestBody String flowData) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notBlank(metaId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "metaId"));
        Validation.notBlank(version, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "version"));
        Validation.notBlank(flowData, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowData"));
        FlowOfferId flowOfferId = flowContextsService.startFlows(metaId, version, flowData);
        return Views.viewOfFlowRunResult(flowOfferId);
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
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notBlank(metaId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "metaId"));
        Validation.notBlank(version, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "version"));
        return Views.viewOfContexts(
                flowContextsService.findStartNodeContexts(metaId, version, this.contextOf(httpRequest, tenantId)));
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
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notBlank(metaId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "metaId"));
        Validation.notBlank(version, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "version"));
        Validation.notBlank(nodeId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "nodeId"));
        return Views.viewOfContexts(
                flowContextsService.findNodeContexts(metaId, version, nodeId, this.contextOf(httpRequest, tenantId)));
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
    public Map<String, Map<String, Long>> findContextStatusViewCount(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId,
            @PathVariable("metaId") String metaId, @PathVariable("version") String version) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notBlank(metaId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "metaId"));
        Validation.notBlank(version, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "version"));
        List<FlowContext<FlowData>> flowContexts = flowContextsService.findContextStatusViewCount(metaId, version,
                this.contextOf(httpRequest, tenantId));
        return Views.viewOfContextStatus(flowContexts);
    }

    /**
     * 批量查询开始、正在运行中、运行错误的流程实例
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param streamIds 流程streamId列表
     * @return 流程实例数量列表
     */
    @PostMapping(value = "/stream-ids", summary = "根据streamId列表查询开始、正在运行中、运行错误的流程实例列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public List<Map<String, Object>> findContextStatusViewByStreamIds(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId,
            @RequestBody List<String> streamIds) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notEmpty(streamIds, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "metaId"));
        return flowContextsService.findContextStatusViewByStreamIds(streamIds, this.contextOf(httpRequest, tenantId));
    }

    /**
     * 根据traceId查询流程实例运行状态和百分比
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param traceIds traceID
     * @return 实例运行状态和百分比
     */
    @GetMapping("/operation-status/{traceId}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Map<String, Object>> getFlowCompleteness(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId,
            @RequestBody List<String> traceIds) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notEmpty(traceIds, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "traceIds"));

        return flowContextsService.getFlowCompleteness(traceIds, true, this.contextOf(httpRequest, tenantId));
    }

    /**
     * 根据traceId查询流程实例运行错误信息详情
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param traceId traceID
     * @return 流程实例运行错误信息详情列表
     */
    @GetMapping("/error-infos/{traceId}")
    @ResponseStatus(HttpResponseStatus.OK)
    public List<FlowsErrorInfo> getFlowErrorInfo(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId,
            @PathVariable("traceId") String traceId) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notBlank(traceId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "traceId"));
        return flowContextsService.getFlowErrorInfo(traceId);
    }

    /**
     * 根据流程实例traceId查询所有contexts
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param traceId 流程实例traceId
     * @return 流程实例context
     */
    @GetMapping(value = "/trace-ids/{traceId}", summary = "根据流程实例traceId查询该traceId中所有的contexts")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findContextByTraceId(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId,
            @PathVariable("traceId") String traceId) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notBlank(traceId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "traceId"));
        return Views.viewOfContexts(
                flowContextsService.findContextByTraceId(traceId, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 根据流程实例traceId终止流程
     * filter中可以传入与业务相关的过滤条件，停止满足条件的部分context，目前不支持
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param traceId 流程实例traceId
     * @param filter filter
     */
    @PostMapping(value = "/terminate/{traceId}", summary = "根据流程实例traceId终止流程")
    @ResponseStatus(HttpResponseStatus.OK)
    public void terminateFlow(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("traceId") String traceId,
            @RequestBody Map<String, Object> filter) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notBlank(traceId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "traceId"));
        flowContextsService.terminateFlows(traceId, filter, this.contextOf(httpRequest, tenantId));
    }

    private OperationContext contextOf(HttpClassicServerRequest request, String tenantId) {
        return ControllerUtil.contextOf(request, tenantId, this.authenticator);
    }
}