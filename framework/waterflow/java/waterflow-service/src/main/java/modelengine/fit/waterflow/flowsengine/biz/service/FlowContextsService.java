/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.biz.service;

import static com.huawei.fit.jober.common.ErrorCodes.ENTITY_NOT_FOUND;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_NODE_MAX_TASK;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_NODE_NOT_FOUND;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_RETRY_JOBER_UPDATE_DATABASE_FAILED;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_START_ERROR;
import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static modelengine.fit.waterflow.common.Constant.BUSINESS_DATA_KEY;
import static modelengine.fit.waterflow.common.Constant.OPERATOR_KEY;
import static modelengine.fit.waterflow.common.Constant.STREAM_ID_SEPARATOR;
import static modelengine.fit.waterflow.common.Constant.TO_BATCH_KEY;
import static modelengine.fit.waterflow.common.Constant.TRACE_EXCLUSIVE_STATUS_MAP;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowDefinitionStatus.INACTIVE;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.READY;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.RETRYABLE;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.ProcessType.PROCESS;
import static modelengine.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.entity.FlowNodePublishInfo;
import com.huawei.fit.jober.entity.FlowPublishContext;
import com.huawei.fit.jober.entity.consts.NodeTypes;

import modelengine.fit.waterflow.common.utils.SleepUtil;
import modelengine.fit.waterflow.common.utils.UUIDUtil;
import modelengine.fit.waterflow.flowsengine.biz.service.entity.FlowRetryInfo;
import modelengine.fit.waterflow.flowsengine.biz.service.entity.FlowTransCompletionInfo;
import modelengine.fit.waterflow.flowsengine.biz.service.entity.FlowsErrorInfo;
import modelengine.fit.waterflow.flowsengine.domain.flows.InterStream;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowOfferId;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowRetry;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrace;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrans;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.callbacks.FlowEventCallback;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.events.FlowEvent;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowTraceStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.From;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.IdGenerator;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.To;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Blocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Node;
import modelengine.fit.waterflow.flowsengine.domain.flows.utils.FlowExecuteInfoUtil;
import modelengine.fit.waterflow.flowsengine.fitable.TraceServiceImpl;
import modelengine.fit.waterflow.flowsengine.persist.po.FlowContextPO;
import modelengine.fit.waterflow.flowsengine.utils.FlowUtil;
import modelengine.fit.waterflow.flowsengine.utils.WaterFlows;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.annotation.Scheduled;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 流程实例相关服务
 *
 * @author 杨祥宇
 * @since 2023/9/1
 */
@Component
public class FlowContextsService {
    private static final Logger LOG = Logger.get(FlowContextsService.class);

    /**
     * 重试自动任务的等待间隔时间，防止同一耗时重试对象在短时间内被多次尝试重试
     */
    private static final long RETRY_WAITING_INTERVAL = 15000L;

    /**
     * 重试任务周期
     */
    private static final long RETRY_INTERVAL = 30000L;

    private final FlowDefinitionRepo definitionRepo;

    private final FlowContextRepo repo;

    private final FlowContextMessenger messenger;

    private final QueryFlowContextPersistRepo queryRepo;

    private final FlowTraceRepo traceRepo;

    private final FlowRetryRepo retryRepo;

    private final FlowLocks locks;

    private final TraceOwnerService traceOwnerService;

    private final List<FlowEventCallback> consumers;

    private final TraceServiceImpl traceService;

    private final Map<String, FlowRetryInfo> flowRetryMap = new HashMap<>();

    private volatile boolean isRetryRunning = false;

    public FlowContextsService(FlowDefinitionRepo definitionRepo,
            @Fit(alias = "flowContextPersistRepo") FlowContextRepo repo,
            @Fit(alias = "flowContextPersistMessenger") FlowContextMessenger messenger,
            QueryFlowContextPersistRepo queryRepo, FlowTraceRepo traceRepo, FlowRetryRepo retryRepo, FlowLocks locks,
            TraceOwnerService traceOwnerService, @Fit List<FlowEventCallback> consumers,
            TraceServiceImpl traceService) {
        this.definitionRepo = definitionRepo;
        this.repo = repo;
        this.messenger = messenger;
        this.queryRepo = queryRepo;
        this.traceRepo = traceRepo;
        this.retryRepo = retryRepo;
        this.locks = locks;
        this.traceOwnerService = traceOwnerService;
        this.consumers = consumers;
        this.traceService = traceService;
    }

    private static boolean isContextRunning(FlowContextPO flowContextPO) {
        FlowNodeStatus contextStatus = FlowNodeStatus.valueOf(flowContextPO.getStatus());
        return FlowNodeStatus.NEW.equals(contextStatus) || FlowNodeStatus.PENDING.equals(contextStatus)
                || FlowNodeStatus.READY.equals(contextStatus) || FlowNodeStatus.RETRYABLE.equals(contextStatus);
    }

    /**
     * 启动流程实例
     *
     * @param flowId 流程定义UUID标识 {@link String}
     * @param flowData 流程启动数据 {@link String}
     * @param operationContext 表示操作上下文的 {@link OperationContext}
     * @return 流程实例相关标识
     */
    public FlowOfferId startFlows(String flowId, String flowData, OperationContext operationContext) {
        return startFlows(flowId, FlowData.parseFromJson(flowData), operationContext);
    }

    /**
     * 启动流程实例
     *
     * @param flowId 流程定义UUID标识 {@link String}
     * @param flowData flowData 流程启动数据 {@link FlowData}
     * @param operationContext 表示操作上下文的 {@link OperationContext}
     * @return 流程实例相关标识
     */
    public FlowOfferId startFlows(String flowId, FlowData flowData, OperationContext operationContext) {
        Map<String, Object> contextData = ObjectUtils.cast(
                Optional.ofNullable(flowData.getContextData()).orElse(new HashMap<>()));
        flowData.setContextData(contextData);
        FlowDefinition flowDefinition = Optional.ofNullable(definitionRepo.find(flowId))
                .orElseThrow(() -> new JobberParamException(ENTITY_NOT_FOUND, "startFlows", flowId));
        if (flowDefinition.getStatus() == INACTIVE) {
            throw new JobberParamException(FLOW_START_ERROR);
        }
        From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(repo, messenger, this.locks);
        if (flowDefinition.isEnableOutputScope()) {
            FlowUtil.cacheResultToNode(flowData.getBusinessData(), from.getId());
        }
        contextData.put("flowDefinitionId", flowId);
        contextData.put("nodeType", FlowNodeType.START.getCode());
        FlowOfferId offerId = from.offer(flowData);
        this.publishStartNodeData(flowId, offerId.getTraceId(), from.getId(), flowData);
        return offerId;
    }

