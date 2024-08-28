/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.biz.service.scheduletasks;

import static com.huawei.fit.jober.common.ErrorCodes.FLOW_START_ERROR;

import com.huawei.fit.jober.common.exceptions.JobberParamException;

import modelengine.fit.waterflow.flowsengine.biz.service.TraceOwnerService;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrace;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo.DefaultFlowDefinitionRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowDefinitionStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.From;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.IdGenerator;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Node;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 流程实例状态计算定时器
 * 以flow trace为粒度
 *
 * @author 杨祥宇
 * @since 2024/2/20
 */
@Component
public class RestartContextSchedule {
    private static final Logger log = Logger.get(RestartContextSchedule.class);

    private static final List<String> APPLICATIONS = new ArrayList<>();

    static {
        APPLICATIONS.add("a3000");
    }

    private final FlowTraceRepo traceRepo;

    private final FlowContextPersistRepo contextPersistRepo;

    private final DefaultFlowDefinitionRepo flowDefinitionRepo;

    private final FlowLocks locks;

    private final FlowContextPersistMessenger messenger;

    private final TraceOwnerService traceOwnerService;

    public RestartContextSchedule(FlowTraceRepo traceRepo, FlowContextPersistRepo contextPersistRepo,
            DefaultFlowDefinitionRepo flowDefinitionRepo, FlowLocks locks,
            FlowContextPersistMessenger messenger, TraceOwnerService traceOwnerService) {
        this.traceRepo = traceRepo;
        this.contextPersistRepo = contextPersistRepo;
        this.flowDefinitionRepo = flowDefinitionRepo;
        this.locks = locks;
        this.messenger = messenger;
        this.traceOwnerService = traceOwnerService;
    }

    /**
     * 重启中断的context
     */
    @Scheduled(strategy = Scheduled.Strategy.FIXED_RATE, value = "60000")
    public void restartInterruptContext() {
        try {
            List<String> traceIds = traceRepo.findRunningTrace(APPLICATIONS);
            log.info("restartInterruptContext. traceIds:{}.", String.join(",", traceIds));
            restartContext(traceIds);
        } catch (Throwable e) {
            log.error("[restartInterruptContext] exception, errorMessage={}.", e.getMessage());
            log.error("[restartInterruptContext] exception=", e);
        }
    }

    private void restartContext(List<String> traceIds) {
        for (String traceId : traceIds) {
            try {
                if (this.traceOwnerService.isOwn(traceId)) {
                    continue;
                }
                if (!this.traceOwnerService.tryOwn(traceId, null)) {
                    continue;
                }
                log.warn("restartInterruptContext take over trace:{}", traceId);
                Optional<FlowTrace> flowTrace = Optional.ofNullable(traceRepo.find(traceId));
                if (!flowTrace.isPresent()) {
                    traceOwnerService.release(traceId);
                    continue;
                }
                String streamId = flowTrace.get().getStreamId();
                Optional<FlowDefinition> flowDefinitionOptional = Optional.ofNullable(
                        flowDefinitionRepo.findByStreamId(streamId));
                if (!flowDefinitionOptional.isPresent()) {
                    traceOwnerService.release(traceId);
                    continue;
                }
                FlowDefinition flowDefinition = flowDefinitionOptional.get();
                if (flowDefinition.getStatus() == FlowDefinitionStatus.INACTIVE) {
                    throw new JobberParamException(FLOW_START_ERROR);
                }
                From<FlowData> flow = (From<FlowData>) flowDefinition.convertToFlow(contextPersistRepo, messenger,
                        this.locks);
                List<List<FlowContext<FlowData>>> restartContexts =
                        new ArrayList<>(contextPersistRepo.getRunningContextsByTraceId(traceId)
                                .stream()
                                .collect(Collectors.groupingBy(context -> context.getPosition() + context.getStatus()))
                                .values());
                restartContext(flow, restartContexts, flowDefinition);
            } catch (Throwable e) {
                log.error("Restart flow error, trace id: {}, errorMessage: {}.", traceId, e.getMessage());
                log.error("Exception=", e);
            }
        }
    }

    private void restartContext(From<FlowData> flow, List<List<FlowContext<FlowData>>> restartContexts,
            FlowDefinition flowDefinition) {
        restartContexts.forEach(contexts -> {
            FlowContext<FlowData> context = contexts.get(0);
            if (Objects.equals(context.getStatus(), FlowNodeStatus.NEW)) {
                // 节点上new状态context
                FlowNode flowNode = flowDefinition.getFlowNode(context.getPosition());
                if (flowNode.belongTo(FlowNodeType.START)) {
                    flow.offer(contexts);
                    return;
                }
                Node<FlowData, FlowData> node = flow.findNodeFromFlow(flow, context.getPosition());
                node.offer(contexts);
            } else if (Objects.equals(context.getStatus(), FlowNodeStatus.PENDING) && !flowDefinition.getNodeMap()
                    .containsKey(context.getPosition())) {
                // 线上pending状态context
                FlowNode flowNode = flowDefinition.getFromNodeByEvent(context.getPosition());
                if (flowNode.belongTo(FlowNodeType.START)) {
                    flow.offer(contexts);
                    return;
                }
                Node<FlowData, FlowData> node = flow.findNodeFromFlow(flow, flowNode.getMetaId());
                node.offer(contexts);
            } else if (Objects.equals(context.getStatus(), FlowNodeStatus.READY)) {
                // 节点上ready状态context加到重试列表
                List<List<FlowContext<FlowData>>> batchContextLists = new ArrayList<>(contexts.stream().collect(
                        Collectors.groupingBy(FlowContext::getToBatch)).values());
                batchContextLists.forEach(batchContexts -> {
                    List<String> contextIds = batchContexts.stream().map(IdGenerator::getId)
                            .collect(Collectors.toList());
                    contextPersistRepo.updateStatus(contextIds, FlowNodeStatus.RETRYABLE);
                    contextPersistRepo.saveRetrySchedule(batchContexts);
                });
            } else {
                log.warn("Unknown context status, status={}, traceId={}, position={}.", context.getStatus(),
                        context.getTraceId(), context.getPosition());
            }
        });
    }
}
