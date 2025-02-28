/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.converter;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.FlowDataConverter;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.MappingFlowDataConverter;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.MappingFromType;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.MappingNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.MappingNodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 基于映射关系的转换器
 *
 * @author 宋永坦
 * @since 2024/4/17
 */
public class MappingFlowDataConverterParser implements FlowDataConverterParser {
    private static final String ENTITY = "entity";

    private static final String INPUT = "inputParams";

    private static final String OUTPUT = "outputParams";

    private static final String NAME = "name";

    private static final String TYPE = "type";

    private static final String VALUE = "value";

    private static final String FROM = "from";

    private static final String REFERENCE_NODE = "referenceNode";
    private static final String FALLBACK_ON_NODE_DATA_MISS = "fallbackOnNodeDataMiss";

    private static MappingNode getMappingNode(Map<String, Object> config) {
        Object value = config.get(VALUE);
        MappingNodeType type = MappingNodeType.get(cast(config.get(TYPE)));
        MappingFromType from = MappingFromType.get(cast(config.get(FROM)));
        if (MappingNodeType.isNestedType(type) && MappingFromType.EXPAND.equals(from)) {
            List<Map<String, Object>> nestedValueConfig = cast(value);
            value = nestedValueConfig.stream()
                    .map(MappingFlowDataConverterParser::getMappingNode)
                    .collect(Collectors.toList());
        }
        boolean fallbackOnNodeDataMiss = (boolean) Optional.ofNullable(config.get(FALLBACK_ON_NODE_DATA_MISS))
                .orElse(false);
        return MappingNode.builder()
                .name(cast(config.get(NAME)))
                .type(type)
                .from(from)
                .value(value)
                .referenceNode(cast(config.get(REFERENCE_NODE)))
                .fallbackOnNodeDataMiss(fallbackOnNodeDataMiss)
                .build();
    }

    @Override
    public FlowDataConverter parse(Map<String, Object> converterConfig) {
        Map<String, Object> entity = cast(converterConfig.get(ENTITY));
        List<Map<String, Object>> input = cast(Optional.ofNullable(entity.get(INPUT)).orElse(new ArrayList<>()));
        List<Map<String, Object>> output = cast(Optional.ofNullable(entity.get(OUTPUT)).orElse(new ArrayList<>()));
        List<MappingNode> inputMappingConfig = input.stream()
                .map(MappingFlowDataConverterParser::getMappingNode)
                .collect(Collectors.toList());
        String outputName = "";
        if (!output.isEmpty()) {
            outputName = cast(output.get(0).get(NAME));
        }

        return new MappingFlowDataConverter(inputMappingConfig, outputName);
    }
}