    /**
     * 启动流程实例
     *
     * @param metaId 流程定义ID标识 {@link String}
     * @param version 流程定义版本
     * @param flowData 流程启动数据 {@link String}
     * @return String 流程实例transId标识
     */
    public FlowOfferId startFlows(String metaId, String version, String flowData) {
        FlowDefinition flowDefinition = Optional.ofNullable(definitionRepo.findByMetaIdAndVersion(metaId, version))
                .orElseThrow(() -> new JobberParamException(ENTITY_NOT_FOUND, "startFlows", metaId, version));
        if (flowDefinition.getStatus() == INACTIVE) {
            throw new JobberParamException(FLOW_START_ERROR);
        }
        From<FlowData> from = ObjectUtils.cast(flowDefinition.convertToFlow(repo, messenger, this.locks));
        return from.offer(FlowData.parseFromJson(flowData));
    }

    /**
     * 在某个trans下启动流程实例
     *
     * @param metaId 流程定义ID标识 {@link String}
     * @param version 流程定义版本
     * @param transId transId
     * @param flowData 流程启动数据 {@link String}
     * @return String 流程实例transId标识
     */
    public FlowOfferId startFlowsWithTrans(String metaId, String version, String transId, String flowData) {
        FlowDefinition flowDefinition = Optional.ofNullable(definitionRepo.findByMetaIdAndVersion(metaId, version))
                .orElseThrow(() -> new JobberParamException(ENTITY_NOT_FOUND, "startFlowsWithTrans", metaId, version));
        if (flowDefinition.getStatus() == INACTIVE) {
            throw new JobberParamException(FLOW_START_ERROR);
        }
        From<FlowData> from = ObjectUtils.cast(flowDefinition.convertToFlow(repo, messenger, this.locks));
        return from.offer(FlowData.parseFromJson(flowData), new FlowTrans(transId));
    }

    /**
     * 删除流程实例所有信息
     *
     * @param transId 流程实例trans id标识
     */
    @Transactional
    public void deleteFlow(String transId) {
        List<String> traceIds = repo.getTraceByTransId(transId);
        repo.deleteByTransId(transId);
        traceRepo.deleteByIdList(traceIds);
    }

    /**
     * 对流程的指定节点关联数据源
     *
     * @param metaId 流程metaId标识
     * @param version 流程定义版本
     * @param nodeMetaId 流程中节点的metaId
     * @param publisher 数据源
     */
    public void offerFlowNode(String metaId, String version, String nodeMetaId, InterStream<FlowData> publisher) {
        FlowDefinition flowDefinition = Optional.ofNullable(definitionRepo.findByMetaIdAndVersion(metaId, version))
                .orElseThrow(() -> new JobberParamException(ENTITY_NOT_FOUND, "offerFlowNode", metaId, version));
        if (flowDefinition.getStatus() == INACTIVE) {
            throw new JobberParamException(FLOW_START_ERROR);
        }
        From<FlowData> from = ObjectUtils.cast(flowDefinition.convertToFlow(repo, messenger, this.locks));
        Node<FlowData, ?> node = from.findNodeFromFlow(from, nodeMetaId);
        Validation.notNull(node, () -> new JobberParamException(FLOW_NODE_NOT_FOUND, nodeMetaId, metaId, version));
        node.offer(publisher);
    }

    /**
     * 人工任务恢复流程执行
     * 参数外层map的key为contextId，value为有更新的业务数据map
     * 内存map的key为businessData和operator，后续可以补充passData
     *
     * @param flowId 流程定义UUID
     * @param contexts 变更的上下文业务数据结合
     */
    public void resumeFlows(String flowId, Map<String, Map<String, Object>> contexts) {
        if (MapUtils.isEmpty(contexts)) {
            return;
        }

        FlowDefinition flowDefinition = Optional.ofNullable(definitionRepo.find(flowId))
                .orElseThrow(() -> new JobberParamException(ENTITY_NOT_FOUND, "resumeFlows", flowId));
        if (flowDefinition.getStatus() == INACTIVE) {
            throw new JobberParamException(FLOW_START_ERROR);
        }

        Set<String> ids = contexts.keySet();
        List<FlowContext<FlowData>> exits = repo.getPendingAndSentByIds(new ArrayList(ids));
        if (CollectionUtils.isEmpty(exits)) {
            LOG.error("[resumeFlows] invalid context ids: {}", ids.toString());
            return;
        }
        String streamId = exits.get(0).getStreamId();
        String position = exits.get(0).getPosition();
        LOG.info("Start resume water flow, flow uuid: {}, stream id: {}, context ids: {}, position: {}", flowId,
                streamId, ids.toString(), position);
        String toBatch = UUIDUtil.uuid();
        From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(repo, messenger, locks);
        Blocks.Block<FlowData> block = from.getBlock(position);
        boolean enableOutputScope = flowDefinition.isEnableOutputScope();
        exits.forEach(c -> {
            Map<String, Object> changedValues = contexts.get(c.getId());
            if (enableOutputScope) {
                FlowUtil.cacheResultToNode(cast(changedValues.get(BUSINESS_DATA_KEY)), block.getTarget().getId());
            }
            c.getData().setOperator(String.valueOf(changedValues.get(OPERATOR_KEY)));
            c.getData()
                    .setBusinessData(FlowUtil.mergeMaps(c.getData().getBusinessData(),
                            ObjectUtils.cast(changedValues.get(BUSINESS_DATA_KEY))));
            c.toBatch(toBatch);
        });
        repo.updateFlowData(exits);
        block.process(exits);
        LOG.info("Resume water flow success, flow uuid: {}, stream id:{}, context ids: {}, position: {}", flowId,
                streamId, ids.toString(), position);
    }

    /**
     * 查询开始节点的context信息
     *
     * @param metaId 流程metaId标识 {@link String}
     * @param version 流程对应的版本 {@link String}
     * @param operationContext 表示操作上下文的 {@link OperationContext}
     * @return 开始节点的context列表
     */
    public List<FlowContextPO> findStartNodeContexts(String metaId, String version, OperationContext operationContext) {
        return new ArrayList<>();
    }

    /**
     * 查询当前节点的context列表
     *
     * @param metaId 流程metaId标识 {@link String}
     * @param version version 流程对应的版本 {@link String}
     * @param nodeId 节点id标识 {@link String}
     * @param operationContext 表示操作上下文的 {@link OperationContext}
     * @return context列表
     */
    public List<FlowContextPO> findNodeContexts(String metaId, String version, String nodeId,
            OperationContext operationContext) {
        return new ArrayList<>();
    }

    /**
     * 查询流程实例状态统计视图
     *
     * @param metaId 流程metaId标识 {@link String}
     * @param version version 流程对应的版本 {@link String}
     * @param operationContext 表示操作上下文的 {@link OperationContext}
     * @return context列表
     */
    public List<FlowContext<FlowData>> findContextStatusViewCount(String metaId, String version,
            OperationContext operationContext) {
        return repo.findByStreamId(metaId, version);
    }

