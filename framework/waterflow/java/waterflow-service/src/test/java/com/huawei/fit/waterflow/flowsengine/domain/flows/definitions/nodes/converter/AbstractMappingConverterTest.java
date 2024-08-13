/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.huawei.fitframework.util.MapBuilder;

import lombok.Getter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

/**
 * 映射抽象类功能的测试
 *
 * @author 宋永坦
 * @since 2024/4/20
 */
public class AbstractMappingConverterTest {
    private class MappingConverterForTest extends AbstractMappingProcessor {
        @Getter
        private int callCount = 0;

        @Override
        protected Object generateInput(MappingNode mappingConfig, Map<String, Object> businessData) {
            ++callCount;
            return mappingConfig.getValue();
        }
    }

    @Test
    @DisplayName("测试引用单层key的转换的成功场景")
    void shouldReturnValueWhenGenerateGivenReferenceTypeAndOneLevelPath() {
        String expectValue = "level1Value";
        MappingNode mappingConfig = new MappingNode("keyString", MappingNodeType.STRING, MappingFromType.REFERENCE,
                Arrays.asList("level1"), "");
        Map<String, Object> businessData = MapBuilder.<String, Object>get().put("level1", expectValue).build();

        AbstractMappingProcessor target = new MappingConverterForTest();
        Object result = target.generate(mappingConfig, businessData);

        assertTrue(result instanceof String);
        assertEquals(expectValue, result);
    }

    @Test
    @DisplayName("测试引用多层key的转换的成功场景")
    void shouldReturnValueWhenGenerateGivenReferenceTypeAndMultiLevelPath() {
        String expectValue = "level2Value";
        MappingNode mappingConfig = new MappingNode("keyString", MappingNodeType.STRING, MappingFromType.REFERENCE,
                Arrays.asList("level1", "level2"), "");
        Map<String, Object> businessData = MapBuilder.<String, Object>get()
                .put("level1", MapBuilder.<String, Object>get().put("level2", expectValue).build())
                .build();

        AbstractMappingProcessor target = new MappingConverterForTest();
        Object result = target.generate(mappingConfig, businessData);

        assertTrue(result instanceof String);
        assertEquals(expectValue, result);
    }

    @Test
    @DisplayName("测试如果引用path不存在时返回空引用的场景")
    void shouldReturnNullWhenGenerateGivenReferenceTypeAndMultiLevelPath() {
        MappingNode mappingConfig = new MappingNode("keyString", MappingNodeType.STRING, MappingFromType.REFERENCE,
                Arrays.asList("level1"), "");
        Map<String, Object> businessData = MapBuilder.<String, Object>get().put("level2", "xxx").build();

        AbstractMappingProcessor target = new MappingConverterForTest();
        Object result = target.generate(mappingConfig, businessData);

        assertNull(result);
    }

    @Test
    @DisplayName("测试值类型调用generateValue的成功场景")
    void shouldCallGenerateValueWhenGenerateGivenValueType() {
        int expectCallGenerateValueCount = 1;
        String expectValue = "value";
        MappingNode mappingConfig = new MappingNode("keyString", MappingNodeType.STRING, MappingFromType.INPUT,
                expectValue,
                "");
        Map<String, Object> businessData = MapBuilder.<String, Object>get().put("level2", "xxx").build();

        MappingConverterForTest target = new MappingConverterForTest();
        Object result = target.generate(mappingConfig, businessData);

        assertEquals(expectValue, result);
        assertEquals(target.getCallCount(), expectCallGenerateValueCount);
    }
}
