/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.service.support.DeployStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link PluginDo} 的单元测试。
 *
 * @author 鲁为
 * @since 2024-08-09
 */
@DisplayName("测试 PluginDo")
public class PluginDoTest {
    private final ObjectSerializer serializer =
            new JacksonObjectSerializer(null, null, null);

    @Test
    @DisplayName("从传输层实体类转换数据库类，返回正确结果")
    void shouldSuccessWhenFromPluginData() {
        PluginData pluginData = new PluginData();
        pluginData.setPluginId("testPluginId");
        pluginData.setPluginName("testPlugin");
        pluginData.setExtension(MapBuilder.<String, Object>get()
                .put("type", "java").build());
        pluginData.setDeployStatus("deployed");
        PluginDo pluginDo = PluginDo.fromPluginData(pluginData, this.serializer);
        assertThat(pluginDo.getPluginId()).isEqualTo("testPluginId");
        assertThat(pluginDo.getPluginName()).isEqualTo("testPlugin");
        assertThat(pluginDo.getExtension()).isEqualTo("{\"type\":\"java\"}");
        assertThat(pluginDo.getDeployStatus().toString()).isEqualTo("DEPLOYED");
    }

    @Test
    @DisplayName("从数据库类转换传输层实体类，返回正确结果")
    void shouldSuccessWhenToPluginData() {
        PluginDo pluginDo = new PluginDo();
        pluginDo.setPluginId("testPluginId");
        pluginDo.setPluginName("testPlugin");
        pluginDo.setExtension("{\"type\":\"java\"}");
        pluginDo.setDeployStatus(DeployStatus.DEPLOYED);
        pluginDo.setBuiltin(true);
        PluginData pluginData = PluginDo.toPluginData(pluginDo, this.serializer);
        assertThat(pluginData.getPluginId()).isEqualTo("testPluginId");
        assertThat(pluginData.getPluginName()).isEqualTo("testPlugin");
        assertThat(pluginData.getExtension()).isEqualTo(MapBuilder.<String, Object>get()
                .put("type", "java").build());
        assertThat(pluginData.getDeployStatus()).isEqualTo("DEPLOYED");
        assertThat(pluginData.getBuiltin()).isEqualTo(true);
    }
}
