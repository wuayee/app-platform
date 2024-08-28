/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.transfer;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

/**
 * 表示 {@link PluginToolData} 的单元测试。
 *
 * @author 鲁为
 * @since 2024-08-10
 */
@DisplayName("测试 PluginToolData")
public class PluginToolDataTest {
    @Test
    @DisplayName("用构建器构建插件工具传输类时，返回成功")
    void shouldSuccessWhenBuildPluginToolData() {
        PluginToolData pluginToolData = new PluginToolData.Builder()
                .creator("creator")
                .modifier("modifier")
                .name("name")
                .description("description")
                .uniqueName("uniqueName")
                .schema(MapBuilder.<String, Object>get()
                        .put("properties", "a")
                        .build())
                .runnables(MapBuilder.<String, Object>get()
                        .put("Fit", "Fit")
                        .build())
                .source("Builtin")
                .icon("/path/to/icon")
                .tags(new HashSet<>())
                .version("1.0.0")
                .likeCount(0)
                .downloadCount(0)
                .toolUniqueName("toolUniqueName")
                .build();
        assertThat(pluginToolData.getToolUniqueName()).isEqualTo("toolUniqueName");
    }
}
