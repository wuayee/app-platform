/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.converter;

import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;

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
    public FlowData convertInput(FlowData input) {
        Map<String, Object> businessData = input.getBusinessData();
        inputMappingConfig.forEach(mappingConfig -> {
            businessData.put(mappingConfig.getName(),
                    MappingProcessorFactory.get(mappingConfig).generate(mappingConfig, businessData));
        });
        return input;
    }

    @Override
    public FlowData convertOutput(Object result, FlowData output) {
        if (!this.outputName.isEmpty()) {
            output.getBusinessData().put(outputName, result);
        }
        return output;
    }
}
