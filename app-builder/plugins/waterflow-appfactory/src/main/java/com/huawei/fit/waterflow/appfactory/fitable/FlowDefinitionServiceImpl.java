/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.appfactory.fitable;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.FlowDefinitionService;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.entity.FlowDefinitionResult;
import com.huawei.fit.waterflow.biz.util.FlowDefinitionParseUtils;
import com.huawei.fit.waterflow.biz.util.ParamUtils;
import com.huawei.fit.waterflow.flowsengine.biz.service.FlowsService;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.persist.po.FlowDefinitionPO;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.inspection.Validation;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link FlowDefinitionService}实现类
 *
 * @author y00679285
 * @since 2023/12/12
 */
@Component
public class FlowDefinitionServiceImpl implements FlowDefinitionService {
    private final FlowsService flowsService;

    public FlowDefinitionServiceImpl(FlowsService flowsService) {
        this.flowsService = flowsService;
    }

    @Override
    @Fitable("c35226173ac9411886b239ce097b6eaa")
    public FlowDefinitionResult createFlowDefinition(String definitionData, OperationContext context) {
        Validation.notBlank(definitionData, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "definitionData"));
        FlowDefinition flowDefinition =
                flowsService.createFlows(definitionData, ParamUtils.convertToInternalOperationContext(context));
        return new FlowDefinitionResult(flowDefinition.getDefinitionId());
    }

    @Override
    @Fitable("cee425da76cd4cc99040ed10a2c91d8f")
    public FlowDefinitionResult updateFlowDefinition(String flowDefinitionId, String definitionData,
            OperationContext context) {
        Validation.notBlank(flowDefinitionId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowDefinitionId"));
        Validation.notBlank(definitionData, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "definitionData"));
        FlowDefinition flowDefinition = flowsService.updateFlows(flowDefinitionId,
                definitionData,
                ParamUtils.convertToInternalOperationContext(context));
        return new FlowDefinitionResult(flowDefinition.getDefinitionId());
    }

    @Override
    @Fitable("cee425da76cd4cc99040ed10a2c91d80")
    public List<FlowDefinitionResult> getFlowDefinitionByMetaIdAndPartVersion(String metaId, String version,
            OperationContext context) {
        return this.flowsService.findFlowsByMetaIdAndPartVersion(metaId, version)
                .stream()
                .map(this::buildFlowDefinitionResult)
                .collect(Collectors.toList());
    }

    @Override
    @Fitable("cee425da76cd4cc99040ed10a2c91d81")
    public String getParsedGraphData(String flowViewData, String version) {
        return FlowDefinitionParseUtils.getParsedGraphData(JSONObject.parseObject(flowViewData), version);
    }

    private FlowDefinitionResult buildFlowDefinitionResult(FlowDefinitionPO po) {
        FlowDefinitionResult result = new FlowDefinitionResult(po.getDefinitionId());
        result.setMetaId(po.getMetaId());
        result.setName(po.getName());
        result.setTenantId(po.getTenant());
        result.setVersion(po.getVersion());
        result.setStatus(po.getStatus());
        result.setGraph(po.getGraph());
        return result;
    }

    @Override
    @Fitable("1f7b0c6d61ddbb3ec78ee51112386a44")
    public void deleteFlows(String flowId, OperationContext context) {
        flowsService.deleteFlows(flowId, ParamUtils.convertToInternalOperationContext(context));
    }

    @Override
    @Fitable("2b2f0128e632508a2d9fd1783c54e1ac")
    public void deleteFlows(String metaId, String version, OperationContext context) {
        flowsService.deleteFlows(metaId, version, ParamUtils.convertToInternalOperationContext(context));
    }
}
