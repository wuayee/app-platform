/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.tianzhou.controller;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;
import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;

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
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.common.model.JoberResponse;
import modelengine.fit.waterflow.biz.common.vo.FlowCallbackVO;
import modelengine.fit.waterflow.biz.common.vo.FlowDefinitionVO;
import modelengine.fit.waterflow.biz.common.vo.FlowEventVO;
import modelengine.fit.waterflow.biz.common.vo.FlowFilterVO;
import modelengine.fit.waterflow.biz.common.vo.FlowJoberVO;
import modelengine.fit.waterflow.biz.common.vo.FlowNodeVO;
import modelengine.fit.waterflow.biz.common.vo.FlowTaskVO;
import modelengine.fit.waterflow.biz.util.ControllerUtil;
import modelengine.fit.waterflow.biz.util.Views;
import modelengine.fit.waterflow.flowsengine.biz.service.FlowsService;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.events.FlowEvent;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.filters.FlowFilter;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.tasks.FlowTask;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowDefinitionPO;
import modelengine.fit.waterflow.tianzhou.Constant;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;

import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 流程定义相关controller
 *
 * @author 杨祥宇
 * @since 2023/8/1
 */
@Component
@RequestMapping(value = Constant.BASE_URI_PREFIX + "/flows", group = "流程定义管理接口")
public class FlowsController {
    private static final Logger log = Logger.get(FlowsController.class);

    private final FlowsService flowsService;

    private final Authenticator authenticator;

