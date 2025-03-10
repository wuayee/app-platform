/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


package modelengine.fit.waterflow.appfactory.fitable;

import static modelengine.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;

import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.common.exceptions.JobberParamException;
import modelengine.fit.jober.entity.FlowInstanceResult;
import modelengine.fit.waterflow.entity.FlowStartDTO;
import modelengine.fit.waterflow.entity.FlowStartInfo;
import modelengine.fit.waterflow.entity.JoberErrorInfo;
import modelengine.fit.waterflow.service.SingleFlowRuntimeService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.inspection.Validation;

import java.util.Map;

/**
 * {@link FlowInstanceService}实现类
 *
 * @author 杨祥宇
 * @since 2023/12/12
 */
@Component
public class FlowInstanceServiceImpl implements FlowInstanceService {
    private final SingleFlowRuntimeService singleFlowRuntimeService;


    public FlowInstanceServiceImpl(SingleFlowRuntimeService singleFlowRuntimeService) {
        this.singleFlowRuntimeService = singleFlowRuntimeService;
    }

    @Override
    @Fitable(id = "378a7970e68a4ee497da8855361ef2f5")
    public FlowInstanceResult startFlow(String flowDefinitionId, FlowStartInfo startInfo,
            OperationContext context) {
        Validation.notBlank(flowDefinitionId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowId"));
        FlowStartDTO flowStartDTO = singleFlowRuntimeService.startFlow(flowDefinitionId, startInfo,
                AippFlowDefinitionServiceImpl.convertOperationContext(context));
        return new FlowInstanceResult(flowStartDTO.getTraceId());
    }

    @Override
    @Fitable(id = "cef3e1815acb453d8351e304d75cb949")
    public void resumeFlow(String flowDefinitionId, String instanceId, Map<String, Object> request,
            OperationContext context) {
        this.singleFlowRuntimeService.resumeFlow(flowDefinitionId, instanceId, request,
                AippFlowDefinitionServiceImpl.convertOperationContext(context));
    }

    @Override
    @Fitable(id = "w1r6cox2mg0zhqbk1q6yzljxufis1uw3")
    public void terminateFlows(String flowDefinitionId, String instanceId, Map<String, Object> filter,
            OperationContext operationContext) {
        this.singleFlowRuntimeService.terminateFlows(flowDefinitionId, instanceId, filter,
                AippFlowDefinitionServiceImpl.convertOperationContext(operationContext));
    }

    @Override
    @Fitable(id = "0de50d7b726f49818d09678098699260")
    public void resumeAsyncJob(String flowDataId, Map<String, Object> businessData,
            OperationContext operationContext) {
        this.singleFlowRuntimeService.resumeAsyncJob(flowDataId, businessData,
                AippFlowDefinitionServiceImpl.convertOperationContext(operationContext));
    }

    @Override
    @Fitable(id = "66f699f095504dde85e8e8c96bf446f1")
    public void failAsyncJob(String flowDataId, JoberErrorInfo errorInfo, OperationContext operationContext) {
        this.singleFlowRuntimeService.failAsyncJob(flowDataId, errorInfo,
                AippFlowDefinitionServiceImpl.convertOperationContext(operationContext));
    }
}
