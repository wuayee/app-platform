/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.repository.support;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import modelengine.jade.store.entity.query.PluginToolQuery;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.repository.pgsql.entity.PluginToolDo;
import modelengine.jade.store.repository.pgsql.mapper.PluginToolMapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示 {@link DefaultPluginRepository} 的单元测试。
 *
 * @author 鲁为
 * @since 2024-08-09
 */
@DisplayName("测试 DefaultPluginRepository")
public class DefaultPluginToolRepositoryTest {
    private DefaultPluginToolRepository pluginToolRepository;
    private PluginToolMapper pluginToolMapper;

    @BeforeEach
    void setUp() {
        this.pluginToolMapper = mock(PluginToolMapper.class);
        this.pluginToolRepository = new DefaultPluginToolRepository(this.pluginToolMapper);
    }

    @Test
    @DisplayName("添加插件工具时，返回正确结果")
    void shouldSuccessWhenAddPluginTool() {
        PluginToolData pluginToolData = new PluginToolData();
        this.pluginToolRepository.addPluginTool(pluginToolData);
        verify(this.pluginToolMapper).addPluginTool(PluginToolDo.fromPluginToolData(pluginToolData));
    }

    @Test
    @DisplayName("添加插件工具列表时，返回正确结果")
    void shouldSuccessWhenAddPluginTools() {
        PluginToolData pluginToolData = new PluginToolData();
        List<PluginToolData> pluginToolDataList = new ArrayList<>();
        pluginToolDataList.add(pluginToolData);
        this.pluginToolRepository.addPluginTools(pluginToolDataList);
        List<PluginToolDo> pluginToolDoList =
                pluginToolDataList.stream().map(PluginToolDo::fromPluginToolData).collect(Collectors.toList());
        verify(this.pluginToolMapper).addPluginTools(pluginToolDoList);
    }

    @Test
    @DisplayName("删除插件工具时，返回正确结果")
    void shouldSuccessWhenDeletePluginTool() {
        String uniqueName = "testUniqueName";
        this.pluginToolRepository.deletePluginTool(uniqueName);
        verify(this.pluginToolMapper).deletePluginTool(uniqueName);
    }

    @Test
    @DisplayName("根据动态条件查询插件工具列表时，返回正确结果")
    void shouldSuccessWhenGetPluginTools() {
        PluginToolQuery pluginToolQuery = new PluginToolQuery();
        List<PluginToolDo> pluginToolDoList = new ArrayList<>();
        Mockito.when(this.pluginToolMapper.getPluginTools(pluginToolQuery)).thenReturn(pluginToolDoList);
        assertThat(this.pluginToolRepository.getPluginTools(pluginToolQuery)).isEqualTo(pluginToolDoList);
    }

    @Test
    @DisplayName("根据插件工具唯一标识列表查询插件工具列表时，返回正确结果")
    void shouldSuccessWhenGetPluginToolsByUniqueNames() {
        List<String> uniqueNames = Collections.singletonList("uniqueName");
        List<PluginToolDo> pluginToolDoList = new ArrayList<>();
        Mockito.when(this.pluginToolMapper.getPluginToolsByUniqueNames(uniqueNames)).thenReturn(pluginToolDoList);
        assertThat(this.pluginToolRepository.getPluginTools(uniqueNames)).isEqualTo(pluginToolDoList);
    }

    @Test
    @DisplayName("根据插件唯一标识查询插件工具列表时，返回正确结果")
    void shouldSuccessWhenGetPluginToolsByPluginId() {
        String pluginId = "testPluginId";
        List<PluginToolDo> pluginToolDoList = new ArrayList<>();
        Mockito.when(this.pluginToolMapper.getPluginToolsByPluginId(pluginId)).thenReturn(pluginToolDoList);
        assertThat(this.pluginToolRepository.getPluginTools(pluginId)).isEqualTo(pluginToolDoList);
    }

    @Test
    @DisplayName("根据动态条件查询插件工具列表元素个数时，返回正确结果")
    void shouldSuccessWhenGetPluginToolsCount() {
        PluginToolQuery pluginToolQuery = new PluginToolQuery();
        Mockito.when(this.pluginToolMapper.getPluginToolsCount(pluginToolQuery)).thenReturn(1);
        assertThat(this.pluginToolRepository.getPluginToolsCount(pluginToolQuery)).isEqualTo(1);
    }

    @Test
    @DisplayName("根据工具唯一标识查询插件工具时，返回正确结果")
    void shouldSuccessWhenGetPluginToolByUniqueName() {
        String toolUniqueName = "testUniqueName";
        PluginToolDo pluginToolDo = new PluginToolDo();
        Mockito.when(this.pluginToolMapper.getPluginToolByUniqueName(toolUniqueName)).thenReturn(pluginToolDo);
        assertThat(this.pluginToolRepository.getPluginToolByUniqueName(toolUniqueName)).isEqualTo(pluginToolDo);
    }

    @Test
    @DisplayName("根据插件工具唯一标识列表检查插件工具时，返回成功")
    void shouldSuccessWhenCheckPluginTools() {
        List<String> uniqueNames = Collections.singletonList("testUniqueName");
        PluginToolDo pluginToolDo = new PluginToolDo();
        Mockito.when(this.pluginToolMapper.getPluginToolByUniqueName(uniqueNames.get(0))).thenReturn(pluginToolDo);
        Assertions.assertThat(this.pluginToolRepository.hasPluginTools(uniqueNames).get(0)).isEqualTo(true);
    }
}
