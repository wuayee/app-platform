/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelenginei.jade.maven.complie.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

/**
 * 表示 {@link ToolEntity} 的单元测试。
 *
 * @author 杭潇
 * @since 2024-08-05
 */
@DisplayName("测试 ToolEntity 类")
class ToolEntityTest {
    @Test
    @DisplayName("测试 ToolEntity 标准化后数据正确")
    void shouldOkWhenToolEntityNormalize() {
        ToolEntity toolEntity = new ToolEntity();
        toolEntity.setNamespace("test");
        toolEntity.setName("testMethod");
        toolEntity.setDescription("This is a test method.");
        toolEntity.setReturnDescription("Return description");
        toolEntity.setReturnType("{\"type\":\"string\"}");
        toolEntity.setFitableId("fitableId123");
        toolEntity.setGenericableId("genericableId123");
        toolEntity.setReturnConvertor("");
        toolEntity.setExtraParameters(Collections.emptyList());
        toolEntity.setExtensions(MapBuilder.<String, Object>get().put("k1", "v1").put("k2", "v2").build());

        Map<String, Object> result = toolEntity.normalize();
        Map<String, Object> schema = ObjectUtils.cast(result.get("schema"));
        assertEquals("testMethod", schema.get("name"));
        assertEquals("This is a test method.", schema.get("description"));

        Map<String, Object> runnable = ObjectUtils.cast(result.get("runnables"));
        Map<String, Object> fit = ObjectUtils.cast(runnable.get("FIT"));
        assertEquals("fitableId123", fit.get("fitableId"));
        assertEquals("genericableId123", fit.get("genericableId"));

        Map<String, Object> returns = ObjectUtils.cast(schema.get("return"));
        assertEquals("Return description", returns.get("description"));
        assertEquals("", returns.get("converter"));
        assertEquals("string", returns.get("type"));

        Map<String, Object> extensions = ObjectUtils.cast(result.get("extensions"));
        assertEquals("v1", extensions.get("k1"));
        assertEquals("v2", extensions.get("k2"));
    }
}