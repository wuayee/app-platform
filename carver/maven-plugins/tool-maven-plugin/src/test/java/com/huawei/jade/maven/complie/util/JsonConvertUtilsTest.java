/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.maven.complie.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import modelengine.fitframework.util.ObjectUtils;
import com.huawei.jade.maven.complie.entity.MethodEntity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 表示 {@link JsonConvertUtils} 的单元测试。
 *
 * @author 杭潇
 * @since 2024-08-05
 */
@DisplayName("测试 JsonConvertUtils 类")
class JsonConvertUtilsTest {
    @Test
    @DisplayName("解析 MethodEntity 数据正确")
    void convertMethodEntityObjectMapSuccessfully() {
        MethodEntity methodEntity = new MethodEntity();
        methodEntity.setMethodName("testMethod");
        methodEntity.setMethodDescription("This is a test method.");
        methodEntity.setReturnDescription("Return description");
        methodEntity.setReturnType("{\"type\":\"string\"}");
        methodEntity.setFitableId("fitableId123");
        methodEntity.setGenericableId("genericableId123");
        Set<String> tags = new LinkedHashSet<>();
        tags.add("tag1");
        tags.add("tag2");
        methodEntity.setTags(tags);

        Map<String, Object> result = JsonConvertUtils.convertMethodEntityObjectMap(methodEntity);
        Map<String, Object> schema = ObjectUtils.cast(result.get("schema"));
        assertEquals("testMethod", schema.get("name"));
        assertEquals("This is a test method.", schema.get("description"));

        Map<String, Object> runnables = ObjectUtils.cast(result.get("runnables"));
        Map<String, Object> fit = ObjectUtils.cast(runnables.get("FIT"));
        assertEquals("fitableId123", fit.get("fitableId"));
        assertEquals("genericableId123", fit.get("genericableId"));

        Map<String, Object> returns = ObjectUtils.cast(schema.get("return"));
        assertEquals("Return description", returns.get("description"));
        assertEquals("string", returns.get("type"));

        Map<String, Object> extensions = ObjectUtils.cast(result.get("extensions"));
        assertEquals(tags, extensions.get("tags"));
    }
}