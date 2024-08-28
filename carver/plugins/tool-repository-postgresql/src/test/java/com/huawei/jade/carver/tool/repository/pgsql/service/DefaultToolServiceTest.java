/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.repository.pgsql.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.carver.tool.model.query.ToolQuery;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.repository.pgsql.ToolRepository;
import com.huawei.jade.carver.tool.repository.pgsql.model.entity.ToolDo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link DefaultToolService} 的单元测试。
 *
 * @author 鲁为
 * @since 2024-08-07
 */
@DisplayName("测试 DefaultToolService")
public class DefaultToolServiceTest {
    private final ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null);

    @InjectMocks
    private DefaultToolService toolService;

    @Mock
    private ToolRepository toolRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

    private ToolDo mockToolDo() {
        ToolDo toolDo = new ToolDo();
        Map<String, Object> schema = new HashMap<>();
        schema.put("name", "name");
        schema.put("description", "description");
        toolDo.setSchema(this.serializer.serialize(schema));
        toolDo.setCreator("creator");
        toolDo.setIcon("icon");
        toolDo.setModifier("modifier");
        toolDo.setUniqueName("testUniqueName");
        toolDo.setSource("source");
        toolDo.setVersion("version");
        toolDo.setName("name");
        return toolDo;
    }

    @Test
    @DisplayName("验证插入工具成功")
    void shouldSuccessWhenAddTool() {
        ToolData toolData = mockToolData();
        this.toolService.addTools(Collections.singletonList(toolData));
        verify(toolRepository).addTools(Mockito.anyList());
    }

    @Test
    @DisplayName("验证插入工具列表成功")
    void shouldSuccessWhenDeleteTool() {
        List<String> uniqueNames = new ArrayList<>();
        this.toolService.deleteTools(uniqueNames);
        verify(toolRepository).deleteTools(uniqueNames);
    }

    @Test
    @DisplayName("验证获取工具成功")
    void shouldSuccessWhenGetTool() {
        String uniqueName = "testUniqueName";
        ToolDo toolDo = mockToolDo();
        Mockito.when(this.toolRepository.getTool(uniqueName))
            .thenReturn(Optional.of(ToolDo.do2Info(toolDo, this.serializer)));
        ToolData toolData = this.toolService.getTool(uniqueName);
        assertThat(toolData.getUniqueName()).isEqualTo(uniqueName);
    }

    @Test
    @DisplayName("验证精确获取工具列表成功")
    void shouldSuccessWhenGetTools() {
        ToolQuery toolQuery = new ToolQuery();
        toolQuery.setToolName("testToolName");
        toolQuery.setMode("or");
        toolQuery.setIncludeTags(new HashSet<>());
        toolQuery.setExcludeTags(new HashSet<>());
        toolQuery.setOffset(1);
        toolQuery.setLimit(10);
        String uniqueName = "testUniqueName";
        ToolDo toolDo = mockToolDo();
        Mockito.when(this.toolRepository.getTool(uniqueName))
            .thenReturn(Optional.of(ToolDo.do2Info(toolDo, this.serializer)));
        Mockito.when(this.toolRepository.getTools(toolQuery))
            .thenReturn(Collections.singletonList(ToolDo.do2Info(toolDo, this.serializer)));
        assertThat(this.toolService.getTools(toolQuery).getData().get(0).getUniqueName()).isEqualTo(uniqueName);
    }

    @Test
    @DisplayName("验证模糊查询工具列表成功")
    void shouldSuccessWhenSearchTools() {
        ToolQuery toolQuery = new ToolQuery();
        toolQuery.setToolName("testToolName");
        toolQuery.setMode("or");
        toolQuery.setIncludeTags(new HashSet<>());
        toolQuery.setExcludeTags(new HashSet<>());
        toolQuery.setOffset(1);
        toolQuery.setLimit(10);
        String uniqueName = "testUniqueName";
        ToolDo toolDo = mockToolDo();
        Mockito.when(this.toolRepository.searchTools(toolQuery))
            .thenReturn(Collections.singletonList(ToolDo.do2Info(toolDo, this.serializer)));
        Mockito.when(this.toolRepository.searchToolsCount(toolQuery)).thenReturn(1);
        Mockito.when(this.toolRepository.getTags(Mockito.anyString())).thenReturn(new HashSet<>());
        assertThat(this.toolService.searchTools(toolQuery).getData().get(0).getUniqueName()).isEqualTo(uniqueName);
    }

    @Test
    @DisplayName("验证获取某一版本工具成功")
    void shouldSuccessWhenGetToolByVersion() {
        String uniqueName = "testUniqueName";
        String version = "version";
        ToolDo toolDo = mockToolDo();
        Mockito.when(this.toolRepository.getToolByVersion(uniqueName, version))
            .thenReturn(Optional.of(ToolDo.do2Info(toolDo, this.serializer)));
        assertThat(this.toolService.getToolByVersion(uniqueName, version).getVersion()).isEqualTo(version);
    }

    @Test
    @DisplayName("验证获取所有版本工具成功")
    void shouldSuccessWhenGetAllToolVersions() {
        ToolQuery toolQuery = new ToolQuery();
        toolQuery.setToolName("testToolName");
        String uniqueName = "testUniqueName";
        ToolDo toolDo = mockToolDo();
        Mockito.when(this.toolRepository.getAllToolVersions(toolQuery))
            .thenReturn(Collections.singletonList(ToolDo.do2Info(toolDo, this.serializer)));
        assertThat(this.toolService.getAllToolVersions(toolQuery).getData().get(0).getUniqueName()).isEqualTo(
            uniqueName);
    }
}
