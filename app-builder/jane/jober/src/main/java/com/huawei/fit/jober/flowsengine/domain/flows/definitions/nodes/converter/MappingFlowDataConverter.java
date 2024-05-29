/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于映射关系的数据转换
 *
 * @author s00558940
 * @since 2024/4/17
 */
public class MappingFlowDataConverter implements FlowDataConverter {
    private final List<MappingNode> inputMappingConfig;

    private final String outputName;

    public MappingFlowDataConverter(List<MappingNode> inputMappingConfig, String outputName) {
        this.inputMappingConfig = inputMappingConfig;
        this.outputName = outputName;
    }

    @Override
    public Map<String, Object> convertInput(Map<String, Object> input) {
        Map<String, Object> result = new HashMap<>();
        this.inputMappingConfig.forEach(mappingConfig -> {
            result.put(mappingConfig.getName(),
                    MappingProcessorFactory.get(mappingConfig).generate(mappingConfig, input));
        });
        return result;
    }

    @Override
    public Map<String, Object> convertOutput(Object result) {
        HashMap<String, Object> outputMap = new HashMap<>();
        if (!this.outputName.isEmpty()) {
            outputMap.put(outputName, result);
        }
        return outputMap;
    }
}
