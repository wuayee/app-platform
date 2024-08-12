/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.service.ToolService;
import com.huawei.jade.store.entity.query.PluginToolQuery;
import com.huawei.jade.store.entity.transfer.PluginToolData;
import com.huawei.jade.store.repository.pgsql.entity.PluginToolDo;
import com.huawei.jade.store.repository.pgsql.entity.TagDo;
import com.huawei.jade.store.repository.pgsql.repository.PluginToolRepository;
import com.huawei.jade.store.repository.pgsql.repository.TagRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link DefaultPluginToolService} 的单元测试。
 *
 * @author 鲁为
 * @since 2024-08-10
 */
@DisplayName("测试 DefaultPluginToolService")
public class DefaultPluginToolServiceTest {
    private DefaultPluginToolService pluginToolService;

    private ToolService toolService;

    private PluginToolRepository pluginToolRepository;

    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        this.toolService = mock(ToolService.class);
        this.pluginToolRepository = mock(PluginToolRepository.class);
        this.tagRepository = mock(TagRepository.class);
        this.pluginToolService = new DefaultPluginToolService(this.toolService, this.pluginToolRepository,
            this.tagRepository);
    }

    @Test
    @DisplayName("添加插件工具时，返回成功")
    void shouldSuccessWhenAddPluginTool() {
        PluginToolData pluginToolData = new PluginToolData();
        String uniqueName = "testUniqueName";
        pluginToolData.setUniqueName(uniqueName);
        Mockito.when(this.toolService.addTool(pluginToolData)).thenReturn(uniqueName);
        assertThat(this.pluginToolService.addPluginTool(pluginToolData)).isNotEqualTo(uniqueName);
    }

    @Test
    @DisplayName("添加插件工具列表时，返回成功")
    void shouldSuccessWhenAddPluginTools() {
        PluginToolData pluginToolData = new PluginToolData();
        String uniqueName = "testUniqueName";
        pluginToolData.setUniqueName(uniqueName);
        List<PluginToolData> pluginToolDataList = new ArrayList<>();
        pluginToolDataList.add(pluginToolData);
        assertThat(this.pluginToolService.addPluginTools(pluginToolDataList).get(0)).isNotEqualTo(uniqueName);
    }

    @Test
    @DisplayName("根据动态条件查询插件工具列表时，返回成功")
    void shouldSuccessWhenGetPluginTools() {
        PluginToolQuery pluginToolQuery = new PluginToolQuery();
        pluginToolQuery.setLimit(10);
        pluginToolQuery.setOffset(1);
        pluginToolQuery.setIncludeTags(new HashSet<>());
        pluginToolQuery.setExcludeTags(new HashSet<>());
        List<PluginToolDo> dos = Collections.singletonList(mockPluginToolDo());
        Mockito.when(this.pluginToolRepository.getPluginTools(pluginToolQuery)).thenReturn(dos);
        Mockito.when(this.pluginToolRepository.getPluginToolsCount(pluginToolQuery)).thenReturn(0);
        Mockito.when(this.toolService.getTool(Mockito.anyString())).thenReturn(mockToolData());
        Mockito.when(this.tagRepository.getTags(Mockito.anyString()))
            .thenReturn(Collections.singletonList(new TagDo()));
        assertThat(this.pluginToolService.getPluginTools(pluginToolQuery).getCount()).isEqualTo(0);
    }

    private ToolData mockToolData() {
        ToolData toolData = new ToolData();
        Map<String, Object> schema = new HashMap<>();
        schema.put("name", "name");
        schema.put("description", "description");
        toolData.setSchema(schema);
        toolData.setCreator("creator");
        toolData.setIcon("icon");
        toolData.setLatest(true);
        toolData.setModifier("modifier");
        toolData.setTags(new HashSet<>(Collections.singleton("tag")));
        toolData.setRunnables(new HashMap<>());
        toolData.setUniqueName("unigueName");
        toolData.setSource("source");
        toolData.setVersion("version");
        return toolData;
    }

    private PluginToolDo mockPluginToolDo() {
        PluginToolDo pluginToolDo = new PluginToolDo();
        pluginToolDo.setToolUniqueName("testUniqueName");
        pluginToolDo.setLikeCount(0);
        pluginToolDo.setDownloadCount(0);
        pluginToolDo.setPluginId("testPluginId");
        pluginToolDo.setName("name");
        pluginToolDo.setCreator("creator");
        pluginToolDo.setCreatedTime("createTime");
        pluginToolDo.setId("id");
        pluginToolDo.setModifier("modifier");
        pluginToolDo.setUpdatedTime("updatedTime");
        return pluginToolDo;
    }

    @Test
    @DisplayName("根据插件唯一标识查询插件工具列表时，返回成功")
    void shouldSuccessWhenGetPluginToolsByPluginId() {
        String pluginId = "testPluginId";
        List<PluginToolDo> dos = new ArrayList<>();
        Mockito.when(this.pluginToolRepository.getPluginTools(pluginId)).thenReturn(dos);
        assertThat(this.pluginToolService.getPluginTools(pluginId)).isEqualTo(dos);
    }

    @Test
    @DisplayName("根据插件工具唯一标识查询插件工具时，返回成功")
    void shouldSuccessWhenGetPluginTool() {
        String uniqueName = "testUniqueName";
        PluginToolDo pluginToolDo = mockPluginToolDo();
        Mockito.when(this.pluginToolRepository.getPluginToolByUniqueName(uniqueName)).thenReturn(pluginToolDo);
        ToolData toolData = new ToolData();
        toolData.setCreator("testCreator");
        toolData.setModifier("testModifier");
        toolData.setName("testTool");
        toolData.setDescription("description");
        toolData.setUniqueName(uniqueName);
        Mockito.when(this.toolService.getTool(uniqueName)).thenReturn(toolData);
        Mockito.when(this.tagRepository.getTags(uniqueName)).thenReturn(Collections.singletonList(new TagDo()));
        assertThat(this.pluginToolService.getPluginTool(uniqueName).getToolUniqueName()).isEqualTo(null);
    }

    @Test
    @DisplayName("根据插件工具唯一标识删除插件工具时，返回成功")
    void shouldSuccessWhenDeletePluginTool() {
        String uniqueName = "testUniqueName";
        PluginToolDo pluginToolDo = new PluginToolDo();
        pluginToolDo.setToolUniqueName(uniqueName);
        pluginToolDo.setLikeCount(0);
        pluginToolDo.setDownloadCount(0);
        pluginToolDo.setPluginId("testPluginId");
        Mockito.when(this.pluginToolRepository.getPluginToolByUniqueName(uniqueName)).thenReturn(pluginToolDo);
        ToolData toolData = new ToolData();
        toolData.setCreator("testCreator");
        toolData.setModifier("testModifier");
        toolData.setName("testTool");
        toolData.setDescription("description");
        toolData.setUniqueName(uniqueName);
        Mockito.when(this.toolService.getTool(uniqueName)).thenReturn(toolData);
        Mockito.when(this.tagRepository.getTags(uniqueName)).thenReturn(Collections.singletonList(new TagDo()));
        PluginToolData pluginToolData = new PluginToolData();
        Mockito.when(this.toolService.deleteTool(uniqueName)).thenReturn(uniqueName);
        assertThat(this.pluginToolService.deletePluginTool(uniqueName)).isEqualTo(uniqueName);
    }
}
