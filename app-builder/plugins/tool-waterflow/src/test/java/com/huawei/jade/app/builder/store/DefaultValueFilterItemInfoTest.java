/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.builder.store;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.store.ItemInfo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * 表示 {@link WaterFlowTool} 的单元测试。
 *
 * @author 王攀博
 * @since 2024-04-22
 */
@DisplayName("测试 DefaultValueFilterItemInfo")
public class DefaultValueFilterItemInfoTest {
    private ItemInfo itemInfo;

    @BeforeEach
    void setup() {
        this.itemInfo = new DefaultValueFilterItemInfo(this.buildItemInfo());
    }

    ItemInfo buildItemInfo() {
        return ItemInfo.custom()
                .category("Tool")
                .group("t1")
                .name("test_schema_default_implementation_name")
                .uniqueName("decorator-customize-water-flow-uuid")
                .tags(Collections.singleton("Customize-Workflow"))
                .description("This is a demo schema tool.")
                .schema(this.buildSchema())
                .build();
    }

    Map<String, Object> buildSchema() {
        return MapBuilder.<String, Object>get()
                .put("group", "t1")
                .put("name", "test_schema_default_implementation_name")
                .put("index", "test_schema_index")
                .put("description", "This is a demo schema tool.")
                .put("parameters",
                        MapBuilder.<String, Object>get()
                                .put("type", "object")
                                .put("properties",
                                        MapBuilder.<String, Object>get()
                                                .put("p1",
                                                        MapBuilder.<String, Object>get()
                                                                .put("type", "string")
                                                                .put("description", "This is the first parameter.")
                                                                .build())
                                                .put("p2",
                                                        MapBuilder.<String, Object>get()
                                                                .put("type", "string")
                                                                .put("default", "p2_default_value.")
                                                                .build())
                                                .put("p3",
                                                        MapBuilder.<String, Object>get().put("type", "string").build())
                                                .build())
                                .put("order", new ArrayList<>(Arrays.asList("p1", "p2", "p3")))
                                .put("required", new ArrayList<>(Arrays.asList("p1", "p2", "p3")))
                                .build())
                .put("return", MapBuilder.<String, Object>get().put("type", "string").build())
                .put("tag", "Customize-Workflow")
                .build();
    }

    @Test
    @DisplayName("过滤掉默认参数")
    void shouldReturnIncorrectResult() {
        Map<String, Object> schema = this.itemInfo.schema();
        assertThat(schema).containsEntry("group", "t1")
                .containsEntry("name", "test_schema_default_implementation_name")
                .containsEntry("index", "test_schema_index")
                .containsEntry("description", "This is a demo schema tool.")
                .containsEntry("parameters",
                        MapBuilder.<String, Object>get()
                                .put("type", "object")
                                .put("properties",
                                        MapBuilder.<String, Object>get()
                                                .put("p1",
                                                        MapBuilder.<String, Object>get()
                                                                .put("type", "string")
                                                                .put("description", "This is the first parameter.")
                                                                .build())
                                                .put("p3",
                                                        MapBuilder.<String, Object>get().put("type", "string").build())
                                                .build())
                                .put("required", new ArrayList<>(Arrays.asList("p1", "p3")))
                                .build())
                .containsEntry("return", MapBuilder.<String, Object>get().put("type", "string").build())
                .containsEntry("tag", "Customize-Workflow");
    }
}
