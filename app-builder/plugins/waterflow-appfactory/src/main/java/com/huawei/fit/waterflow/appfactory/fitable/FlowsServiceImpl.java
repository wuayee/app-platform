/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.appfactory.fitable;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.flow.graph.entity.FlowSaveEntity;
import com.huawei.fit.jober.FlowsService;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.entity.FlowInfo;
import com.huawei.fit.waterflow.graph.FlowsEngineWebServiceForDbGraph;
import com.huawei.fit.waterflow.graph.util.FlowDefinitionParseUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * {@link FlowsService} 实现类
 *
 * @author 杨祥宇
 * @since 2023/12/13
 */
@Component
public class FlowsServiceImpl implements FlowsService {
    private final FlowsEngineWebServiceForDbGraph flowsEngineService;

    public FlowsServiceImpl(FlowsEngineWebServiceForDbGraph flowsEngineService) {
        this.flowsEngineService = flowsEngineService;
    }

    private static FlowSaveEntity buildFlowSaveEntity(String flowId, String version, String configData) {
        String flowDefinitionData =
                FlowDefinitionParseUtils.getParsedGraphData(JSONObject.parseObject(configData), version);
        return FlowSaveEntity.builder()
                .id(flowId)
                .version(version)
                .graphData(configData)
                .definitionData(flowDefinitionData)
                .build();
    }

    private static void paramValidation(String flowId, String version, OperationContext context) {
        Validation.notBlank(flowId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowId"));
        Validation.notBlank(version, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "version"));
        Validation.notNull(context, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "context"));
    }

    private static void paramValidation(String flowId, String version, String configData, OperationContext context) {
        paramValidation(flowId, version, context);
        Validation.notBlank(configData, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "configData"));
    }

    @Override
    @Fitable(id = "5ab91218fdd04b95a9ae4971453bbce0")
    public FlowInfo createFlows(String configData, OperationContext context) {
        Validation.notBlank(configData, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "configData"));
        Validation.notNull(context, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "context"));
        JSONObject parsedData = JSONObject.parseObject(configData);
        String flowId = ObjectUtils.cast(parsedData.get("id"));
        String version = ObjectUtils.cast(parsedData.get("version"));
        FlowSaveEntity flowSaveEntity = FlowSaveEntity.builder()
                .graphData(configData)
                .id(flowId)
                .version(version)
                .build();
        return flowsEngineService.createFlows(flowSaveEntity, context);
    }

    @Override
    @Fitable(id = "c64c0a6c5d184f44ac4e22744f71d267")
    public FlowInfo updateFlows(String flowId, String version, String configData, OperationContext context) {
        paramValidation(flowId, version, configData, context);
        FlowSaveEntity flowSaveEntity = FlowSaveEntity.builder()
                .id(flowId)
                .version(version)
                .graphData(configData)
                .build();
        return flowsEngineService.createFlows(flowSaveEntity, context);
    }

    @Override
    @Fitable(id = "da67602ef44942449da1b1c9b2ee9e70")
    public FlowInfo upgradeFlows(String flowId, String newVersion, String configData, OperationContext context) {
        paramValidation(flowId, newVersion, configData, context);
        FlowSaveEntity flowSaveEntity =
                FlowSaveEntity.builder().id(flowId).version(newVersion).graphData(configData).build();
        return flowsEngineService.upgradeFlows(flowSaveEntity, context);
    }

    @Override
    @Fitable(id = "286ccb60c353444bab312bc9c45d8ae6")
    public FlowInfo publishFlows(String flowId, String version, String configData, OperationContext context) {
        paramValidation(flowId, version, configData, context);
        FlowSaveEntity flowSaveEntity = buildFlowSaveEntity(flowId, version, configData);
        return flowsEngineService.publishFlows(flowSaveEntity, context);
    }

    @Override
    @Fitable(id = "ac191b559ab14688a5219172638afb01")
    public FlowInfo publishFlowsWithoutElsa(String flowId, String version, String configData,
            OperationContext context) {
        paramValidation(flowId, version, configData, context);
        FlowSaveEntity flowSaveEntity = buildFlowSaveEntity(flowId, version, configData);
        return flowsEngineService.publishFlowsWithoutElsa(flowSaveEntity, context);
    }

    @Override
    @Fitable(id = "a70c9b525dc84869bf56ab92f037c5b2")
    public FlowInfo getFlows(String flowId, String version, OperationContext context) {
        paramValidation(flowId, version, context);
        return flowsEngineService.getFlows(flowId, version, context);
    }

    @Override
    @Fitable(id = "0a56aaadeefa4f42801fd58c4d4e83e0")
    public FlowInfo getFlows(String definitionId, OperationContext context) {
        return flowsEngineService.getFlowDefinitionById(definitionId, context);
    }

    @Override
    @Fitable(id = "79cc73a7d3954d9987efc742bff19686")
    public int deleteFlows(String flowId, String version, OperationContext context) {
        paramValidation(flowId, version, context);
        return flowsEngineService.deleteFlows(flowId, version, context);
    }

    @Override
    @Fitable(id = "79c2b5deee8c406f9d882b4d13fa031b")
    public void deleteFlowsWithoutElsa(String flowId, String version, OperationContext context) {
        paramValidation(flowId, version, context);
        flowsEngineService.deleteFlowsWithoutElsa(flowId, version, context);
    }
}
