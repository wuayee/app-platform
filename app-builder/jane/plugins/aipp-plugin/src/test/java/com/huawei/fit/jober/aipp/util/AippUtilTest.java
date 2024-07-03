/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class AippUtilTest {
    @Test
    @DisplayName("key重复出现场景")
    void testParsePromptWithMultiRepeatedKeys() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("key1", "value1");
        businessData.put("key2", "value2");
        businessData.put("key3", "value3");

        String testBasePrompt = "hello $(key1)$(key1) world $(key2)$(key1)!";
        String expectedPrompt = "hello value1value1 world value2value1!";
        Assertions.assertEquals(expectedPrompt, DataUtils.parsePrompt(businessData, testBasePrompt));
    }

    @Test
    @DisplayName("括号内包含空格")
    void testParsePromptWithBlankCharacters() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("key1", "value1");
        businessData.put("key2", "value2");
        businessData.put("key 3", "value3");

        String testBasePrompt = "hello $( key1)$(key1 ) world $( key2 )$(  key1  )$(key 3)!";
        String expectedPrompt = "hello value1value1 world value2value1value3!";
        Assertions.assertEquals(expectedPrompt, DataUtils.parsePrompt(businessData, testBasePrompt));
    }

    @Test
    @DisplayName("${}花括号格式")
    void testParsePromptWithCurlyBrace() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("key1", "value1");
        businessData.put("key2", "value2");
        businessData.put("key 3", "value3");

        String testBasePrompt = "hello ${ key1}${key1 } world ${ key2 }${  key1  }${key 3}!";
        String expectedPrompt = "hello value1value1 world value2value1value3!";
        Assertions.assertEquals(expectedPrompt, DataUtils.parsePrompt(businessData, testBasePrompt));
    }

    @Test
    @DisplayName("括号嵌套场景")
    void testParsePromptWithNestedBrace() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("key1", "value1");
        businessData.put("key2", "value2");

        String testBasePrompt = "hello ${key2 $(key1) }$(${key2 }) world";
        String expectedPrompt = "hello ${key2 value1 }$(value2) world";
        Assertions.assertEquals(expectedPrompt, DataUtils.parsePrompt(businessData, testBasePrompt));
    }

    @Test
    @DisplayName("括号混用场景-待屏蔽")
    void testParsePromptWithFixBrace() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("key1", "value1");
        businessData.put("key2", "value2");

        String testBasePrompt = "hello ${key1)${key2) world";
        String expectedPrompt = "hello value1value2 world";
        Assertions.assertEquals(expectedPrompt, DataUtils.parsePrompt(businessData, testBasePrompt));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "hello world"})
    @DisplayName("不包含key结构的场景")
    void testParsePromptWithoutKeys(String noKeyPrompt) {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("key1", "value1");
        businessData.put("key2", "value2");
        Assertions.assertEquals(noKeyPrompt, DataUtils.parsePrompt(businessData, noKeyPrompt));
    }
}
