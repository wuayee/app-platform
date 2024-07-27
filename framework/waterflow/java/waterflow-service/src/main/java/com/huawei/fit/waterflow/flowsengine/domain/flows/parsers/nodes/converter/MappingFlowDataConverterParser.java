/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.nodes.converter;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.FlowDataConverter;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.MappingFlowDataConverter;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.MappingFromType;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.MappingNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.MappingNodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 基于映射关系的转换器
 *
 * @author s00558940
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
        return MappingNode.builder()
                .name(cast(config.get(NAME)))
                .type(type)
                .from(from)
                .value(value)
                .referenceNode(cast(config.get(REFERENCE_NODE)))
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
