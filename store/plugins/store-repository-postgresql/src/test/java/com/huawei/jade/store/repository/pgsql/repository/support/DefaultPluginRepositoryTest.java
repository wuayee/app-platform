/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.repository.support;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.repository.pgsql.entity.PluginDo;
import com.huawei.jade.store.repository.pgsql.mapper.PluginMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * 表示 {@link DefaultPluginRepository} 的单元测试。
 *
 * @author 鲁为
 * @since 2024-08-09
 */
@DisplayName("测试 DefaultPluginRepository")
public class DefaultPluginRepositoryTest {
    private DefaultPluginRepository pluginRepository;
    private PluginMapper pluginMapper;

    private final ObjectSerializer serializer =
            new JacksonObjectSerializer(null, null, null);

    @BeforeEach
    void setUp() {
        this.pluginMapper = mock(PluginMapper.class);
        this.pluginRepository = new DefaultPluginRepository(this.pluginMapper, this.serializer);
    }

    @Test
    @DisplayName("添加插件时，返回正确结果")
    void shouldSuccessWhenAddPlugin() {
        PluginData pluginData = new PluginData();
        pluginData.setExtension(MapBuilder.<String, Object>get()
                .put("type", "java")
                .put("artifactId", "jade-demo-parent")
                .put("groupId", "store-demo-plugin")
                .put("version", "1.0.0")
                .build());
        String res = this.pluginRepository.addPlugin(pluginData);
        assertThat(res).isEqualTo(null);
    }

    @Test
    @DisplayName("删除插件时，返回正确结果")
    void shouldSuccessWhenDeletePlugin() {
        String pluginId = "testPluginId";
        this.pluginRepository.deletePlugin(pluginId);
        verify(this.pluginMapper).deletePlugin(pluginId);
    }

    @Test
    @DisplayName("查询插件时，返回正确结果")
    void shouldSuccessWhenGetPluginByPluginId() {
        String pluginId = "testPluginId";
        PluginDo pluginDo = new PluginDo();
        pluginDo.setPluginId(pluginId);
        Mockito.when(this.pluginMapper.getPluginByPluginId(pluginId)).thenReturn(pluginDo);
        assertThat(this.pluginRepository.getPluginByPluginId(pluginId).getPluginId()).isEqualTo(pluginId);
    }
}
