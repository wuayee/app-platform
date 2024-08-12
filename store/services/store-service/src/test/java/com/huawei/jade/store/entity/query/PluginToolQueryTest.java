/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

/**
 * 表示 {@link PluginToolQuery} 的单元测试。
 *
 * @author 鲁为
 * @since 2024-08-10
 */
@DisplayName("测试 PluginToolQuery")
public class PluginToolQueryTest {
    @Test
    @DisplayName("用构建器构建插件工具查询类时，返回成功")
    void shouldSuccessWhenBuildPluginToolQuery() {
        PluginToolQuery pluginToolQuery = new PluginToolQuery.Builder()
                .toolName("toolName")
                .includeTags(new HashSet<>())
                .excludeTags(new HashSet<>())
                .mode("mode")
                .offset(1)
                .limit(10)
                .version("1.0.0")
                .isPublished(true)
                .owner("owner")
                .collector("collector")
                .build();
        assertThat(pluginToolQuery.getToolName()).isEqualTo("toolName");
    }
}
