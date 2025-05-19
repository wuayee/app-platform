/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * 字符串类型的转换测试
 *
 * @author 宋永坦
 * @since 2024/4/20
 */
class StringMappingProcessorTest {
    @Test
    @DisplayName("测试字符串值转换的成功场景")
    void shouldReturnValueWhenGenerateGivenStringValue() {
        String expectValue = "this is a string";
        MappingNode mappingConfig = new MappingNode("key", MappingNodeType.STRING, MappingFromType.INPUT, expectValue,
                "");

        MappingProcessor target = new StringMappingProcessor();
        Object result = target.generate(mappingConfig, new HashMap<>());

        assertEquals(expectValue, result);
    }

    @Test
    @DisplayName("测试其它类型值转换的成功场景")
    void shouldReturnValueWhenGenerateGivenCanToStringValue() {
        int value = 666;
        String expectValue = String.valueOf(value);
        MappingNode mappingConfig = new MappingNode("key", MappingNodeType.STRING, MappingFromType.INPUT, value, "");

        MappingProcessor target = new StringMappingProcessor();
        Object result = target.generate(mappingConfig, new HashMap<>());

        assertEquals(expectValue, result);
    }
}