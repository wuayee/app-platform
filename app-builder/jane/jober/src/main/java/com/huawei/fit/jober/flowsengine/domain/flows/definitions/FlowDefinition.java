/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.definitions;

import static com.huawei.fit.jober.common.Constant.STREAM_ID_SEPARATOR;
import static com.huawei.fit.jober.common.ErrorCodes.ENTITY_NOT_FOUND;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_FIND_TO_NODE_BY_EVENT_FAILED;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_HAS_NO_START_NODE;
import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeType.START;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.FlowNode;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.events.FlowEvent;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowDefinitionStatus;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeType;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.FitStream.Publisher;
import com.huawei.fit.jober.flowsengine.utils.WaterFlows;
import com.huawei.fitframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;

/**
 * 流程定义核心类
 * 流程定义来自前端的流程配置，主要包含流程节点、节点事件、节点任务
 * 其中流程节点可以包含1~N个节点事件
 * 其中流程节点可以包含1~N个节点任务
 * state节点自动执行是produce处理类型，可以接filterBlock产生M:N的实例数据，与手动执行互斥
 * state节点手动执行是map处理类型，可以接validateBlock实现手动执行任务，与自动执行互斥
 * condition、parallel节点是just处理类型，可以接validateBlock实现手动执行任务，且不能接filterBlock产生M:N的实例数据
 *
 * @author g00564732
 * @since 2023/08/14
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowDefinition {
    /**
     * 是否允许输出节点作用域的key
     */
    public static final String ENABLE_OUTPUT_SCOPE_KEY = "enableOutputScope";

    /**
     * 流程定义ID
     */
    @Getter
    @Setter
    private String definitionId;

    /**
     * 流程定义metaID，与前端保持一致
     */
    @Getter
    @Setter
    private String metaId;

    /**
     * 流程定义名称
     */
    @Getter
    @Setter
    private String name;

    /**
     * 流程定义描述信息
     */
    @Getter
    @Setter
    private String description;

    /**
     * 流程定义版本
     */
    @Getter
    @Setter
    private String version;

    /**
     * 租户唯一标识的
     */
    @Getter
    @Setter
    private String tenant;

    /**
     * 创建人
     */
    @Setter
    @Getter
    private String createdBy;

    /**
     * 流程中间节点，key为节点metaId，value为节点实例
     */
    @Setter
    @Getter
    private Map<String, FlowNode> nodeMap;

    /**
     * 流程定义状态
     */
    @Getter
    @Setter
    private FlowDefinitionStatus status;

    /**
     * 流程定义创建时间
     */
    @Getter
    @Setter
    private String releaseTime;

    /**
     * 流程的扩展属性信息
     */
    @Getter
    @Setter
    private Map<String, Object> properties;

    /**
     * 将流程定义转换为处理流，运行流程实例
     * 初次启动流程实例
     *
     * @param repo 流程实例运行时的处理context的repo
     * @param messenger 流程实例运行时的处理context的messenger
     * @param locks 流程实例运行时的处理context的锁
     * @return {@link Publisher<FlowData>} stream流程
     */
    public Publisher<FlowData> convertToFlow(FlowContextRepo<FlowData> repo, FlowContextMessenger messenger,
            FlowLocks locks) {
        String streamId = this.getStreamId();
        Lock lock = locks.getLocalLock(streamId);
        lock.lock();
        try {
            Publisher<FlowData> exists = WaterFlows.getPublisher(streamId);
            if (exists != null) {
                return exists;
            }
            FlowNode.FlowEnv flowEnv = new FlowNode.FlowEnv(repo, messenger, locks);
            nodeMap.values().forEach((fromNode) -> {
                fromNode.setParentFlow(this);
                fromNode.getEvents().forEach(event -> {
                    //  startNode不能出现在event的to属性, endNode不能出现在event的from属性
                    FlowNode toNode = nodeMap.get(event.getTo());
                    fromNode.subscribe(streamId, flowEnv, toNode, event);
                });
            });
            return WaterFlows.putPublisher(streamId, getFlowNode(START).getPublisher(streamId, repo, messenger, locks));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取流程定义的streamId
     *
     * @return 流程定义的streamId
     */
    public String getStreamId() {
        return StringUtils.join(STREAM_ID_SEPARATOR, this.metaId, this.version);
    }

    /**
     * 根据节点metaId获取流程定义中的节点
     *
     * @param metaId 流程节点ID
     * @return {@link FlowNode} 流程节点实例
     */
    public FlowNode getFlowNode(String metaId) {
        return this.nodeMap.get(metaId);
    }

    /**
     * 根据边metaId获取流程定义中的节点
     *
     * @param eventId 流程边ID
     * @return {@link FlowNode} 流程节点实例
     */
    public FlowNode getFlowNodeByEvent(String eventId) {
        return nodeMap.values()
                .stream()
                .map(n -> n.getEvents()
                        .stream()
                        .filter(e -> e.getMetaId().equals(eventId))
                        .findAny()
                        .map(FlowEvent::getTo)
                        .orElse(""))
                .filter(StringUtils::isNotEmpty)
                .findAny()
                .map(t -> nodeMap.get(t))
                .orElseThrow((() -> new JobberException(FLOW_FIND_TO_NODE_BY_EVENT_FAILED)));
    }

    /**
     * getFromNodeByEvent
     *
     * @param eventId eventId
     * @return FlowNode
     */
    public FlowNode getFromNodeByEvent(String eventId) {
        return nodeMap.values()
                .stream()
                .map(n -> n.getEvents()
                        .stream()
                        .filter(e -> e.getMetaId().equals(eventId))
                        .findAny()
                        .map(FlowEvent::getFrom)
                        .orElse(""))
                .filter(StringUtils::isNotEmpty)
                .findAny()
                .map(from -> nodeMap.get(from))
                .orElseThrow((() -> new JobberException(FLOW_FIND_TO_NODE_BY_EVENT_FAILED)));
    }

    /**
     * 根据节点类型获取节点
     *
     * @param type 流程节点类型
     * @return {@link FlowNode} 流程节点实例
     */
    public FlowNode getFlowNode(FlowNodeType type) {
        Optional<FlowNode> startNode = nodeMap.values().stream().filter(node -> node.belongTo(type)).findAny();
        if (!startNode.isPresent()) {
            throw new JobberException(FLOW_HAS_NO_START_NODE, this.getStreamId());
        }
        return startNode.get();
    }

    /**
     * 获取流程定义中的节点ID集合
     *
     * @return 节点ID集合
     */
    public Set<String> getNodeIdSet() {
        return this.nodeMap.keySet();
    }

    /**
     * getToNodeByEventId
     *
     * @param eventMetaId eventMetaId
     * @return FlowNode
     */
    public FlowNode getToNodeByEventId(String eventMetaId) {
        return nodeMap.values()
                .stream()
                .map(flowNode -> flowNode.getEventByMetaId(eventMetaId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(flowEvent -> nodeMap.get(flowEvent.getTo()))
                .findFirst()
                .orElseThrow((() -> new JobberException(FLOW_FIND_TO_NODE_BY_EVENT_FAILED)));
    }

    /**
     * 获取指向每个节点的event集合
     *
     * @return 节点以及对应event集合
     */
    public Map<String, Set<FlowEvent>> getFromEvents() {
        Map<String, Set<FlowEvent>> fromEvents = new HashMap<>();

        this.getNodeMap().values().forEach(n -> n.getEvents().forEach(e -> {
            if (StringUtils.isNotEmpty(e.getTo())) {
                fromEvents.putIfAbsent(e.getTo(), new HashSet<>());
                fromEvents.get(e.getTo()).add(e);
            }
        }));
        return fromEvents;
    }

    /**
     * 获取end节点id
     *
     * @return end节点id
     */
    public String getEndNode() {
        return this.getNodeMap()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().belongTo(FlowNodeType.END))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new JobberException(ENTITY_NOT_FOUND, "endNode", this.getDefinitionId()));
    }

    /**
     * 查询流程的配置信息
     *
     * @param key 属性key
     * @return 属性对象
     */
    public <T> T getProperty(String key) {
        return cast(this.properties.get(key));
    }

    /**
     * 是否允许节点的返回值有作用域限制, 可以防止不同节点的输出相同key时不会相互覆盖，后续的节点可以引用不同节点的相同输出key
     *
     * @return boolean
     */
    public boolean isEnableOutputScope() {
        Boolean enableOutputScope = this.getProperty(ENABLE_OUTPUT_SCOPE_KEY);
        return !Objects.isNull(enableOutputScope) && enableOutputScope;
    }
}