    public FlowsController(Authenticator authenticator, FlowsService flowsService) {
        this.authenticator = authenticator;
        this.flowsService = flowsService;
    }

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
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> createFlows(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestBody Map<String, Object> graphData) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notNull(graphData, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "graphData"));
        String definitionGraph = JSON.toJSONString(graphData);
        FlowDefinition flowDefinition =
                flowsService.createFlows(definitionGraph, this.contextOf(httpRequest, tenantId));
        return Views.viewOfFlows(flowDefinition, definitionGraph);
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
    @ResponseStatus(HttpResponseStatus.CREATED)
    public Map<String, Object> updateFlows(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("flowId") String flowId,
            @RequestBody Map<String, Object> graphData) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notBlank(flowId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowId"));
        String definitionGraph = JSON.toJSONString(graphData);
        FlowDefinition flowDefinition =
                flowsService.updateFlows(flowId, definitionGraph, this.contextOf(httpRequest, tenantId));
        return Views.viewOfFlows(flowDefinition, definitionGraph);
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
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notBlank(flowId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowId"));
        FlowDefinition flowDefinition = flowsService.findFlowsById(flowId, this.contextOf(httpRequest, tenantId));
        return Views.viewOfFlows(FlowsController.convert(flowDefinition));
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
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void deleteFlows(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @PathVariable("flowId") String flowId) {
        Validation.notBlank(flowId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowId"));
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        flowsService.deleteFlows(flowId, this.contextOf(httpRequest, tenantId));
    }

    /**
     * 查询租户下流程定义列表
     * 返回流程名称以及版本号以及状态
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @return 流程定义
     */
    @GetMapping(summary = "查询租户下流程定义列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public List<Map<String, Object>> findFlowsByTenant(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        List<FlowDefinitionPO> flows = flowsService.findFlowsByTenant(this.contextOf(httpRequest, tenantId));
        return Views.viewOfFlows(flows);
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
            @RequestParam("name") String name, @PathVariable("version") String version) {
        Validation.notBlank(name, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "name"));
        Validation.notBlank(version, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "version"));
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        String originName;
        try {
            originName = URLDecoder.decode(name, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error("Cannot parser name: {}", name);
            throw new JobberParamException(INPUT_PARAM_IS_INVALID, "name");
        }
        FlowDefinitionPO flows =
                flowsService.findFlowsByName(originName, version, this.contextOf(httpRequest, tenantId));
        return Views.viewOfFlows(flows);
    }

    /**
     * 根据metaId 和 version查询对应的流程定义
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param metaId 流程定义元数据ID
     * @param version 流程定义版本
     * @return 流程定义
     */
    @GetMapping(value = "/meta-ids/{metaId}/versions/{version}", summary = "根据流程定义的metaId和version查询流程定义")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findFlowsByMetaIdAndVersion(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId,
            @PathVariable("metaId") String metaId, @PathVariable("version") String version) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notBlank(metaId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "metaId"));
        Validation.notBlank(version, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "version"));
        FlowDefinitionPO flowDefinitionPO =
                flowsService.findFlowsByMetaIdAndVersion(metaId, version, this.contextOf(httpRequest, tenantId));
        return Views.viewOfFlowStatus(flowDefinitionPO);
    }

    /**
     * 根据stream列表查询对应流程定义信息
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param streamIds 流程streamId列表
     * @return 流程定义列表
     */
    @PostMapping(value = "/stream-ids", summary = "根据streamId列表查询流程定义列表")
    @ResponseStatus(HttpResponseStatus.OK)
    public List<Map<String, Object>> findFlowsByStreamIds(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse, @PathVariable("tenant_id") String tenantId,
            @RequestBody List<String> streamIds) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notEmpty(streamIds, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "streamIds"));
        Map<String, FlowDefinitionPO> flows = flowsService.findFlowsByStreamIds(streamIds);
        return Views.viewOfFlows(flows);
    }

    /**
     * 根据fitableIds获取ohScript脚本
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @param tenantId 租户ID
     * @param fitableIds 调用服务ids
     * @return 脚本代码
     */
    @PostMapping(value = "/ohscripts", summary = "根据fitableIds获取ohScript脚本")
    @ResponseStatus(HttpResponseStatus.OK)
    public JoberResponse getScript(HttpClassicServerRequest httpRequest, HttpClassicServerResponse httpResponse,
            @PathVariable("tenant_id") String tenantId, @RequestBody List<String> fitableIds) {
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notNull(fitableIds, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "fitableIds"));
        return JoberResponse.success(flowsService.getScript(fitableIds));
    }

    private static FlowDefinitionVO convert(FlowDefinition flowDefinition) {
        if (flowDefinition == null) {
            return null;
        }
        return FlowDefinitionVO.builder()
                .definitionId(flowDefinition.getDefinitionId())
                .metaId(flowDefinition.getMetaId())
                .name(flowDefinition.getName())
                .description(flowDefinition.getDescription())
                .version(flowDefinition.getVersion())
                .tenant(flowDefinition.getTenant())
                .createdBy(flowDefinition.getCreatedBy())
                .nodeMap(convertFlowNodeMap(flowDefinition.getNodeMap()))
                .status(flowDefinition.getStatus().name())
                .releaseTime(flowDefinition.getReleaseTime())
                .streamId(flowDefinition.getStreamId())
                .fromEvents(convert(flowDefinition.getFromEvents()))
                .nodeIdSet(flowDefinition.getNodeIdSet())
                .build();
    }

    private static Map<String, FlowNodeVO> convertFlowNodeMap(Map<String, FlowNode> flowNodeMap) {
        if (flowNodeMap == null) {
            return null;
        }
        return flowNodeMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> convert(entry.getValue())));
    }

    private static FlowNodeVO convert(FlowNode flowNode) {
        if (flowNode == null) {
            return null;
        }
        return FlowNodeVO.builder()
                .metaId(flowNode.getMetaId())
                .name(flowNode.getName())
                .type(flowNode.getType().name())
                .triggerMode(flowNode.getTriggerMode().name())
                .properties(flowNode.getProperties())
                .events(flowNode.getEvents() == null
                        ? null
                        : flowNode.getEvents().stream().map(FlowsController::convert).collect(Collectors.toList()))
                .jober(FlowsController.convert(flowNode.getJober()))
                .joberFilter(FlowsController.convert(flowNode.getJoberFilter()))
                .task(FlowsController.convert(flowNode.getTask()))
                .taskFilter(FlowsController.convert(flowNode.getTaskFilter()))
                .callback(FlowsController.convert(flowNode.getCallback()))
                .build();
    }

    private static FlowCallbackVO convert(FlowCallback flowCallback) {
        if (flowCallback == null) {
            return null;
        }
        return FlowCallbackVO.builder()
                .nodeMetaId(flowCallback.getNodeMetaId())
                .name(flowCallback.getName())
                .type(flowCallback.getType().name())
                .filteredKeys(flowCallback.getFilteredKeys())
                .fitables(flowCallback.getFitables())
                .properties(flowCallback.getProperties())
                .build();
    }

    private static FlowTaskVO convert(FlowTask flowTask) {
        if (flowTask == null) {
            return null;
        }
        return FlowTaskVO.builder()
                .taskId(flowTask.getTaskId())
                .taskType(flowTask.getTaskType().name())
                .exceptionFitables(flowTask.getExceptionFitables())
                .properties(flowTask.getProperties())
                .build();
    }

    private static FlowFilterVO convert(FlowFilter flowFilter) {
        if (flowFilter == null) {
            return null;
        }
        return FlowFilterVO.builder()
                .filterType(flowFilter.getFilterType().name())
                .properties(flowFilter.getProperties())
                .build();
    }

    private static FlowJoberVO convert(FlowJober flowJober) {
        if (flowJober == null) {
            return null;
        }
        return FlowJoberVO.builder()
                .nodeMetaId(flowJober.getNodeMetaId())
                .nodeId(flowJober.getNodeId())
                .name(flowJober.getName())
                .type(flowJober.getType().name())
                .properties(flowJober.getProperties())
                .fitables(flowJober.getFitables())
                .exceptionFitables(flowJober.getExceptionFitables())
                .build();
    }

    private static Map<String, Set<FlowEventVO>> convert(Map<String, Set<FlowEvent>> flowEventMap) {
        if (flowEventMap == null) {
            return null;
        }
        return flowEventMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> convert(entry.getValue())));
    }

    private static Set<FlowEventVO> convert(Set<FlowEvent> flowEventSet) {
        if (flowEventSet == null) {
            return null;
        }
        return flowEventSet.stream().map(FlowsController::convert).collect(Collectors.toSet());
    }

    private static FlowEventVO convert(FlowEvent flowEvent) {
        if (flowEvent == null) {
            return null;
        }
        return FlowEventVO.builder()
                .metaId(flowEvent.getMetaId())
                .name(flowEvent.getName())
                .from(flowEvent.getFrom())
                .to(flowEvent.getTo())
                .conditionRule(flowEvent.getConditionRule())
                .build();
    }

    private OperationContext contextOf(HttpClassicServerRequest request, String tenantId) {
        return ControllerUtil.contextOf(request, tenantId, this.authenticator);
    }
}
