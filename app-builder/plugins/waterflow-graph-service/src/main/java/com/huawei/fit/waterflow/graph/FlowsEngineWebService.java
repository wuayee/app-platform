/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.graph;

import static com.huawei.fit.jober.common.ErrorCodes.FLOW_DEFINITION_DELETE_ERROR;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_GRAPH_DATA_PARSE_FAILED;
import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;
import static com.huawei.fit.jober.common.ErrorCodes.NOT_SUPPORT;
import static com.huawei.fit.jober.common.ErrorCodes.UN_EXCEPTED_ERROR;
import static com.huawei.fit.waterflow.biz.common.Constant.STREAM_ID_SEPARATOR;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.flow.graph.entity.FlowGraphDefinition;
import com.huawei.fit.jane.flow.graph.entity.FlowGraphQueryParam;
import com.huawei.fit.jane.flow.graph.entity.FlowSaveEntity;
import com.huawei.fit.jane.flow.graph.entity.elsa.GraphParam;
import com.huawei.fit.jane.flow.graph.entity.elsa.response.GetPageResponse;
import com.huawei.fit.jane.flow.graph.repo.FlowsGraphRepo;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.gateway.User;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.entity.FlowInfo;
import com.huawei.fit.jober.entity.FlowNodeFormInfo;
import com.huawei.fit.jober.entity.FlowNodeInfo;
import com.huawei.fit.jober.entity.consts.NodeTypes;
import com.huawei.fit.waterflow.biz.stub.TagService;
import com.huawei.fit.waterflow.biz.util.FlowDefinitionParseUtils;
import com.huawei.fit.waterflow.biz.util.ParamUtils;
import com.huawei.fit.waterflow.flowsengine.biz.service.FlowContextsService;
import com.huawei.fit.waterflow.flowsengine.biz.service.FlowsService;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.tasks.FlowTask;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowTaskType;
import com.huawei.fit.waterflow.graph.util.Views;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.model.RangedResultSet;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 流程查询bff封装
 *
 * @author y00679285
 * @since 2023/10/11
 */
@Component
public class FlowsEngineWebService implements FlowsEngineService {
    private static final Logger log = Logger.get(FlowsEngineWebService.class);

    private static final String UNKNOWN_IP = "unknown";

    private static final String UNPUBLISHED_STATUS = "unpublished";

    private static final String PUBLISHED_STATUS = "active";

    private static final String FLOW_GRAPH = "FLOW GRAPH";

    private final FlowsService flowsService;

    private final FlowContextsService flowContextsService;

    private final FlowsGraphRepo flowsGraphRepo;

    private final Authenticator authenticator;

    private final TagService tagService;

    private final QueryFlowContextPersistRepo queryFlowContextPersistRepo;

    public FlowsEngineWebService(FlowContextsService flowContextsService, FlowsService flowsService,
            FlowsGraphRepo flowsGraphRepo, Authenticator authenticator,
            TagService tagService,
            QueryFlowContextPersistRepo queryFlowContextPersistRepo) {
        this.flowContextsService = flowContextsService;
        this.flowsService = flowsService;
        this.flowsGraphRepo = flowsGraphRepo;
        this.authenticator = authenticator;
        this.tagService = tagService;
        this.queryFlowContextPersistRepo = queryFlowContextPersistRepo;
    }

    /**
     * 构造flow的保存保存信息
     *
     * @param flowId flowId
     * @param version 版本
     * @param body 主要参数
     * @return 保存结果
     */
    public static FlowSaveEntity buildFlowSaveEntity(String flowId, String version, Map<String, Object> body) {
        FlowSaveEntity flowSaveEntity = new FlowSaveEntity();
        flowSaveEntity.setId(flowId);
        flowSaveEntity.setVersion(version);
        flowSaveEntity.setTags((List<String>) body.get("tags"));
        // todo 此处definitionData含义？
        flowSaveEntity.setGraphData((String) body.get("definitionData"));
        flowSaveEntity.setPrevious((String) body.get("previous"));
        return flowSaveEntity;
    }

