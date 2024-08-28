/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.tianzhou.controller;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;
import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static modelengine.fit.waterflow.common.Constant.OPERATOR_KEY;
import static modelengine.fit.waterflow.tianzhou.Constant.BASE_URI_PREFIX;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.flow.graph.entity.FlowGraphDefinition;
import com.huawei.fit.jane.flow.graph.entity.FlowSaveEntity;
import com.huawei.fit.jane.flow.graph.entity.elsa.response.GetPageResponse;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.entity.FlowInfo;
import modelengine.fit.waterflow.biz.util.ControllerUtil;
import modelengine.fit.waterflow.biz.util.Views;
import modelengine.fit.waterflow.graph.FlowsEngineWebService;
import modelengine.fit.waterflow.graph.FlowsEngineWebServiceForDbGraph;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;

import com.alibaba.fastjson.JSON;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 封装流程相关接口
 *
 * @author 杨祥宇
 * @since 2023/10/11
 */
@Component
@RequestMapping(value = BASE_URI_PREFIX + "/flows-bff", group = "流程封装接口")
public class FlowsEngineController {
    private final FlowsEngineWebServiceForDbGraph flowsEngineService;

    private final Authenticator authenticator;

    public FlowsEngineController(Authenticator authenticator, FlowsEngineWebServiceForDbGraph flowsEngineService) {
        this.authenticator = authenticator;
        this.flowsEngineService = flowsEngineService;
    }

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
        OperationContext operationContext = buildOperationContext(tenantId, this.getOperator(httpRequest));
        GetPageResponse flowDefinitionsPage = flowsEngineService.findFlowDefinitionsPage(JSON.toJSONString(data),
                httpRequest, tenantId, operationContext);
        return Views.viewOf(flowDefinitionsPage);
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
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> createFlows(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> data) {
        OperationContext operationContext = buildOperationContext(tenantId, this.getOperator(httpRequest));
        return flowsEngineService.createFlows(data, httpRequest, tenantId, operationContext);
    }

    /**
     * 发布流程定义
     *
     * @param tenantId 租户id标识
     * @param flowId 流程id
     * @param version 流程版本
     * @param body 请求体
     */
    @PostMapping(value = "/flows/{flowId}/versions/{version}/publish", summary = "Orchestrator发布流程定义")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public void publishFlow(@PathVariable("tenant_id") String tenantId, @PathVariable("flowId") String flowId,
            @PathVariable("version") String version, @RequestBody Map<String, Object> body) {
        Validation.notBlank(cast(body.get("definitionData")),
                () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "definitionData"));
        checkFlowId(flowId);
        checkVersion(version);

        FlowSaveEntity flowSaveEntity = FlowsEngineWebService.buildFlowSaveEntity(flowId, version, body);
        OperationContext operationContext = buildOperationContext(tenantId, body);
        flowsEngineService.publishFlows(flowSaveEntity, operationContext);
    }

    private static OperationContext buildOperationContext(String tenantId, Map<String, Object> body) {
        return buildOperationContext(tenantId, ObjectUtils.<String>cast(body.get(OPERATOR_KEY)));
    }

    private static OperationContext buildOperationContext(String tenantId, String operator) {
        OperationContext operationContext = new OperationContext();
        operationContext.setTenantId(tenantId);
        operationContext.setOperator(operator);
        return operationContext;
    }

    /**
     * 保存流程定义
     *
     * @param tenantId 租户id标识
     * @param flowId 流程id
     * @param version 流程版本
     * @param body 请求体
     */
    @PostMapping(value = "/flows/{flowId}/versions/{version}/save", summary = "Orchestrator保存流程定义")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public void saveFlow(@PathVariable("tenant_id") String tenantId, @PathVariable("flowId") String flowId,
            @PathVariable("version") String version, @RequestBody Map<String, Object> body) {
        Validation.notBlank(ObjectUtils.cast(body.get("definitionData")),
                () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "definitionData"));
        checkFlowId(flowId);
        checkVersion(version);
        FlowSaveEntity flowSaveEntity = FlowsEngineWebService.buildFlowSaveEntity(flowId, version, body);
        OperationContext operationContext = buildOperationContext(tenantId, body);
        flowsEngineService.updateFlows(flowSaveEntity, operationContext);
    }

    private void checkFlowId(String flowId) {
        String regex = "^[0-9a-fA-F]{32}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(flowId);
        if (!matcher.matches()) {
            throw new JobberParamException(INPUT_PARAM_IS_INVALID, "flow id");
        }
    }

    private void checkVersion(String version) {
        String regex = "^\\d+(\\.\\d+){2}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(version);
        if (!matcher.matches()) {
            throw new JobberParamException(INPUT_PARAM_IS_INVALID, "version");
        }
    }

    /**
     * 根据flow_id和version去获取流程定义
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户id
     * @param flowId 流程定义id
     * @param version 流程版本
     * @return 流程定义详情信息
     */
    @GetMapping(value = "/{flow_id}/versions/{version}", summary = "查询流程定义详情")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findFlowByFlowIdAndVersion(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId,
            @PathVariable("flow_id") String flowId, @PathVariable("version") String version) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notBlank(flowId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowId"));
        Validation.notBlank(version, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "version"));
        FlowInfo flowInfo = flowsEngineService.getFlows(flowId, version, new OperationContext());
        return Views.viewOfFlowInfo(flowInfo);
    }

    /**
     * 根据flow_id去获取流程定义版本列表
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户id
     * @param flowId 流程定义id
     * @return 版本列表
     */
    @GetMapping(value = "/{flow_id}", summary = "查询流程定义版本列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findFlowsByFlowId(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId,
            @PathVariable("flow_id") String flowId) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notBlank(flowId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowId"));
        List<FlowGraphDefinition> flowGraphDefinitions = flowsEngineService.findFlowsByFlowId(flowId);
        return Views.viewOfFlowGraphList(flowGraphDefinitions);
    }

    /**
     * 分页查询流程定义列表
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tag 多个标签
     * @param createdBy 创建用户
     * @param tenantId 租户ID
     * @param offset 分页参数：偏移
     * @param limit 分页参数：限制
     * @return 流程定义列表和分页信息
     */
    @GetMapping(value = "/list", summary = "分页查询流程定义列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findFlowDefinitionsByUserOrTag(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse,
            @RequestParam(value = "tag", required = false, defaultValue = "") String tag,
            @RequestParam(value = "create_user", required = false) String createdBy,
            @PathVariable("tenant_id") String tenantId, @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        return Views.viewOf(flowsEngineService.findFlowList(createdBy, Collections.singletonList(tag), offset, limit,
                new OperationContext()), "definitions", Views::viewOfFlowGraphDefinition);
    }

    /**
     * 删除流程定义
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户id标识
     * @param flowId 流程定义metaId
     * @param version 流程定义版本
     */
    @DeleteMapping(value = "/{flow_id}/versions/{version}", summary = "根据流程定义元ID和版本删除流程定义")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void deleteFlows(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("flow_id") String flowId,
            @PathVariable("version") String version) {
        Validation.notBlank(flowId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowId"));
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notBlank(version, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "version"));
        flowsEngineService.deleteFlows(flowId, version, new OperationContext());
    }

    private String getOperator(HttpClassicServerRequest request) {
        return ControllerUtil.getOperator(request, this.authenticator);
    }
}
