/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.biz.service;

import static com.huawei.fit.jober.common.ErrorCodes.ENTITY_NOT_FOUND;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_DEFINITION_UPDATE_NOT_SUPPORT;
import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;
import static com.huawei.fit.waterflow.common.Constant.STREAM_ID_SEPARATOR;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.repo.QueryFlowDefinitionRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowDefinitionStatus;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.FlowParser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.validators.FlowValidator;
import com.huawei.fit.waterflow.flowsengine.persist.po.FlowDefinitionPO;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程定义相关服务
 *
 * @author y00679285
 * @since 2023/8/1
 */
@Component
@RequiredArgsConstructor
public class FlowsService {
    private static final Logger log = Logger.get(FlowsService.class);

    private final FlowParser flowParser;

    private final FlowValidator flowValidator;

    private final FlowDefinitionRepo flowDefinitionRepo;

    private final FlowContextPersistRepo flowContextPersistRepo;

    private final QueryFlowDefinitionRepo queryFlowDefinitionRepo;

    /**
     * 创建流程定义
     *
     * @param graphData 流程定义json数据 {@link String}
     * @param context 表示操作上下文的 {@link OperationContext}
     * @return 流程定义对象 {@link FlowDefinition}
     */
    public FlowDefinition createFlows(String graphData, OperationContext context) {
        Validation.notBlank(graphData, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "graphData"));
        FlowDefinition flowsDefinition = flowParser.parse(graphData);
        flowsDefinition.setTenant(context.tenantId());
        flowsDefinition.setCreatedBy(context.operator());
        flowValidator.validate(flowsDefinition);
        flowDefinitionRepo.save(flowsDefinition, graphData);
        return flowsDefinition;
    }

    /**
     * 更新流程定义
     *
     * @param flowId 流程定义id标识
     * @param graphData 流程定义json数据
     * @param operationContext 表示操作上下文的 {@link OperationContext}
     * @return 流程定义对象
     */
    @Deprecated
    public FlowDefinition updateFlows(String flowId, String graphData, OperationContext operationContext) {
        FlowDefinition flowDefinition = flowDefinitionRepo.find(flowId);
        FlowDefinition parseFlow = flowParser.parse(graphData);
        Validation.notNull(flowDefinition, () -> new JobberParamException(ENTITY_NOT_FOUND, "FlowDefinition", flowId));
        Validation.isTrue(FlowDefinitionStatus.ACTIVE.equals(flowDefinition.getStatus()),
                () -> new JobberParamException(FLOW_DEFINITION_UPDATE_NOT_SUPPORT, flowDefinition.getStatus()));
        parseFlow.setDefinitionId(flowDefinition.getDefinitionId());
        flowDefinitionRepo.update(parseFlow, graphData);
        return parseFlow;
    }

    /**
     * 删除流程定义
     *
     * @param flowId 流程定义id标识 {@link String}
     * @param context 表示操作上下文的 {@link OperationContext}
     */
    @Transactional
    public void deleteFlows(String flowId, OperationContext context) {
        FlowDefinition flowDefinition = flowDefinitionRepo.find(flowId);
        Integer runningContextNum = flowContextPersistRepo.findRunningContextCountByMetaId(flowDefinition.getMetaId(),
                flowDefinition.getVersion());
        if (runningContextNum == 0) {
            flowDefinitionRepo.delete(flowId);
            flowContextPersistRepo.delete(flowDefinition.getMetaId(), flowDefinition.getVersion());
        }
    }

    /**
     * 根据metaid和version删除流程定义
     *
     * @param metaId 流程metaId {@link String}
     * @param version 版本 {@link String}
     * @param context 表示操作上下文的 {@link OperationContext}
     */
    public void deleteFlows(String metaId, String version, OperationContext context) {
        FlowDefinition flowDefinition = flowDefinitionRepo.findByMetaIdAndVersion(metaId, version);
        if (flowDefinition == null) {
            return;
        }
        deleteFlows(flowDefinition.getDefinitionId(), context);
    }

    /**
     * 根据metaid和version强制流程定义
     *
     * @param metaId metaId
     * @param version version
     * @param context context
     */
    @Transactional
    public void forceDeleteFlows(String metaId, String version, OperationContext context) {
        FlowDefinition flowDefinition = flowDefinitionRepo.findByMetaIdAndVersion(metaId, version);
        if (flowDefinition == null) {
            return;
        }
        flowDefinitionRepo.delete(flowDefinition.getDefinitionId());
        flowContextPersistRepo.delete(flowDefinition.getMetaId(), flowDefinition.getVersion());
    }

    /**
     * 根据租户id获取流程定义对象列表
     *
     * @param context 表示操作上下文的 {@link OperationContext}
     * @return 流程定义PO对象列表 {@link List} {@code <} {@link FlowDefinitionPO} {@code >}
     */
    public List<FlowDefinitionPO> findFlowsByTenant(OperationContext context) {
        return flowDefinitionRepo.findByTenantId(context.tenantId());
    }

    /**
     * 根据流程名称和版本获取对应流程定义
     *
     * @param name 流程定义名称 {@link String}
     * @param version 流程定义版本 {@link String}
     * @param context 表示操作上下文的 {@link OperationContext}
     * @return 流程定义PO对象 {@link FlowDefinitionPO}
     */
    public FlowDefinitionPO findFlowsByName(String name, String version, OperationContext context) {
        return flowDefinitionRepo.findByFlowNameVersion(name, version);
    }

    /**
     * 根据流程定义Id获取对应流程定义
     *
     * @param flowId 流程定义对应id {@link String}
     * @param context 表示操作上下文的 {@link OperationContext}
     * @return 流程定义对象 {@link FlowDefinition}
     */
    public FlowDefinition findFlowsById(String flowId, OperationContext context) {
        return flowDefinitionRepo.find(flowId);
    }

    /**
     * 根据流程的metaId和version查询对应流程定义
     *
     * @param metaId 流程的metaId {@link String}
     * @param version 流程定义版本 {@link String}
     * @param context 表示操作上下文的 {@link OperationContext}
     * @return 流程定义对象 {@link FlowDefinition}
     */
    public FlowDefinitionPO findFlowsByMetaIdAndVersion(String metaId, String version, OperationContext context) {
        return queryFlowDefinitionRepo.findByMetaIdAndVersion(metaId, version);
    }

    /**
     * 根据流程的metaId和部分version查询对应流程定义
     *
     * @param metaId 流程的metaId {@link String}
     * @param version 流程定义版本 {@link String}
     * @return 流程定义对象 {@link FlowDefinition}
     */
    public List<FlowDefinitionPO> findFlowsByMetaIdAndPartVersion(String metaId, String version) {
        return queryFlowDefinitionRepo.findByMetaIdAndPartVersion(metaId, version);
    }

    /**
     * 根据metaId和version查询流程定义
     *
     * @param streamIds 流程streamId列表
     * @return 流程定义
     */
    public Map<String, FlowDefinitionPO> findFlowsByStreamIds(List<String> streamIds) {
        List<FlowDefinitionPO> flowDefinitionPOList = queryFlowDefinitionRepo.findByStreamId(streamIds);
        return flowDefinitionPOList.stream()
                .collect(Collectors.toMap(f -> f.getMetaId() + STREAM_ID_SEPARATOR + f.getVersion(), f -> f));
    }

    /**
     * 根据fitableIds获取ohScript脚本
     *
     * @param fitableIds 调用服务ids
     * @return 脚本代码
     */
    public String getScript(List<String> fitableIds) {
        if (fitableIds.isEmpty()) {
            return "ext::context";
        }
        StringBuilder script = new StringBuilder();
        for (int i = 0; i < fitableIds.size(); i++) {
            script.append("let context")
                    .append(i + 1)
                    .append(" = entity{\n" + "    .id = \"")
                    .append(fitableIds.get(i))
                    .append("\";\n" + ".async = true;\n")
                    .append(".format = \"cbor\";\n" + "};\n")
                    .append("let f")
                    .append(i + 1)
                    .append(" = fit::handleTask(context")
                    .append(i + 1)
                    .append(");\n\n");
        }
        script.append("ext::context");
        for (int i = 0; i < fitableIds.size(); i++) {
            script.append(" >> f").append(i + 1);
        }
        return script.toString();
    }

    /**
     * 根据fitableid分页查询使用了这个fitable的流程定义
     *
     * @param fitableId fitable id
     * @param offset offset
     * @param limit limit
     * @return 流程定义列表和总数
     */
    public Map<String, Object> getFlowDefinitionByFitable(String fitableId, Integer offset, Integer limit) {
        log.info("Get flow definition by fitable id: {}", fitableId);
        Map<String, Object> res = new HashMap<>();
        res.put("definitions", queryFlowDefinitionRepo.findByFitableId(fitableId, offset, limit)
                .stream()
                .map(FlowsService::viewOfFlowDefinition)
                .collect(Collectors.toList()));
        res.put("total", queryFlowDefinitionRepo.getCountByFitableId(fitableId));
        return res;
    }

    /**
     * 组装definition信息
     *
     * @param definition definition
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOfFlowDefinition(FlowDefinitionPO definition) {
        Map<String, Object> view = new LinkedHashMap<>(4);
        put(view, "id", definition.getMetaId());
        put(view, "version", definition.getVersion());
        put(view, "name", definition.getName());
        put(view, "graph", definition.getGraph());
        return view;
    }

    private static void put(Map<String, Object> view, String key, Object value) {
        if (value != null) {
            view.put(key, value);
        }
    }

    /**
     * getCountByFitable
     *
     * @param fitableIds fitableIds
     * @return Map<String, Integer>
     */
    public Map<String, Integer> getCountByFitable(List<String> fitableIds) {
        log.info("Get flow definition count by fitable ids: {}", fitableIds);
        List<Map<String, Object>> result = queryFlowDefinitionRepo.selectFitableCounts(fitableIds);
        Map<String, Integer> formatResult = new HashMap<>();
        result.forEach(entry -> {
            formatResult.put(ObjectUtils.cast(entry.get("fitableId")),
                    Integer.parseInt(String.valueOf(entry.get("count"))));
        });
        return formatResult;
    }

    /**
     * 通过graph数据获取FlowDefinition信息
     *
     * @param flowGraph graph数据
     * @return FlowDefinition信息
     */
    public FlowDefinition getFlowDefinitionByGraphData(String flowGraph) {
        return flowParser.parse(flowGraph);
    }
}