    /**
     * 查询流程列表
     *
     * @param queryData 查询条件
     * @param httpRequest httpRequest
     * @param tenantId 租户id
     * @param operationContext 操作人信息
     * @return 流程定义列表
     */
    public GetPageResponse findFlowDefinitionsPage(String queryData, HttpClassicServerRequest httpRequest,
            String tenantId, OperationContext operationContext) {
        JSONObject param = JSONObject.parseObject(queryData);
        JSONObject pageParam = param.getJSONObject("page");
        Integer pageNo = pageParam.getInteger("pageNo");
        Integer pageSize = pageParam.getInteger("pageSize");
        FlowGraphQueryParam queryParam = FlowGraphQueryParam.builder()
                .creatUser(getUser(httpRequest))
                .offset(pageNo * pageSize)
                .limit(pageSize)
                .build();
        RangedResultSet<FlowGraphDefinition> flowList = flowsGraphRepo.getFlowList(queryParam, operationContext);
        GetPageResponse getPageResponse = GetPageResponse.builder()
                .code(0)
                .msg("")
                .count(flowList.getRange().getTotal())
                .cursor(flowList.getRange().getOffset())
                .data(flowList.getResults().stream().map(graph -> GetPageResponse.FlowInfo.builder()
                        .documentId(graph.getFlowId())
                        .text(graph.getName())
                        .version(graph.getStatus())
                        .createTime(graph.getCreatedAt().toString())
                        .createUser(graph.getCreatedBy())
                        .updateTime(graph.getUpdatedAt().toString())
                        .updateUser(graph.getUpdatedBy())
                        .build()).collect(Collectors.toList()))
                .build();

        log.info("FlowDefinition size: {}", getPageResponse.getData().size());
        Map<String, GetPageResponse.FlowInfo> flowInfoMap = getPageResponse.getData()
                .stream()
                .peek(f -> f.setVersionStatus("unpublished"))
                .collect(Collectors.toMap(d -> d.getDocumentId() + STREAM_ID_SEPARATOR + "1.0.0", d -> d));
        if (flowInfoMap.isEmpty()) {
            return getPageResponse;
        }

        ArrayList<String> streamIds = new ArrayList<>(flowInfoMap.keySet());
        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notEmpty(streamIds, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "streamIds"));

        List<Map<String, Object>> flows = Views.viewOfFlows(flowsService.findFlowsByStreamIds(streamIds));

        flows.forEach(f -> {
            GetPageResponse.FlowInfo flowInfo = flowInfoMap.get(f.get("streamId"));
            flowInfo.setReleaseTime(f.get("releaseTime").toString().replaceFirst("\\.\\d+$", ""));
            flowInfo.setVersionStatus(f.get("versionStatus").toString());
        });

        List<Map<String, Object>> flowContexts = flowContextsService.findContextStatusViewByStreamIds(streamIds,
                this.contextOf(httpRequest, tenantId));

