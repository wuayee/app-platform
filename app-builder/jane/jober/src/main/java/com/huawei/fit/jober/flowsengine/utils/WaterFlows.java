/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.utils;

import static java.lang.System.identityHashCode;

import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.FitStream;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.FitStream.Publisher;
import com.huawei.fitframework.log.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WaterFlow的全局单列实例管理类
 * WaterFlows.getPublisher根据流程定义的唯一ID：streamID获取WaterFlow实例
 * 如果已经存在则直接返回，否则需要将FlowDefinition转换WaterFlow，并保存下来
 * 后续需要提供删除功能：流程定义删除时，对应的Lock和WaterFlow都要删除
 *
 * @author g00564732
 * @since 2023/10/30
 */
public final class WaterFlows {
    private static final Logger LOG = Logger.get(WaterFlows.class);

    private static final Map<String, Publisher<FlowData>> FLOWS = new ConcurrentHashMap<>();

    private static final Map<String, Map<String, NodeInfo>> NODE_INFO_MAP = new HashMap<>();

    /**
     * 根据流程版本更新water flow
     *
     * @param streamId 流程版本
     * @param publisher water flow
     * @return {@link Publisher} water flow
     */
    public static Publisher<FlowData> putPublisher(String streamId, Publisher<FlowData> publisher) {
        Publisher<FlowData> exits = Optional.ofNullable(FLOWS.putIfAbsent(streamId, publisher))
                .orElseGet(() -> getPublisher(streamId));
        Map<String, NodeInfo> streamNodeMap = NODE_INFO_MAP.computeIfAbsent(streamId, key -> new HashMap<>());
        buildNodeInfoMap(publisher, streamNodeMap, 0);
        LOG.info("[WaterFlows::putPublisher] put new publisher only if not exists, "
                        + "streamId: {}, publisher: {}, exits: {}",
                streamId, identityHashCode(publisher), identityHashCode(exits));
        return exits;
    }

    private static void buildNodeInfoMap(Publisher publisher, Map<String, NodeInfo> nodeInfoMap, int order) {
        if (nodeInfoMap.containsKey(publisher.getId())) {
            return;
        }
        nodeInfoMap.put(publisher.getId(), new NodeInfo(order));
        LOG.info("buildNodeInfo, nodeId: {}, order:{}", publisher.getId(), order);
        List<FitStream.Subscription> subscriptions = publisher.getSubscriptions();
        subscriptions.forEach(subscription -> {
            FitStream.Subscriber to = subscription.getTo();
            if (to instanceof Publisher) {
                buildNodeInfoMap((Publisher) to, nodeInfoMap, order + 1);
            } else {
                // for end node
                nodeInfoMap.put(to.getId(), new NodeInfo(order + 1));
            }
        });
    }

    /**
     * 获取节点的顺序
     *
     * @param streamId 流程版本id
     * @param nodeId 节点id
     * @return 节点顺序
     */
    public static int getNodeOrder(String streamId, String nodeId) {
        int resultOrder = Optional.ofNullable(NODE_INFO_MAP.get(streamId))
                .flatMap(map -> Optional.ofNullable(map.get(nodeId)))
                .map(NodeInfo::getOrder)
                .orElse(0);
        LOG.info("getNodeOrder, nodeId: {}, order:{}", nodeId, resultOrder);
        return resultOrder;
    }

    /**
     * 根据流程版本获取water flow
     *
     * @param streamId 流程版本
     * @return {@link Publisher} water flow
     */
    public static Publisher<FlowData> getPublisher(String streamId) {
        return FLOWS.get(streamId);
    }

    /**
     * 根据流程版本删除water flow
     *
     * @param streamId 流程版本
     */
    public static void removePublisher(String streamId) {
        FLOWS.remove(streamId);
    }

    /**
     * 清除flowable flow缓存
     */
    public static void clear() {
        FLOWS.clear();
    }

    private static class NodeInfo {
        private final int order;

        public NodeInfo(int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
        }
    }
}
