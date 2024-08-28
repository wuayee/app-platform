/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.transfer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link PluginData} 的测试类。
 *
 * @author 鲁为
 * @since 2024-08-22
 */
@DisplayName("测试 PluginData")
public class PluginDataTest {
    @Test
    @DisplayName("用构建器构建插件工具传输类时，返回成功")
    void shouldSuccessWhenBuildPluginData() {
        PluginData pluginData = new PluginData();
        pluginData.setBuiltin(true);
        assertThat(pluginData.getBuiltin()).isEqualTo(true);
    }
}