        flowContexts.forEach(c -> {
            GetPageResponse.FlowInfo flowInfo = flowInfoMap.get(c.get("streamId"));
            flowInfo.setRunningInstance(ObjectUtils.cast(c.get("runningContexts")));
            flowInfo.setAllInstance(ObjectUtils.cast(c.get("allContexts")));
            flowInfo.setErrorInstance(ObjectUtils.cast(c.get("errorContexts")));
        });
        return getPageResponse;
    }

    public com.huawei.fit.jane.task.util.OperationContext contextOf(HttpClassicServerRequest request, String tenantId) {
        String ip = compute(
                Arrays.asList(FlowsEngineWebService::getForwardedIp, FlowsEngineWebService::getProxyClientIp,
                        FlowsEngineWebService::getWlProxyClientIp, FlowsEngineWebService::getHttpClientIp,
                        FlowsEngineWebService::getHttpForwardedFor), request);
        String operator = getOperator(request);
        return com.huawei.fit.jane.task.util.OperationContext.custom()
                .operator(operator)
                .operatorIp(ip)
                .tenantId(tenantId)
                .langage(getAcceptLangaes(request))
                .sourcePlatform(getSourcePlatform(request))
                .build();
    }

    private static String compute(List<Function<HttpClassicServerRequest, Optional<String>>> mappers,
            HttpClassicServerRequest request) {
        Optional<String> optional = Optional.empty();
        for (Function<HttpClassicServerRequest, Optional<String>> mapper : mappers) {
            optional = mapper.apply(request);
            if (optional.isPresent()) {
                break;
            }
        }
        return optional.orElse(request.remoteAddress().hostAddress());
    }

    private static Optional<String> getForwardedIp(HttpClassicServerRequest request) {
        return header(request, "X-Forwarded-For").map(value -> StringUtils.split(value, ','))
                .map(Stream::of)
                .orElse(Stream.empty())
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .filter(FlowsEngineWebService::knownIp)
                .findFirst();
    }

    private static Optional<String> getProxyClientIp(HttpClassicServerRequest request) {
        return header(request, "Proxy-Client-IP").filter(FlowsEngineWebService::knownIp);
    }

    private static Optional<String> getWlProxyClientIp(HttpClassicServerRequest request) {
        return header(request, "WL-Proxy-Client-IP").filter(FlowsEngineWebService::knownIp);
    }

    private static Optional<String> getHttpClientIp(HttpClassicServerRequest request) {
        return header(request, "HTTP_CLIENT_IP").filter(FlowsEngineWebService::knownIp);
    }

    private static Optional<String> getHttpForwardedFor(HttpClassicServerRequest request) {
        return header(request, "HTTP_X_FORWARDED_FOR").filter(FlowsEngineWebService::knownIp);
    }

    protected String getOperator(HttpClassicServerRequest request) {
        return this.authenticator.authenticate(request).fqn();
    }

    private static String getAcceptLangaes(HttpClassicServerRequest request) {
        return request.headers()
                .first("Accept-Language")
                .orElse(request.headers().first("accept-language").orElse(StringUtils.EMPTY));
    }

    private static String getSourcePlatform(HttpClassicServerRequest request) {
        return request.headers().first("SourcePlatform").orElse(StringUtils.EMPTY);
    }

    private static Optional<String> header(HttpClassicServerRequest request, String name) {
        return request.headers()
                .names()
                .stream()
                .filter(value -> StringUtils.equalsIgnoreCase(value, name))
                .findFirst()
                .flatMap(request.headers()::first);
    }

    private static boolean knownIp(String ip) {
        return !StringUtils.equalsIgnoreCase(ip, UNKNOWN_IP);
    }

    /**
     * 发布流程
     *
     * @param data 创建流程json
     * @param httpRequest httpRequest
     * @param tenantId 租户id
     * @param operationContext 操作人信息
     * @return 发布结果信息
     */
    public Map<String, Object> createFlows(Map<String, Object> data, HttpClassicServerRequest httpRequest,
            String tenantId, OperationContext operationContext) {
        Map<String, Object> flowGraph = (Map<String, Object>) data.get("flowData");

        Validation.notBlank(tenantId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "tenant"));
        Validation.notNull(flowGraph, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowGraph"));
        String definitionGraph = JSON.toJSONString(flowGraph);
        FlowDefinition flowDefinition = flowsService.createFlows(definitionGraph,
                this.contextOf(httpRequest, tenantId));
        Map<String, Object> flowsView = Views.viewOfFlows(flowDefinition, definitionGraph);

        FlowSaveEntity flowSaveEntity = FlowSaveEntity.builder()
                .id((String) flowsView.get("metaId"))
                .version((String) flowsView.get("version"))
                .status(UNPUBLISHED_STATUS)
                .graphData(JSON.toJSONString(data.get("graphData")))
                .previous(null)
                .build();
        FlowInfo flowInfo = this.saveFlow(flowSaveEntity, operationContext);
        return Views.viewOf(flowsView, flowInfo);
    }

    private String getUser(HttpClassicServerRequest request) {
        User user = this.authenticator.authenticate(request);
        String operator = user.name() + "," + user.account();
        try {
            operator = URLEncoder.encode(operator, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new JobberException(UN_EXCEPTED_ERROR, operator);
        }
        return operator;
    }

    @Override
    @Transactional
    public FlowInfo createFlows(FlowSaveEntity flowSaveEntity, OperationContext context) {
        flowSaveEntity.setStatus(UNPUBLISHED_STATUS);
        saveTags(flowSaveEntity, context);
        return saveFlow(flowSaveEntity, context);
    }

    @Override
    @Transactional
    public FlowInfo publishFlows(FlowSaveEntity flowSaveEntity, OperationContext context) {
        flowSaveEntity.setStatus(PUBLISHED_STATUS);
        saveTags(flowSaveEntity, context);
        if (flowSaveEntity.getDefinitionData() == null) {
            flowSaveEntity.setDefinitionData(convertToFlowDefinition(flowSaveEntity));
        }
        FlowDefinition flows = flowsService.createFlows(flowSaveEntity.getDefinitionData(),
                ParamUtils.convertToInternalOperationContext(context));
        FlowInfo flowInfo = saveFlow(flowSaveEntity, context);
        return publishFlowsHandle(flowInfo, flows);
    }

    @Override
    public FlowInfo publishFlowsWithoutElsa(FlowSaveEntity flowSaveEntity, OperationContext context) {
        FlowInfo flowInfo = this.buildFlowInfo(flowSaveEntity);
        FlowDefinition flows = flowsService.createFlows(flowSaveEntity.getDefinitionData(),
                ParamUtils.convertToInternalOperationContext(context));
        return publishFlowsHandle(flowInfo, flows);
    }

    private FlowInfo publishFlowsHandle(FlowInfo flowInfo, FlowDefinition flows) {
        flowInfo.setFlowDefinitionId(flows.getDefinitionId());
        flowInfo.setFlowNodes(getFlowNodeInfos(flows.getNodeMap()));
        return flowInfo;
    }

    private static List<FlowNodeInfo> getFlowNodeInfos(Map<String, FlowNode> nodeMap) {
        return nodeMap.entrySet().stream().map(entry -> {
            String nodeMetaId = entry.getKey();
            FlowNode node = entry.getValue();
            FlowNodeInfo flowNodeInfo = new FlowNodeInfo();
            flowNodeInfo.setId(nodeMetaId);
            flowNodeInfo.setName(node.getName());
            flowNodeInfo.setType(NodeTypes.valueOf(node.getType().getCode()).getType());
            FlowNodeFormInfo flowNodeForm = null;
            FlowTask task = node.getTask();
            if (task != null) {
                if (task.getTaskType() == FlowTaskType.AIPP_SMART_FORM) {
                    flowNodeForm = new FlowNodeFormInfo();
                    flowNodeForm.setFormId(task.getTaskId());
                    // todo暂时写死
                    flowNodeForm.setVersion("1.0.0");
                }
            }
            flowNodeInfo.setFlowNodeForm(flowNodeForm);
            flowNodeInfo.setProperties(node.getProperties()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, item -> item.getValue())));
            return flowNodeInfo;
        }).collect(Collectors.toList());
    }

    @Override
    public FlowInfo updateFlows(FlowSaveEntity flowSaveEntity, OperationContext context) {
        return createFlows(flowSaveEntity, context);
    }

    public FlowInfo upgradeFlows(FlowSaveEntity flowSaveEntity, OperationContext context) {
        GraphParam graphParam = new GraphParam();
        graphParam.setGraphId(flowSaveEntity.getId());
        graphParam.setVersion(flowSaveEntity.getVersion());
        graphParam.setJson(flowSaveEntity.getGraphData());
        int ans = flowsGraphRepo.upgradeFlows(graphParam);
        if (ans != 0) {
            String errMsg = String.format(Locale.ROOT, "upgrade flows failed. ans=%s graphParam=%s", ans,
                    JSON.toJSONString(graphParam));
            log.error(errMsg);
            throw new JobberException(UN_EXCEPTED_ERROR, errMsg);
        }
        return this.buildFlowInfo(flowSaveEntity);
    }

    private FlowInfo buildFlowInfo(FlowSaveEntity flowSaveEntity) {
        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setFlowId(flowSaveEntity.getId());
        flowInfo.setVersion(flowSaveEntity.getVersion());
        return flowInfo;
    }

    @Override
    public FlowInfo getFlows(String flowId, String version, OperationContext context) {
        FlowSaveEntity flowSaveEntity = new FlowSaveEntity();
        flowSaveEntity.setId(flowId);
        flowSaveEntity.setVersion(version);
        String flowGraphData = flowsGraphRepo.getFlow(flowSaveEntity, context);
        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setFlowNodes(getFlowNodeInfos(flowsService.getFlowDefinitionByGraphData(
                        FlowDefinitionParseUtils.getParsedGraphData(JSONObject.parseObject(flowGraphData), version))
                .getNodeMap()));
        flowInfo.setConfigData(flowGraphData);
        flowInfo.setFlowId(flowId);
        flowInfo.setVersion(version);
        return flowInfo;
    }

    @Override
    @Transactional
    public int forceDeleteFlows(String flowId, String version, OperationContext context) {
        int ans = deleteFlowGraph(flowId, version, context);
        flowsService.forceDeleteFlows(flowId, version, ParamUtils.convertToInternalOperationContext(context));
        removeTags(flowId, context);
        return ans;
    }

    private int deleteFlowGraph(String flowId, String version, OperationContext context) {
        FlowSaveEntity flowSaveEntity = new FlowSaveEntity();
        flowSaveEntity.setId(flowId);
        flowSaveEntity.setVersion(version);
        int ans = flowsGraphRepo.deleteFlow(flowSaveEntity, context);
        if (ans != 0) {
            log.error("delete flows failed.");
            throw new JobberException(UN_EXCEPTED_ERROR, "deleteFlows");
        }
        return ans;
    }

    @Override
    @Transactional
    public int deleteFlows(String flowId, String version, OperationContext context) {
        Integer flowContextNum = queryFlowContextPersistRepo.findUnarchivedContextCountByMetaId(flowId, version);
        if (flowContextNum != 0) {
            log.warn("The flow definition has related running flow, cannot be deleted");
            throw new JobberException(FLOW_DEFINITION_DELETE_ERROR);
        }
        int ans = deleteFlowGraph(flowId, version, context);
        flowsService.deleteFlows(flowId, version, ParamUtils.convertToInternalOperationContext(context));
        removeTags(flowId, context);
        return ans;
    }

    @Override
    public void deleteFlowsWithoutElsa(String flowId, String version, OperationContext context) {
        flowsService.deleteFlows(flowId, version, ParamUtils.convertToInternalOperationContext(context));
    }

    @Override
    public List<FlowGraphDefinition> findFlowsByFlowId(String flowId) {
        throw new JobberException(NOT_SUPPORT, "findFlowsByFlowId");
    }

    @Override
    public GetPageResponse findFlowDefinitionsPage(String data, String tenantId) {
        throw new JobberException(NOT_SUPPORT, "findFlowDefinitionsPage");
    }

    @Override
    public RangedResultSet<FlowGraphDefinition> findFlowList(String createdBy, List<String> tags, int offset, int limit,
            OperationContext context) {
        FlowGraphQueryParam queryParam = new FlowGraphQueryParam();
        queryParam.setCreatUser(createdBy);
        queryParam.setLimit(limit);
        queryParam.setOffset(offset);
        RangedResultSet<FlowGraphDefinition> flowList = RangedResultSet.create(new ArrayList<>(), offset, limit, 0);
        if (tags.isEmpty()) {
            flowList = flowsGraphRepo.getFlowList(queryParam, context);
        } else {
            List<String> flowIds = tagService.list(FLOW_GRAPH, tags);
            if (!flowIds.isEmpty()) {
                queryParam.setFlowIds(flowIds);
                flowList = flowsGraphRepo.getFlowList(queryParam, context);
            }
        }
        List<FlowGraphDefinition> resList = flowList.getResults();
        List<String> allIds = resList.stream().map(FlowGraphDefinition::getFlowId).collect(Collectors.toList());
        Map<String, List<String>> tagMap = tagService.list(FLOW_GRAPH, allIds, null);
        resList.forEach(
                flowGraphDefinition -> flowGraphDefinition.setTags(tagMap.get(flowGraphDefinition.getFlowId())));
        return RangedResultSet.create(resList, offset, limit, flowList.getRange().getTotal());
    }

    private FlowInfo saveFlow(FlowSaveEntity flowSaveEntity, OperationContext context) {
        int ans = flowsGraphRepo.saveFlow(flowSaveEntity, context);
        if (ans != 0) {
            log.error("Create flows failed.");
            throw new JobberException(UN_EXCEPTED_ERROR, "createFlows");
        }
        return this.buildFlowInfo(flowSaveEntity);
    }

    private void saveTags(FlowSaveEntity flowSaveEntity, OperationContext context) {
        Map<String, List<String>> saveTags = new HashMap<>();
        saveTags.put(flowSaveEntity.getId(), flowSaveEntity.getTags());
        tagService.save(FLOW_GRAPH, saveTags, ParamUtils.convertToInternalOperationContext(context));
    }

    private void removeTags(String flowId, OperationContext context) {
        com.huawei.fit.jane.task.util.OperationContext operationContext = ParamUtils.convertToInternalOperationContext(
                context);
        List<String> tags = tagService.list(FLOW_GRAPH, flowId, operationContext);
        tags.forEach(tag -> tagService.remove(FLOW_GRAPH, flowId, tag, operationContext));
    }

    private String convertToFlowDefinition(FlowSaveEntity flowSaveEntity) {
        String formatData;
        try {
            JSONObject parsedData = JSONObject.parseObject(flowSaveEntity.getGraphData());
            formatData = FlowDefinitionParseUtils.getParsedGraphData(parsedData, flowSaveEntity.getVersion());
        } catch (JSONException | FitException | NullPointerException e) {
            log.error("parse graph data failed.");
            throw new JobberException(FLOW_GRAPH_DATA_PARSE_FAILED);
        }
        return formatData;
    }

    private String getCookie(HttpClassicServerRequest request) {
        return request.headers().first("cookie").orElse(StringUtils.EMPTY);
    }

    @Override
    public FlowInfo getFlowDefinitionById(String definitionId, OperationContext context) {
        FlowDefinition flowDefinition = flowsService.findFlowsById(definitionId,
                ParamUtils.convertToInternalOperationContext(context));
        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setFlowDefinitionId(definitionId);
        flowInfo.setFlowId(flowDefinition.getMetaId());
        flowInfo.setVersion(flowDefinition.getVersion());
        flowInfo.setFlowNodes(getFlowNodeInfos(flowDefinition.getNodeMap()));
        return flowInfo;
    }
}
