/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.utils.FlowUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 测试FlowData的输入输出转换
 *
 * @author 宋永坦
 * @since 2024/4/23
 */
class MappingFlowDataConverterTest {
    @Test
    @DisplayName("将返回值按照配置添加到businessData中的成功场景")
    void shouldAddResultToBusinessDataWhenConvertOutputGivenOutputNameAndResult() {
        String customName = "customName";
        String expectGenericableResult = "helloWorld";
        MappingFlowDataConverter target = new MappingFlowDataConverter(null, customName);

        FlowData flowData = FlowData.builder().businessData(new HashMap<>()).contextData(new HashMap<>()).build();
        flowData.getBusinessData().putAll(target.convertOutput(expectGenericableResult));

        assertTrue(flowData.getBusinessData().containsKey(customName));
        Assertions.assertEquals(expectGenericableResult, flowData.getBusinessData().get(customName));
    }

    @Test
    @DisplayName("将入参按照规则生成的成功场景")
    void shouldGenerateToBusinessDataWhenConvertInputGivenMappingConfig() {
        List<MappingNode> inputMappingConfig = new ArrayList<>(
                Arrays.asList(new MappingNode("str", MappingNodeType.STRING, MappingFromType.INPUT, "str1", ""),
                        new MappingNode("int", MappingNodeType.INTEGER, MappingFromType.INPUT, 666, "")));
        MappingFlowDataConverter target = new MappingFlowDataConverter(inputMappingConfig, null);

        FlowData flowData = FlowData.builder().businessData(new HashMap<>()).contextData(new HashMap<>()).build();
        flowData.setBusinessData(
                FlowUtil.mergeMaps(flowData.getBusinessData(), target.convertInput(flowData.getBusinessData())));

        assertTrue(flowData.getBusinessData().containsKey("str"));
        assertTrue(flowData.getBusinessData().containsKey("int"));
        assertEquals("str1", flowData.getBusinessData().get("str"));
        assertEquals(666, flowData.getBusinessData().get("int"));
    }
}