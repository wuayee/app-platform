/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.events;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;

import com.huawei.fit.jober.common.exceptions.JobberException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.events.FlowEvent;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * 流程中event解析类
 *
 * @author 杨祥宇
 * @since 2023/8/16
 */
public class EventParser {
    /**
     * 流程实践解析器单列
     */
    public static final EventParser INSTANCE = new EventParser();

    /**
     * 事件解析
     *
     * @param flowGraphData flowGraphData {@link FlowGraphData} 流程json操作封装对象
     * @param allNodeMap 流程中Node集合
     */
    public void parse(FlowGraphData flowGraphData, Map<String, FlowNode> allNodeMap) {
        IntStream.range(0, flowGraphData.getEvents())
                .mapToObj(eventIndex -> {
                    FlowEvent flowEvent = FlowEvent.builder()
                            .metaId(flowGraphData.getEventMetaId(eventIndex))
                            .name(flowGraphData.getEventName(eventIndex))
                            .from(flowGraphData.getEventFromNode(eventIndex))
                            .to(flowGraphData.getEventToNode(eventIndex))
                            .conditionRule(flowGraphData.getEventConditionRule(eventIndex))
                            .priority(flowGraphData.getEventPriority(eventIndex))
                            .build();
                    Optional.ofNullable(allNodeMap.get(flowEvent.getFrom()))
                            .orElseThrow(() -> new JobberException(INPUT_PARAM_IS_INVALID, "Event toId is null"));
                    return flowEvent;
                })
                .sorted(Comparator.comparingInt(FlowEvent::getPriority))
                .forEach(flowEvent -> allNodeMap.get(flowEvent.getFrom()).getEvents().add(flowEvent));
    }
}
