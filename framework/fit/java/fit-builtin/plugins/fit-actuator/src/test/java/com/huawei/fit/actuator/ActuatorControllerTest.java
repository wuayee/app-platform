/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.actuator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginCategory;
import com.huawei.fitframework.plugin.PluginMetadata;
import com.huawei.fitframework.runtime.FitRuntime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Map;

/**
 * 表示 {@link ActuatorController} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-07-05
 */
@DisplayName("测试 ActuatorController")
public class ActuatorControllerTest {
    private ActuatorController actuatorController;
    private FitRuntime fitRuntime;
    private BrokerClient brokerClient;

    @Test
    @DisplayName("返回正确的插件列表")
    void shouldReturnPlugins() {
        this.fitRuntime = mock(FitRuntime.class);
        this.brokerClient = mock(BrokerClient.class);
        this.actuatorController = new ActuatorController(this.fitRuntime, this.brokerClient);

        Plugin p1 = mock(Plugin.class);
        PluginMetadata m1 = mock(PluginMetadata.class);
        when(p1.metadata()).thenReturn(m1);
        when(m1.group()).thenReturn("g1");
        when(m1.name()).thenReturn("n1");
        when(m1.version()).thenReturn("1.0.1");
        when(m1.category()).thenReturn(PluginCategory.USER);
        when(m1.level()).thenReturn(1);
        Plugin p2 = mock(Plugin.class);
        PluginMetadata m2 = mock(PluginMetadata.class);
        when(p2.metadata()).thenReturn(m2);
        when(m2.group()).thenReturn("g2");
        when(m2.name()).thenReturn("n2");
        when(m2.version()).thenReturn("1.0.2");
        when(m2.category()).thenReturn(PluginCategory.SYSTEM);
        when(m2.level()).thenReturn(2);
        Mockito.when(this.fitRuntime.plugins()).thenReturn(Arrays.asList(p1, p2));

        Map<String, Object> plugins = this.actuatorController.getPlugins();
        assertThat(plugins).isNotEmpty().hasSize(2);
        assertThat(plugins.get("n1")).hasFieldOrPropertyWithValue("group", "g1")
                .hasFieldOrPropertyWithValue("name", "n1")
                .hasFieldOrPropertyWithValue("version", "1.0.1")
                .hasFieldOrPropertyWithValue("category", "user")
                .hasFieldOrPropertyWithValue("level", 1);
        assertThat(plugins.get("n2")).hasFieldOrPropertyWithValue("group", "g2")
                .hasFieldOrPropertyWithValue("name", "n2")
                .hasFieldOrPropertyWithValue("version", "1.0.2")
                .hasFieldOrPropertyWithValue("category", "system")
                .hasFieldOrPropertyWithValue("level", 2);
    }
}
