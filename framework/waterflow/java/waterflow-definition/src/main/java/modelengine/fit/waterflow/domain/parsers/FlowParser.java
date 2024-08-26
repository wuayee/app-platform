/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.parsers;

import static modelengine.fit.waterflow.common.ErrorCodes.INPUT_PARAM_IS_INVALID;

import lombok.RequiredArgsConstructor;
import modelengine.fit.waterflow.common.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.domain.definitions.FlowDefinition;
import modelengine.fit.waterflow.domain.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.domain.enums.FlowDefinitionStatus;
import modelengine.fit.waterflow.domain.enums.FlowNodeType;
import modelengine.fit.waterflow.domain.enums.FlowNodeTypeParser;
import modelengine.fit.waterflow.domain.parsers.nodes.NodeParser;
import modelengine.fit.waterflow.domain.parsers.nodes.events.EventParser;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.inspection.Validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * 流程定义JSON解析器
 * 负责解析JSON类型的流程数据
 *
 * @author 高诗意
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class FlowParser implements Parser {
    private final BrokerClient brokerClient;

    @Override
    public FlowDefinition parse(String flowDefinition) {
        Validation.notBlank(flowDefinition,
                () -> new WaterflowParamException(INPUT_PARAM_IS_INVALID, "flowDefinition"));
        FlowGraphData flowGraphData = new FlowGraphData(flowDefinition);
        Map<String, FlowNode> allNodeMap = parseFlowNode(flowGraphData);

        EventParser.INSTANCE.parse(flowGraphData, allNodeMap);

        return FlowDefinition.builder()
                .name(flowGraphData.getFlowName())
                .metaId(flowGraphData.getFlowMetaId())
                .version(flowGraphData.getFlowVersion())
                .status(FlowDefinitionStatus.getFlowDefinitionStatus(flowGraphData.getFlowStatus()))
                .description(flowGraphData.getFlowDescription())
                .nodeMap(allNodeMap)
                .build();
    }

    private Map<String, FlowNode> parseFlowNode(FlowGraphData flowGraphData) {
        Map<String, FlowNode> allNodeMap = new HashMap<>();
        IntStream.range(0, flowGraphData.getNodes()).forEach(nodeIndex -> {
            FlowNodeType nodeType = FlowNodeType.getNodeType(flowGraphData.getNodeType(nodeIndex));
            FlowNodeTypeParser flowNodeTypeParser = FlowNodeTypeParser.getType(nodeType);
            NodeParser nodeParser = flowNodeTypeParser.getNodeParser();
            Validation.notNull(nodeParser,
                    () -> new WaterflowParamException(INPUT_PARAM_IS_INVALID, "flow node type " + nodeType.getCode()));
            FlowNode flowNode = nodeParser.parseNode(flowGraphData, nodeIndex);
            if (Optional.ofNullable(flowNode.getJober()).isPresent()) {
                flowNode.getJober().setBrokerClient(brokerClient);
            }
            if (Optional.ofNullable(flowNode.getCallback()).isPresent()) {
                flowNode.getCallback().setBrokerClient(brokerClient);
            }
            if (allNodeMap.containsKey(flowNode.getMetaId())) {
                throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, "flow node metaId not allow same");
            }
            allNodeMap.put(flowNode.getMetaId(), flowNode);
        });
        return allNodeMap;
    }
}
