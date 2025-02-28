/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.entity.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

/**
 * 表示 {@link PluginQuery} 的测试类。
 *
 * @author 鲁为
 * @since 2024-08-22
 */
@DisplayName("测试 PluginQuery")
public class PluginQueryTest {
    @Test
    @DisplayName("用构建器构建插件查询类时，返回成功")
    void shouldSuccessWhenBuildPluginQuery() {
        PluginQuery pluginQuery = new PluginQuery.Builder()
                .toolName("toolName")
                .includeTags(new HashSet<>())
                .excludeTags(new HashSet<>())
                .mode("mode")
                .offset(1)
                .limit(10)
                .isBuiltin(true)
                .build();
        assertThat(pluginQuery.getToolName()).isEqualTo("toolName");
        assertThat(pluginQuery.getIsBuiltin()).isEqualTo(true);
    }
}