    /**
     * 根据streamId查询运行中、错误、已完成的context
     *
     * @param streamIds 流程定义streamId标识列表
     * @param operationContext 表示操作上下文的 {@link OperationContext}
     * @return 运行中、错误、已完成的context的列表
     */
    public List<Map<String, Object>> findContextStatusViewByStreamIds(List<String> streamIds,
            OperationContext operationContext) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<FlowContextPO> flowContextPOS = queryRepo.findByStreamIdList(streamIds);
        Map<String, List<FlowContextPO>> flowContextMap = flowContextPOS.stream()
                .collect(Collectors.groupingBy(FlowContextPO::getStreamId));
        flowContextMap.values().forEach(list -> {
            List<FlowContextPO> start = list.stream()
                    .filter(c -> c.getPositionId().equals(c.getRootId()))
                    .collect(Collectors.toList());
            List<FlowContextPO> running = list.stream()
                    .filter(c -> !(FlowNodeStatus.ERROR.equals(FlowNodeStatus.valueOf(c.getStatus()))
                            || FlowNodeStatus.ARCHIVED.equals(FlowNodeStatus.valueOf(c.getStatus()))))
                    .collect(Collectors.toList());
            List<FlowContextPO> error = list.stream()
                    .filter(c -> FlowNodeStatus.ERROR.equals(FlowNodeStatus.valueOf(c.getStatus())))
                    .collect(Collectors.toList());
            Map<String, List<FlowContextPO>> flowContexts = new HashMap<>();
            flowContexts.put("start", start);
            flowContexts.put("running", running);
            flowContexts.put("error", error);
            Map<String, Object> view = flowContextCountViewOf(flowContexts);
            if (!view.isEmpty()) {
                result.add(view);
            }
        });
        return result;
    }

    /**
     * 转换flowContext的view数据
     *
     * @param flowContexts context
     * @return Map<String, Object>
     */
    public static Map<String, Object> flowContextCountViewOf(Map<String, List<FlowContextPO>> flowContexts) {
        Map<String, Object> view = new LinkedHashMap<>(4);
        if (flowContexts.isEmpty() || flowContexts.get("start").isEmpty()) {
            return view;
        }
        put(view, "allContexts", flowContexts.get("start").size());
        put(view, "runningContexts", flowContexts.get("running").size());
        put(view, "errorContexts", flowContexts.get("error").size());
        put(view, "streamId", flowContexts.get("start").get(0).getStreamId());
        return view;
    }

    private static void put(Map<String, Object> view, String key, Object value) {
        if (value != null) {
            view.put(key, value);
        }
    }

    /**
     * 根据流程实例traceId查询所有contexts
     *
     * @param traceId 流程实例traceId
     * @param operationContext 表示操作上下文的 {@link OperationContext}
     * @return 流程实例traceId对应的contexts
     */
    public List<FlowContext<String>> findContextByTraceId(String traceId, OperationContext operationContext) {
        return repo.getContextsByTrace(traceId);
    }

    /**
     * getFlowCompleteness
     *
     * @param traceIds traceIds
     * @param flag flag
     * @param operationContext operationContext
     * @return Map<String, Map < String, Object>>
     */
    public Map<String, Map<String, Object>> getFlowCompleteness(List<String> traceIds, boolean flag,
            OperationContext operationContext) {
        if (traceIds.size() == 0) {
            return new HashMap<>();
        }
        // 根据traceId找到所有的contextIds
        List<FlowTrace> flowTraces = traceRepo.findTraceByIdList(traceIds);
        List<String> streamIds = flowTraces.stream().map(FlowTrace::getStreamId).collect(Collectors.toList());

        List<String> contextIds = flowTraces.stream()
                .flatMap(trace -> trace.getContextPool().stream())
                .distinct()
                .collect(Collectors.toList());
        if (contextIds.isEmpty()) {
            return new HashMap<>();
        }

        List<FlowContextPO> flowContextPOList = queryRepo.findWithFlowDataByContextIdList(new ArrayList<>(contextIds));
        Map<String, FlowDefinition> flowDefinitionMap = definitionRepo.findByStreamIdList(streamIds)
                .stream()
                .collect(Collectors.toMap(FlowDefinition::getStreamId, Function.identity()));

        Map<String, Map<String, Object>> result = new HashMap<>();
        for (FlowTrace trace : flowTraces) {
            List<FlowContextPO> contexts = flowContextPOList.stream()
                    .filter(c -> trace.getContextPool().contains(c.getContextId()))
                    .collect(Collectors.toList());
            FlowDefinition flowDefinition = flowDefinitionMap.get(trace.getStreamId());

            result.put(trace.getId(), calculateFlowProgressFromContext(contexts, flowDefinition, flag));
        }
        return result;
    }

    /**
     * getFlowCompletenessByTransId
     *
     * @param transId transId
     * @param newStatus newStatus
     * @return Map<String, Object>
     */
    public Map<String, Object> getFlowCompletenessByTransId(String transId, boolean newStatus) {
        List<FlowContextPO> contexts = queryRepo.findByTransIdList(Collections.singletonList(transId));
        Validation.notEmpty(contexts, () -> new JobberParamException(INPUT_PARAM_IS_INVALID, transId));

        String streamId = contexts.get(0).getStreamId();
        Map<String, FlowDefinition> flowDefinitionMap = definitionRepo.findByStreamIdList(
                        Collections.singletonList(streamId))
                .stream()
                .collect(Collectors.toMap(FlowDefinition::getStreamId, Function.identity()));
        FlowDefinition flowDefinition = flowDefinitionMap.get(streamId);
        Validation.notNull(flowDefinition, () -> new JobberParamException(INPUT_PARAM_IS_INVALID, transId));

        return calculateFlowProgressFromContext(contexts, flowDefinition, newStatus);
    }

    private Map<String, Object> calculateFlowProgressFromContext(List<FlowContextPO> contexts,
            FlowDefinition flowDefinition, boolean newStatus) {
        double percentage;
        String status = newStatus ? getStatusNew(contexts, flowDefinition) : getStatus(contexts);
        if (FlowTraceStatus.ARCHIVED.name().equals(status)) {
            percentage = 100.00D;
        } else {
            if (flowDefinition != null) {
                percentage = getPercentage(flowDefinition, contexts) * 100.00D;
            } else {
                percentage = 0D;
                LOG.warn("Failed to get flow completeness. flowDefinition not found.");
            }
        }

        Map<String, Object> flowProgress = new HashMap<>();
        List<String> errorNodes = new ArrayList<>();
        if (FlowTraceStatus.ERROR.name().equals(status)) {
            errorNodes = getErrorNode(flowDefinition, contexts);
        }
        flowProgress.put("errorNode", errorNodes);
        flowProgress.put("status", status);
        flowProgress.put("percentage", String.format(Locale.ROOT, "%.2f", percentage));
        return flowProgress;
    }

    /**
     * 获取流程错误信息
     *
     * @param traceId 流程实例traceId
     * @return 错误信息列表
     */
    public List<FlowsErrorInfo> getFlowErrorInfo(String traceId) {
        List<FlowContext<FlowData>> errorContexts = repo.findErrorContextsByTraceId(traceId);
        FlowTrace flowTrace = Optional.ofNullable(traceRepo.find(traceId))
                .orElseThrow(() -> new JobberParamException(ENTITY_NOT_FOUND, traceId));
        FlowDefinition flowDefinition = definitionRepo.findByStreamId(flowTrace.getStreamId());
        if (flowDefinition == null) {
            LOG.error("Get flow error info failed, flow definition not found");
            throw new JobberParamException(ENTITY_NOT_FOUND, traceId);
        }
        return errorContexts.stream().map(c -> {
            FlowNode flowNode = flowDefinition.getNodeMap().get(c.getPosition());
            String nodeName = null;
            if (flowNode == null) {
                nodeName = flowDefinition.getToNodeByEventId(c.getPosition()).getName();
            } else {
                nodeName = flowNode.getName();
            }
            return FlowsErrorInfo.builder()
                    .businessData(c.getData().getBusinessData())
                    .contextErrorInfo(c.getData().getErrorInfo())
                    .nodeId(c.getPosition())
                    .nodeName(nodeName)
                    .build();
        }).collect(Collectors.toList());
    }

    private void publishStartNodeData(String flowDefinitionId, String traceId, String nodeId, FlowData flowData) {
        Map<String, Object> businessData = flowData.getBusinessData();
        Map<String, Object> inputData = ObjectUtils.cast(
                businessData.getOrDefault("startNodeInputParams", new HashMap<String, Object>()));
        if (!inputData.isEmpty()) {
            FlowExecuteInfoUtil.addInputMap2ExecuteInfoMap(flowData, inputData, nodeId, "start");
            FlowExecuteInfoUtil.addOutputMap2ExecuteInfoMap(flowData, inputData, nodeId, "start");
        }
        LocalDateTime now = LocalDateTime.now();
        FlowPublishContext startContext = new FlowPublishContext(traceId, FlowNodeStatus.ARCHIVED.name(), now, now,
                now);
        FlowNodePublishInfo flowNodePublishInfo = new FlowNodePublishInfo();
        flowNodePublishInfo.setFlowDefinitionId(flowDefinitionId);
        flowNodePublishInfo.setNodeId(nodeId);
        flowNodePublishInfo.setNodeType(NodeTypes.START.getType());
        flowNodePublishInfo.setBusinessData(businessData);
        flowNodePublishInfo.setFlowContext(startContext);
        flowNodePublishInfo.setErrorMsg(StringUtils.EMPTY);
        traceService.publishNodeInfo(flowNodePublishInfo);
    }

    private List<String> getErrorNode(FlowDefinition flowDefinition, List<FlowContextPO> contexts) {
        if (flowDefinition == null) {
            return new ArrayList<>();
        }
        List<String> errorContextIds = contexts.stream()
                .filter(c -> FlowNodeStatus.ERROR.name().equals(c.getStatus()))
                .map(FlowContextPO::getPositionId)
                .distinct()
                .collect(Collectors.toList());
        return errorContextIds.stream().map(id -> {
            if (flowDefinition.getNodeMap().containsKey(id)) {
                return flowDefinition.getNodeMap().get(id).getName();
            }
            return flowDefinition.getToNodeByEventId(id).getName();
        }).collect(Collectors.toList());
    }

    private Double getPercentage(FlowDefinition flowDefinition, List<FlowContextPO> flowContextPOList) {
        Set<String> visitNodeIds = new HashSet<>();
        double percentage = 0.0d;
        double commonWeight = (double) 1 / flowDefinition.getNodeMap().size();

        FlowNode startNode = flowDefinition.getFlowNode(FlowNodeType.START);
        Map<String, Set<FlowEvent>> fromEvents = flowDefinition.getFromEvents();
        ArrayDeque<FlowNode> queue = new ArrayDeque<>();
        queue.addLast(startNode);

        double preArchiveCount = 1.0d;
        while (!queue.isEmpty()) {
            int size = queue.size();
            Set<FlowNode> nodes = new HashSet<>(); // 找到这一层所有的不重复节点
            for (int i = 0; i < size; i++) {
                FlowNode node = queue.removeFirst();
                nodes.add(node);
                List<FlowNode> toNodes = getToNodes(flowDefinition, node); // 找到当前节点的下一个节点放入队列中
                toNodes.stream().filter(toNode -> visitNodeIds.add(toNode.getMetaId())).forEach(queue::addLast);
            }
            // 开始循环nodes 找到所有的context
            List<FlowContextPO> curNodeContexts = new ArrayList<>();
            for (FlowNode node : nodes) {
                curNodeContexts.addAll(getCurNodeContexts(flowContextPOList, node, fromEvents));
            }

            double curArchiveCount = calculate(curNodeContexts);
            double curCompletion = commonWeight * (double) size * curArchiveCount * preArchiveCount;
            preArchiveCount *= curArchiveCount;
            percentage += curCompletion;
        }
        return percentage;
    }

    private List<FlowContextPO> getCurNodeContexts(List<FlowContextPO> contextPos, FlowNode node,
            Map<String, Set<FlowEvent>> fromEvents) {
        List<FlowContextPO> result = new ArrayList<>();
        Set<String> fromIds = Optional.ofNullable(fromEvents.get(node.getMetaId()))
                .map(es -> es.stream().map(FlowEvent::getFrom).collect(Collectors.toSet()))
                .orElse(new HashSet<>());
        Set<String> eventIds = Optional.ofNullable(fromEvents.get(node.getMetaId()))
                .map(es -> es.stream().map(FlowEvent::getMetaId).collect(Collectors.toSet()))
                .orElse(new HashSet<>());

        result.addAll(contextPos.stream()
                .filter(context -> "NEW".equals(context.getStatus()))
                .filter(context -> fromIds.contains(context.getPositionId()))
                .collect(Collectors.toList()));
        result.addAll(contextPos.stream()
                .filter(context -> !"NEW".equals(context.getStatus()))
                .filter(context -> (context.getPositionId().equals(node.getMetaId())) || eventIds.contains(
                        context.getPositionId()))
                .collect(Collectors.toList()));

        return result;
    }

    private List<FlowNode> getToNodes(FlowDefinition flowDefinition, FlowNode node) {
        return node.getEvents()
                .stream()
                .map(flowEvent -> flowDefinition.getToNodeByEventId(flowEvent.getMetaId()))
                .collect(Collectors.toList());
    }

    private double calculate(List<FlowContextPO> curNodeContexts) {
        if (curNodeContexts == null || curNodeContexts.size() == 0) {
            return 0;
        }
        long count = curNodeContexts.stream()
                .filter(c -> ("ARCHIVED".equals(c.getStatus()) || "ERROR".equals(c.getStatus())))
                .count();
        return (double) count / curNodeContexts.size();
    }

    private String getStatus(List<FlowContextPO> flowContextPOList) {
        boolean hasError = flowContextPOList.stream()
                .anyMatch(flowContextPO -> FlowNodeStatus.ERROR.equals(
                        FlowNodeStatus.valueOf(flowContextPO.getStatus())));

        boolean allArchived = flowContextPOList.stream()
                .allMatch(flowContextPO -> FlowNodeStatus.ARCHIVED.equals(
                        FlowNodeStatus.valueOf(flowContextPO.getStatus())));

        boolean hasTerminated = flowContextPOList.stream()
                .anyMatch(flowContextPO -> FlowNodeStatus.TERMINATE.equals(
                        FlowNodeStatus.valueOf(flowContextPO.getStatus())));

        if (hasError) {
            return FlowTraceStatus.ERROR.name();
        } else if (allArchived) {
            return FlowTraceStatus.ARCHIVED.name();
        } else if (hasTerminated) {
            return FlowTraceStatus.TERMINATE.name();
        } else {
            return FlowTraceStatus.RUNNING.name();
        }
    }

    private String getStatusNew(List<FlowContextPO> flowContextPOList, FlowDefinition flowDefinition) {
        boolean isTerminated = flowContextPOList.stream()
                .anyMatch(flowContextPO -> FlowNodeStatus.TERMINATE.equals(
                        FlowNodeStatus.valueOf(flowContextPO.getStatus())));
        boolean isRunning = flowContextPOList.stream().anyMatch(FlowContextsService::isContextRunning);
        boolean isArchived = flowContextPOList.stream()
                .allMatch(flowContextPO -> FlowNodeStatus.ARCHIVED.equals(
                        FlowNodeStatus.valueOf(flowContextPO.getStatus())));
        boolean hasError = flowContextPOList.stream()
                .anyMatch(flowContextPO -> FlowNodeStatus.ERROR.equals(
                        FlowNodeStatus.valueOf(flowContextPO.getStatus())));

        if (isTerminated) {
            return FlowTraceStatus.TERMINATE.name();
        }
        if (isRunning) {
            return FlowTraceStatus.RUNNING.name();
        }
        if (isArchived) {
            return FlowTraceStatus.ARCHIVED.name();
        }
        if (hasError) {
            List<String> metaIds = flowContextPOList.stream()
                    .filter(flowContextPO -> FlowNodeStatus.ARCHIVED.equals(
                            FlowNodeStatus.valueOf(flowContextPO.getStatus())))
                    .map(FlowContextPO::getPositionId)
                    .collect(Collectors.toList());
            for (String id : metaIds) {
                FlowNode flowNode = flowDefinition.getFlowNode(id);
                if (flowNode == null) {
                    LOG.warn("==== definition id: {}. node id: {}", flowDefinition.getDefinitionId(), id);
                    continue;
                }
                FlowNodeType type = flowNode.getType();
                if (StringUtils.equals(type.getCode(), "END")) {
                    return FlowTraceStatus.PARTIAL_ERROR.name();
                }
            }
            return FlowTraceStatus.ERROR.name();
        }
        return FlowTraceStatus.RUNNING.name();
    }

    /**
     * 根据traceId终止流程
     * filter中可以传入与业务相关的过滤条件，停止满足条件的部分context，目前不支持
     *
     * @param traceId traceId
     * @param filter filter
     * @param operationContext operationContext
     */
    public void terminateFlows(String traceId, Map<String, Object> filter, OperationContext operationContext) {
        FlowTrace flowTrace = traceRepo.find(traceId);
        if (Objects.isNull(flowTrace)) {
            LOG.warn("The trace is not exist. traceId={}.", traceId);
            return;
        }
        if (TRACE_EXCLUSIVE_STATUS_MAP.get(FlowTraceStatus.TERMINATE.toString())
                .contains(flowTrace.getStatus().toString())) {
            LOG.warn("The trace cannot be terminated, traceId={}, currentStatus={}, start={}, end={}.",
                    flowTrace.getId(), flowTrace.getStatus().name(), flowTrace.getStartTime(), flowTrace.getEndTime());
            return;
        }

        List<FlowContext<String>> contextList = repo.findWithoutFlowDataByTraceId(traceId);
        Set<String> traceIdSet = contextList.stream()
                .flatMap(context -> context.getTraceId().stream())
                .collect(Collectors.toSet());

        traceRepo.updateStatus(new ArrayList<>(traceIdSet), FlowTraceStatus.TERMINATE.toString());
        repo.updateStatus(contextList, FlowNodeStatus.TERMINATE.toString(), null);
        LOG.info("The trace is terminated, traceId={}, currentStatus={}, start={}.", flowTrace.getId(),
                flowTrace.getStatus().name(), flowTrace.getStartTime());
    }

    /**
     * 恢复异步jober
     *
     * @param pre 之前的context
     * @param newContexts 结果context
     * @param operationContext 操作上下文
     */
    public void resumeAsyncJob(List<FlowContext<FlowData>> pre, List<Map<String, Object>> newContexts,
            OperationContext operationContext) {
        LOG.info("resumeAsyncJob preContextId: {}",
                pre.stream().map(FlowContext::getId).collect(Collectors.joining(",")));
        LocalDateTime currentTime = LocalDateTime.now();
        FlowContext<FlowData> preContext = pre.get(0);
        String operator = Optional.ofNullable(operationContext)
                .flatMap(c -> Optional.ofNullable(c.operator()))
                .orElse(preContext.getData().getOperator());
        FlowDefinition flowDefinition = definitionRepo.findByStreamId(preContext.getStreamId());
        boolean enableOutputScope = flowDefinition.isEnableOutputScope();
        From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(repo, messenger, this.locks);
        Node<FlowData, FlowData> node = from.findNodeFromFlow(from, preContext.getPosition());

        FlowNode currentFlowNode = flowDefinition.getFlowNode(preContext.getPosition());
        List<FlowContext<FlowData>> after = newContexts.stream().map(data -> {
            Map<String, Object> newOutputMap = currentFlowNode.getJober().getConverter().convertOutput(null);
            updateMap(newOutputMap, data);
            if (enableOutputScope) {
                FlowUtil.cacheResultToNode(data, node.getId());
            }
            FlowData flowData = FlowData.builder()
                    .operator(operator)
                    .startTime(currentTime)
                    .businessData(data)
                    .contextData(preContext.getData().getContextData())
                    .passData(Collections.emptyMap())
                    .build();
            FlowExecuteInfoUtil.addOutputMap2ExecuteInfoMap(flowData, newOutputMap, currentFlowNode.getMetaId(),
                    "jober");
            return preContext.generate(flowData, preContext.getPosition(), preContext.getCreateAt());
        }).collect(Collectors.toList());

        LOG.info("resumeAsyncJob afterContextId: {}",
                after.stream().map(FlowContext::getId).collect(Collectors.joining(",")));
        node.afterProcess(pre, after);
    }

    private static void updateMap(Map<String, Object> newOutputMap, Map<String, Object> businessData) {
        for (Map.Entry<String, Object> entry : newOutputMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                updateValueFromBusinessData(newOutputMap, businessData, key);
            } else if (value instanceof Map) {
                updateNestedMap(businessData, key, value);
            }
        }
    }

    private static void updateValueFromBusinessData(Map<String, Object> newOutputMap, Map<String, Object> businessData,
            String key) {
        if (businessData.containsKey(key)) {
            newOutputMap.put(key, businessData.get(key));
        }
    }

    private static void updateNestedMap(Map<String, Object> businessData, String key, Object value) {
        Object businessDataValue = businessData.get(key);
        if (businessDataValue instanceof Map) {
            updateMap((Map<String, Object>) value, (Map<String, Object>) businessDataValue);
        }
    }

    /**
     * 将异步任务设置失败
     *
     * @param pre 对应的context
     * @param exception 异常信息
     * @param operationContext 操作上下文
     */
    public void failAsyncJob(List<FlowContext<FlowData>> pre, JobberException exception,
            OperationContext operationContext) {
        LOG.info("failAsyncJob preContextId={}, exception={}.",
                pre.stream().map(FlowContext::getId).collect(Collectors.joining(",")), exception.getMessage());
        FlowContext<FlowData> preContext = pre.get(0);
        FlowDefinition flowDefinition = definitionRepo.findByStreamId(preContext.getStreamId());
        From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(this.repo, this.messenger, this.locks);
        Node<FlowData, FlowData> node = cast(from.findNodeFromFlow(from, preContext.getPosition()));
        FlowNode flowNode = flowDefinition.getFlowNode(node.getId());
        flowNode.notifyException(exception, pre);
        node.setFailed(pre, exception);
    }

    /**
     * 根据transId终止流程
     *
     * @param transId 流程实例id
     * @param operationContext operationContext
     */
    public void terminateFlowsByTransId(String transId, OperationContext operationContext) {
        List<FlowContextPO> contexts = queryRepo.findWithoutFlowDataByTransIdList(Collections.singletonList(transId));
        if (contexts.isEmpty()) {
            LOG.warn("Flow context list is empty, flowTransId : {}", transId);
            return;
        }
        Map<String, Object> filter = new HashMap<>();
        contexts.stream()
                .map(FlowContextPO::getTraceId)
                .collect(Collectors.toSet())
                .forEach(traceId -> terminateFlows(traceId, filter, operationContext));
    }

    /**
     * 重试执行所有状态为RETRYABLE的上下文
     */
    public void retryTask() {
        while (true) {
            try {
                if (!popRetryTask()) {
                    return;
                }
            } catch (Exception ex) {
                LOG.error("retry failed, exception: ", ex);
            } finally {
                SleepUtil.sleep(RETRY_INTERVAL);
            }
        }
    }

    /**
     * 是否拿到重试任务并成功执行
     *
     * @return 是否成功执行
     */
    public boolean popRetryTask() {
        LOG.info("Start retry task.");
        isRetryRunning = true;
        updateFlowRetryMap();
        if (flowRetryMap.isEmpty()) {
            LOG.info("Retry list is empty, retry end.");
            isRetryRunning = false;
            return false;
        }
        startRetry();
        return true;
    }

    private void startRetry() {
        LOG.info("Retry task size: {}.", flowRetryMap.size());
        Iterator<FlowRetryInfo> iterator = flowRetryMap.values().iterator();
        while (iterator.hasNext()) {
            FlowRetryInfo flowRetryInfo = iterator.next();
            if (flowRetryInfo.getTo().isMaxConcurrency()) {
                continue;
            }
            retryByToBatch(flowRetryInfo);
            iterator.remove();
        }
    }

    private void updateFlowRetryMap() {
        LocalDateTime now = LocalDateTime.now();
        List<FlowRetry> flowRetryList = retryRepo.filterByNextRetryTime(now, new ArrayList<>(flowRetryMap.keySet()))
                .stream()
                .filter(flowRetry -> Objects.equals(flowRetry.getEntityType(), TO_BATCH_KEY))
                .collect(Collectors.toList());

        List<String> toBatchIds = flowRetryList.stream().map(FlowRetry::getEntityId).collect(Collectors.toList());
        List<FlowContext<String>> contexts = repo.getWithoutFlowDataByToBatch(toBatchIds);
        cleanFlowRetry(toBatchIds, contexts);

        Map<String, List<FlowContext<String>>> retryContexts = contexts.stream()
                .filter(context -> traceOwnerService.isAnyOwn(context.getTraceId()))
                .collect(Collectors.groupingBy(FlowContext::getToBatch));
        flowRetryList.stream()
                .filter(flowRetry -> retryContexts.containsKey(flowRetry.getEntityId()))
                .forEach(flowRetry -> {
                    Optional<FlowRetryInfo> retryInfo = getFlowRetryInfo(retryContexts, flowRetry);
                    if (!retryInfo.isPresent()) {
                        return;
                    }
                    flowRetryMap.put(flowRetry.getEntityId(), retryInfo.get());
                });
    }

    private void cleanFlowRetry(List<String> toBatchIds, List<FlowContext<String>> contexts) {
        Map<String, List<FlowContext<String>>> allContext = contexts
                .stream()
                .collect(Collectors.groupingBy(FlowContext::getToBatch));
        List<String> cleanedIds = toBatchIds
                .stream()
                .filter(id -> (!allContext.containsKey(id)) || isFinishedContext(allContext.get(id)))
                .collect(Collectors.toList());
        retryRepo.delete(cleanedIds);
    }

    private Optional<FlowRetryInfo> getFlowRetryInfo(Map<String, List<FlowContext<String>>> retryContexts,
                                                     FlowRetry flowRetry) {
        FlowRetryInfo retryInfo = FlowRetryInfo
                .builder()
                .flowRetry(flowRetry)
                .flowContexts(retryContexts.get(flowRetry.getEntityId()))
                .build();

        FlowContext<String> context = retryInfo.getFlowContexts().get(0);
        String streamId = context.getStreamId();
        From<FlowData> from = ObjectUtils.cast(WaterFlows.getPublisher(streamId));
        // 小的并发概率下没有获取到
        if (from == null) {
            flowDefinitionCheck(context.getToBatch(), streamId);
            return Optional.empty();
        }
        String position = context.getPosition();
        To<FlowData, Object> to = from.getSubscriber(position);
        retryInfo.setTo(to);
        return Optional.of(retryInfo);
    }

    /**
     * 为retryJober更新相关数据库
     *
     * @param contexts contexts
     * @param position position
     * @param flowRetry flowRetry
     */
    @Transactional
    public void updateDbForRetry(List<FlowContext<FlowData>> contexts, String position, FlowRetry flowRetry) {
        // 当前调换顺序（理论上这样也更合理）
        // 1. 保证retry record更新失败后不会更新context状态，下次还能被重试拉起
        // 2. 目前待排查之前事务未回退context状态原因
        LocalDateTime retryTime = LocalDateTime.now();
        flowRetry.setLastRetryTime(retryTime);
        flowRetry.setRetryCount(flowRetry.getRetryCount() + 1);
        flowRetry.setNextRetryTime(retryTime.plus(RETRY_WAITING_INTERVAL, ChronoUnit.MILLIS));
        if (retryRepo.updateRetryRecord(Collections.singletonList(flowRetry)) == 0) {
            String toBatch = flowRetry.getEntityId();
            LOG.error("[updateDbForRetry] Updating flow_retry database failed for the toBatch {},"
                    + " firstContextId={}, retryContextSize={}.", toBatch, contexts.get(0).getId(), contexts.size());
            throw new JobberException(FLOW_RETRY_JOBER_UPDATE_DATABASE_FAILED, toBatch);
        }
        repo.updateStatus(contexts, READY.toString(), position);
    }

    /**
     * 是否在执行重试任务
     *
     * @return 重试任务状态
     */
    public synchronized boolean isRetryRunning() {
        return this.isRetryRunning;
    }

    private void retryByToBatch(FlowRetryInfo flowRetry) {
        String lockKey = StringUtils.join(STREAM_ID_SEPARATOR, "retry", flowRetry.getFlowRetry().getEntityId());
        Lock lock = locks.getDistributedLock(lockKey);
        boolean isLockAcquired = lock.tryLock();
        if (!isLockAcquired) {
            LOG.warn("[retryByToBatch] Acquire distributed lock {} failed", lockKey);
            return;
        }
        LOG.debug("[retryByToBatch] Acquire distributed lock {} succeeded", lockKey);
        try {
            executeRetry(flowRetry);
        } catch (Throwable ex) {
            LOG.error("[retryByToBatch] Caught a throwable during the retry. Caused by {}", ex.getMessage());
        } finally {
            lock.unlock();
            LOG.debug("[retryByToBatch] Release distributed lock {} succeeded", lockKey);
        }
    }

    private void executeRetry(FlowRetryInfo flowRetryInfo) {
        String toBatch = flowRetryInfo.getFlowRetry().getEntityId();
        List<FlowContext<String>> contexts = flowRetryInfo.getFlowContexts();
        List<String> traces = this.traceOwnerService.getTraces();
        List<FlowContext<String>> finalContexts = contexts.stream()
                .filter(context -> traces.stream().anyMatch(trace -> context.getTraceId().contains(trace)))
                .collect(Collectors.toList());
        if (unNeedRetry(toBatch, finalContexts)) {
            return;
        }

        String streamId = finalContexts.get(0).getStreamId();
        String position = finalContexts.get(0).getPosition();
        To<FlowData, Object> to = flowRetryInfo.getTo();

        // 节点流量已经超出，则直接返回
        if (to.isMaxConcurrency()) {
            return;
        }
        List<String> traceIds = finalContexts.stream()
                .flatMap(c -> c.getTraceId().stream())
                .distinct()
                .collect(Collectors.toList());
        List<String> contextIds = finalContexts.stream().map(IdGenerator::getId).collect(Collectors.toList());
        LOG.info("Start retrying the jober, toBatch: {}, trace ids: {}, stream id: {}, context ids: {}, "
                + "position: {}", toBatch, traceIds.toString(), streamId, contextIds.toString(), position);
        List<FlowContext<FlowData>> retryContexts = updateRetryStatus(flowRetryInfo, streamId, position,
                finalContexts, to);
        to.getProcessMode().submit(to, retryContexts);
        LOG.info("Retry jober succeeded, toBatch: {}, trace ids: {}, stream id: {}, context ids: {}, " + "position: {}",
                toBatch, traceIds.toString(), streamId, contextIds.toString(), position);
    }

    private void flowDefinitionCheck(String toBatch, String streamId) {
        FlowDefinition flowDefinition = definitionRepo.findByStreamId(streamId);
        if (flowDefinition == null) {
            retryRepo.delete(Collections.singletonList(toBatch));
            LOG.error("[executeRetry] Retry failed: cannot find flow definition for the toBatch {}", toBatch);
            return;
        }
    }

    private boolean unNeedRetry(String toBatch, List<FlowContext<String>> finalContexts) {
        if (isFinishedContext(finalContexts)) {
            LOG.warn("[executeRetry] the batch is no need retry, toBatch={}.", toBatch);
            retryRepo.delete(Collections.singletonList(toBatch));
            return true;
        }
        if (finalContexts.stream().anyMatch(c -> !c.getStatus().equals(RETRYABLE))) {
            LOG.warn("[executeRetry] Retry failed: the toBatch {} is currently unretryable", toBatch);
            return true;
        }
        return false;
    }

    private boolean isFinishedContext(List<FlowContext<String>> contexts) {
        return contexts.stream().anyMatch(context -> FlowNodeStatus.isEndStatus(context.getStatus()));
    }

    private List<FlowContext<FlowData>> updateRetryStatus(FlowRetryInfo flowRetryInfo, String streamId, String position,
        List<FlowContext<String>> finalContexts, To<FlowData, Object> to) {
        Lock lock = locks.getDistributedLock(locks.streamNodeLockKey(streamId, position, PROCESS.toString()));
        lock.lock();
        List<FlowContext<FlowData>> retryContext;
        try {
            if (to.isMaxConcurrency()) {
                throw new JobberException(FLOW_NODE_MAX_TASK, to.getId());
            }
            String entityId = flowRetryInfo.getFlowRetry().getEntityId();
            retryContext = repo.getByToBatch(Collections.singletonList(entityId));
            if (retryContext.isEmpty()) {
                retryRepo.delete(Collections.singletonList(entityId));
                LOG.warn("not find retry context by batch id: {}.", entityId);
                throw new JobberException(FLOW_RETRY_JOBER_UPDATE_DATABASE_FAILED, entityId);
            }
            updateDbForRetry(retryContext, position, flowRetryInfo.getFlowRetry());
            to.updateConcurrency(1);
        } finally {
            lock.unlock();
        }
        return retryContext;
    }

    /**
     * 计算流程trace状态
     */
    @Scheduled(strategy = Scheduled.Strategy.FIXED_RATE, value = "5000")
    public void calculateFlowTraceStatus() {
        try {
            List<String> traceIds = this.traceOwnerService.getTraces();
            if (traceIds.isEmpty()) {
                return;
            }
            List<FlowTrace> flowTraces = traceRepo.findTraceByIdList(traceIds);
            List<String> flowTraceIds = flowTraces.stream().map(IdGenerator::getId).collect(Collectors.toList());
            traceIds.forEach(traceId -> {
                if (!flowTraceIds.contains(traceId)) {
                    this.traceOwnerService.release(traceId);
                }
            });
            flowTraces.forEach(trace -> {
                calculateTraceStatus(trace);
            });
        } catch (Throwable e) {
            LOG.error("The calculate flow trace status timer has error, traceSize={}, errorMessage={}.",
                    this.traceOwnerService.getTraces().size(), e.getMessage());
            LOG.error("Exception=", e);
        }
    }

    private void calculateTraceStatus(FlowTrace trace) {
        try {
            List<FlowContextPO> flowContextPOList = queryRepo.findWithoutFlowDataByTraceId(trace.getId());
            if (flowContextPOList.isEmpty()) {
                LOG.info("The trace is not ready, traceId={}.", trace.getId());
                return;
            }
            FlowDefinition flowDefinition = definitionRepo.findByStreamId(trace.getStreamId());
            if (flowDefinition == null) {
                LOG.warn("Flow definition is null, stream id:{}, trace:{}", trace.getStreamId(), trace.getId());
                this.traceOwnerService.release(trace.getId());
                return;
            }
            String status = getStatusNew(flowContextPOList, flowDefinition);
            if (!Objects.equals(status, FlowTraceStatus.RUNNING.name())) {
                updateTraceStatus(trace, flowContextPOList, status);
            }
        } catch (Throwable e) {
            LOG.error("Failed to calculate flow trace status, streamId={}, traceId={}.", trace.getStreamId(),
                    trace.getId(), e.getMessage());
            LOG.error("Exception=", e);
        }
    }

    private void updateTraceStatus(FlowTrace trace, List<FlowContextPO> flowContextPOList, String status) {
        LOG.info("The trace is completed, traceId={}, status={}, contextCount={}.", trace.getId(), status,
                flowContextPOList.size());
        traceRepo.updateStatus(Collections.singletonList(trace.getId()), status);
        String transId = flowContextPOList.get(0).getTransId();
        Lock transIdLock = locks.getDistributedLock(transId);
        transIdLock.lock();
        try {
            FlowTraceStatus transStatus = calculateTransStatus(transId);
            if (!Objects.equals(FlowTraceStatus.RUNNING, transStatus)) {
                LOG.info("The trans is completed, transId={}, status={}.", transId, transStatus);
                transFinishedCallback(transId, transStatus);
            }
        } finally {
            transIdLock.unlock();
        }
        LOG.debug("Start release trace lock, traceId={}.", trace.getId());
        this.traceOwnerService.release(trace.getId());
        LOG.info("Finish processing the trace, transId={}, traceId={}, status={}.", transId, trace.getId(), status);
    }

    private void transFinishedCallback(String transId, FlowTraceStatus transStatus) {
        String streamId = repo.getStreamIdByTransId(transId);
        String metaId = streamId.split(String.valueOf(STREAM_ID_SEPARATOR))[0];
        String version = streamId.split(String.valueOf(STREAM_ID_SEPARATOR))[1];
        List<String> traceIds = repo.getTraceByTransId(transId);
        FlowTransCompletionInfo info = FlowTransCompletionInfo.builder()
                .flowMetaId(metaId)
                .flowVersion(version)
                .flowTraceIds(traceIds)
                .flowTransId(transId)
                .status(transStatus)
                .build();
        consumers.forEach(consumer -> consumer.onFlowTransCompleted(info));
    }

    /**
     * calculateTransStatus
     *
     * @param transId transId
     * @return FlowTraceStatus
     */
    public FlowTraceStatus calculateTransStatus(String transId) {
        List<String> traceIds = repo.getTraceByTransId(transId);
        List<FlowTrace> traces = traceRepo.findTraceByIdList(traceIds);
        boolean isAllTerminated = traces.stream()
                .allMatch(flowTrace -> Objects.equals(FlowTraceStatus.TERMINATE, flowTrace.getStatus()));
        boolean isAllArchived = traces.stream()
                .allMatch(flowTrace -> Objects.equals(FlowTraceStatus.ARCHIVED, flowTrace.getStatus()));
        boolean isAllError = traces.stream()
                .allMatch(flowTrace -> Objects.equals(FlowTraceStatus.ERROR, flowTrace.getStatus()));
        boolean hasArchivedContext = traces.stream()
                .anyMatch(flowTrace -> Objects.equals(FlowTraceStatus.PARTIAL_ERROR, flowTrace.getStatus())
                        || Objects.equals(FlowTraceStatus.ARCHIVED, flowTrace.getStatus()));
        boolean hasRunningTrace = traces.stream()
                .anyMatch(flowTrace -> Objects.equals(FlowTraceStatus.RUNNING, flowTrace.getStatus()));
        if (hasRunningTrace) {
            return FlowTraceStatus.RUNNING;
        } else if (isAllError) {
            return FlowTraceStatus.ERROR;
        } else if (isAllArchived) {
            return FlowTraceStatus.ARCHIVED;
        } else if (isAllTerminated) {
            return FlowTraceStatus.TERMINATE;
        } else if (hasArchivedContext) {
            return FlowTraceStatus.PARTIAL_ERROR;
        } else {
            return FlowTraceStatus.RUNNING;
        }
    }

    /**
     * 根据contextIds删除context
     *
     * @param contextIds contextIds
     * @param operationContext 操作上下文
     */
    public void deleteFlowContexts(List<String> contextIds, OperationContext operationContext) {
        repo.deleteByContextIds(contextIds);
    }

    /**
     * 更新traceIds的状态
     *
     * @param traceIds traceIds
     * @param status status
     */
    public void updateTraceStatus(List<String> traceIds, FlowTraceStatus status) {
        traceRepo.updateStatus(traceIds, status.toString());
    }

    /**
     * 获取context的traceId
     *
     * @param contextIds contextIds
     * @return traceId列表
     */
    public List<String> findTraceIdsByContextIds(List<String> contextIds) {
        return repo.findTraceIdsByContextIds(contextIds);
    }
}
