/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.store.entity.transfer.PluginToolData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link PluginToolDo} 的单元测试。
 *
 * @author 鲁为
 * @since 2024-08-09
 */
@DisplayName("测试 PluginToolDo")
public class PluginToolDoTest {
    @Test
    @DisplayName("从传输层实体类转换数据库类，返回正确结果")
    void shouldSuccessWhenFromPluginToolData() {
        PluginToolData pluginToolData = new PluginToolData();
        pluginToolData.setPluginId("testPluginId");
        PluginToolDo pluginToolDo = PluginToolDo.fromPluginToolData(pluginToolData);
        assertThat(pluginToolDo.getPluginId()).isEqualTo("testPluginId");
    }

    @Test
    @DisplayName("从数据库类转换传输层实体类，返回正确结果")
    void shouldSuccessWhenToPluginToolData() {
        PluginToolDo pluginToolDo = new PluginToolDo();
        pluginToolDo.setPluginId("testPluginId");
        ToolData toolData = new ToolData();
        PluginToolData pluginToolData = PluginToolDo.toPluginToolData(toolData, pluginToolDo);
        assertThat(pluginToolData.getPluginId()).isEqualTo("testPluginId");
    }
}
