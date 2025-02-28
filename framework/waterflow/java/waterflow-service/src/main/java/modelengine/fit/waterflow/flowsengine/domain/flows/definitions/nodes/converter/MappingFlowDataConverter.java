/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于映射关系的数据转换
 *
 * @author 宋永坦
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
        Map<String, Object> result = new LinkedHashMap<>();
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

    @Override
    public String getOutputName() {
        return this.outputName;
    }
}
