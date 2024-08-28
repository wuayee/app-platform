/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package modelengine.fit.waterflow.appfactory.fitable;

import static com.huawei.fit.jober.common.ErrorCodes.FLOW_EXECUTE_ASYNC_JOBER_FAILED;
import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;
import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static modelengine.fit.waterflow.common.Constant.SYSTEM_PARAMETER_NODE_KEY;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.FlowInstanceService;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.common.util.ParamUtils;
import com.huawei.fit.jober.entity.FlowInstanceResult;
import com.huawei.fit.jober.entity.FlowStartParameter;
import com.huawei.fit.jober.entity.JoberErrorInfo;
import modelengine.fit.waterflow.flowsengine.biz.service.FlowContextsService;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrace;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowContextPO;
import modelengine.fit.waterflow.flowsengine.utils.FlowUtil;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link FlowInstanceService}实现类
 *
 * @author 杨祥宇
 * @since 2023/12/12
 */
@Component
public class FlowInstanceServiceImpl implements FlowInstanceService {
    private static final Logger log = Logger.get(FlowInstanceServiceImpl.class);

    private final FlowContextsService flowContextsService;

    private final FlowTraceRepo traceRepo;

    private final QueryFlowContextPersistRepo contextRepo;

    private final FlowContextRepo<FlowData> repo;

    public FlowInstanceServiceImpl(FlowContextsService flowContextsService, FlowTraceRepo traceRepo,
            QueryFlowContextPersistRepo contextRepo, FlowContextRepo<FlowData> repo) {
        this.flowContextsService = flowContextsService;
        this.traceRepo = traceRepo;
        this.contextRepo = contextRepo;
        this.repo = repo;
    }

    @Override
    @Fitable(id = "378a7970e68a4ee497da8855361ef2f5")
    public FlowInstanceResult startFlow(String flowDefinitionId, FlowStartParameter flowStartParameter,
            OperationContext context) {
        Validation.notBlank(flowDefinitionId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowId"));
        FlowData data = FlowData.builder()
                .operator(flowStartParameter.getOperator())
                .businessData(flowStartParameter.getBusinessData())
                .startTime(LocalDateTime.now())
                .build();
        FlowUtil.cacheResultToNode(data.getBusinessData(), SYSTEM_PARAMETER_NODE_KEY);
        String traceId = flowContextsService.startFlows(flowDefinitionId, data,
                ParamUtils.convertToInternalOperationContext(context)).getTraceId();
        return new FlowInstanceResult(traceId);
    }

    @Override
    @Fitable(id = "cef3e1815acb453d8351e304d75cb949")
    public void resumeFlow(String flowDefinitionId, String traceId, Map<String, Object> request,
            OperationContext context) {
        FlowContextPO flowContextPo = findContext(flowDefinitionId, traceId, FlowNodeStatus.PENDING);
        Map<String, Map<String, Object>> resumeContext = new HashMap<>();
        resumeContext.put(flowContextPo.getContextId(), request);
        flowContextsService.resumeFlows(flowDefinitionId, resumeContext);
    }

    private FlowContextPO findContext(String flowDefinitionId, String traceId, FlowNodeStatus pending) {
        Validation.notBlank(flowDefinitionId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowDefinitionId"));
        Validation.notBlank(traceId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "traceId"));
        FlowTrace flowTrace = traceRepo.find(traceId);
        if (flowTrace == null) {
            log.error("Flow trace is null.");
            throw new JobberException(INPUT_PARAM_IS_INVALID, traceId);
        }
        List<String> contextIds = new ArrayList<>(flowTrace.getContextPool());
        List<FlowContextPO> pendingContexts = contextRepo.findByContextIdList(contextIds)
                .stream()
                .filter(c -> pending.name().equals(c.getStatus()))
                .collect(Collectors.toList());
        if (pendingContexts.size() != 1) {
            log.error("Flow resume failed, pending context size:{}", pendingContexts.size());
            throw new JobberException(INPUT_PARAM_IS_INVALID, traceId);
        }
        return pendingContexts.get(0);
    }

    @Override
    @Fitable(id = "w1r6cox2mg0zhqbk1q6yzljxufis1uw3")
    public void terminateFlows(String flowDefinitionId, String traceId, Map<String, Object> filter,
            OperationContext operationContext) {
        flowContextsService.terminateFlows(traceId, filter,
                ParamUtils.convertToInternalOperationContext(operationContext));
    }

    @Override
    public void resumeAsyncJob(String flowDefinitionId, String traceId, Map<String, Object> newBusinessData,
            OperationContext operationContext) {
        log.info("resumeAsyncJob flowDefinitionId:{0}, traceId:{1}", flowDefinitionId, traceId);
        List<FlowContext<FlowData>> contexts = repo.getContextsByTrace(traceId, FlowNodeStatus.PROCESSING.name());
        if (contexts.size() != 1) {
            log.warn("resumeAsyncJob processing context size != 1. traceId:{0}, size:{1}, contextIds:{2}", traceId,
                    contexts.size(), contexts.stream().map(FlowContext::getId).collect(Collectors.joining(",")));
            return;
        }
        flowContextsService.resumeAsyncJob(contexts, Collections.singletonList(newBusinessData),
                ParamUtils.convertToInternalOperationContext(operationContext));
    }

    @Override
    @Fitable(id = "66f699f095504dde85e8e8c96bf446f1")
    public void failAsyncJob(String flowDefinitionId, String traceId, JoberErrorInfo errorInfo,
            OperationContext operationContext) {
        log.info("failAsyncJob flowDefinitionId={}, traceId={}, errorInfo={}.", flowDefinitionId, traceId, errorInfo);
        List<FlowContext<FlowData>> contexts = repo.getContextsByTrace(traceId, FlowNodeStatus.PROCESSING.name());
        if (contexts.size() != 1) {
            log.warn("failAsyncJob processing context size != 1. traceId={}, size={}, contextIds={}", traceId,
                    contexts.size(), contexts.stream().map(FlowContext::getId).collect(Collectors.joining(",")));
            return;
        }
        flowContextsService.failAsyncJob(contexts,
                new JobberException(FLOW_EXECUTE_ASYNC_JOBER_FAILED, errorInfo.getMessage()),
                ParamUtils.convertToInternalOperationContext(operationContext));
    }
}
