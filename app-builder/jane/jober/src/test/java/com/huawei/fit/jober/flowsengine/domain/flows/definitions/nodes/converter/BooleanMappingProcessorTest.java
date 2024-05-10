/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.huawei.fit.jober.common.exceptions.JobberParamException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * 布尔类型转换的测试
 *
 * @author s00558940
 * @since 2024/4/20
 */
public class BooleanMappingProcessorTest {
    @Test
    @DisplayName("测试将boolean的值转换的成功场景")
    void shouldReturnValueWhenGenerateGivenBooleanValue() {
        MappingNode trueMappingConfig = new MappingNode("true", MappingNodeType.BOOLEAN, MappingFromType.INPUT, true,
                "");
        MappingNode falseMappingConfig = new MappingNode("false", MappingNodeType.BOOLEAN, MappingFromType.INPUT, false,
                "");

        MappingProcessor target = new BooleanMappingProcessor();
        Object trueResult = target.generate(trueMappingConfig, new HashMap<>());
        Object falseResult = target.generate(falseMappingConfig, new HashMap<>());

        assertEquals(true, trueResult);
        assertEquals(false, falseResult);
    }

    @Test
    @DisplayName("测试将字符串的值转换的成功场景")
    void shouldReturnValueWhenGenerateGivenStringBooleanValue() {
        MappingNode trueMappingConfig = new MappingNode("true", MappingNodeType.BOOLEAN, MappingFromType.INPUT, "true",
                "");
        MappingNode falseMappingConfig = new MappingNode("false", MappingNodeType.BOOLEAN, MappingFromType.INPUT,
                "false", "");

        MappingProcessor target = new BooleanMappingProcessor();
        Object trueResult = target.generate(trueMappingConfig, new HashMap<>());
        Object falseResult = target.generate(falseMappingConfig, new HashMap<>());

        assertEquals(true, trueResult);
        assertEquals(false, falseResult);
    }

    @Test
    @DisplayName("测试其它无法转换的类型抛异常的失败场景")
    void shouldThrowJoberExceptionWhenGenerateGivenInvalidTypeValue() {
        MappingNode mappingConfig = new MappingNode("true", MappingNodeType.BOOLEAN, MappingFromType.INPUT,
                new HashMap<>(), "");

        MappingProcessor target = new BooleanMappingProcessor();
        assertThrows(JobberParamException.class, () -> target.generate(mappingConfig, new HashMap<>()));
    }
}
