/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;
import modelengine.fitframework.util.TypeUtils;

import com.huawei.jade.carver.ListResult;
import com.huawei.jade.carver.tool.model.query.ToolQuery;
import com.huawei.jade.common.Result;
import com.huawei.jade.store.entity.query.PluginQuery;
import com.huawei.jade.store.entity.query.PluginToolQuery;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.entity.transfer.PluginToolData;
import com.huawei.jade.store.service.PluginService;
import com.huawei.jade.store.service.PluginToolService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link PluginController} 的单元测试。
 *
 * @author 鲁为
 * @since 2024-08-08
 */
@MvcTest(classes = {PluginController.class})
@DisplayName("测试 PluginController")
public class PluginControllerTest {
    ToolQuery toolQuery = new ToolQuery();

    @Fit
    private MockMvc mockMvc;

    @Mock
    private PluginToolService pluginToolService;

    @Mock
    private PluginService pluginService;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("当添加插件时，返回正确结果。")
    void shouldReturnOkWhenAddPlugin() {
        String pluginId = "testPluginId";
        PluginData pluginData = new PluginData();
        pluginData.setPluginId(pluginId);
        pluginData.setPluginToolDataList(new ArrayList<>());
        Mockito.when(this.pluginService.addPlugin(any())).thenReturn(pluginId);
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/store/plugins")
            .jsonEntity(pluginData)
            .responseType(TypeUtils.parameterized(Result.class, new Type[] {String.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("当添加插件工具时，返回正确结果。")
    void shouldReturnOkWhenAddPluginTool() {
        String uniqueName = "testUniqueName";
        PluginToolData pluginToolData = new PluginToolData();
        Map<String, Object> schema = new HashMap<>();
        schema.put("name", "testName");
        pluginToolData.setSchema(schema);
        Mockito.when(this.pluginToolService.addPluginTool(any())).thenReturn(uniqueName);
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/store/plugins/tools")
            .jsonEntity(pluginToolData)
            .responseType(TypeUtils.parameterized(Result.class, new Type[] {String.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("当查询插件工具时，返回正确结果。")
    void shouldReturnOkWhenGetPluginToolByUniqueName() {
        String uniqueName = "testUniqueName";
        PluginToolData pluginToolData = new PluginToolData();
        pluginToolData.setUniqueName(uniqueName);
        Mockito.when(this.pluginToolService.getPluginTool(anyString())).thenReturn(pluginToolData);
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/store/plugins/tools/testUniqueName")
            .responseType(TypeUtils.parameterized(Result.class, new Type[] {PluginToolData.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("当查询插件时，返回正确结果。")
    void shouldReturnOkWhenGetPluginByPluginId() {
        String pluginId = "testPluginId";
        PluginData pluginData = new PluginData();
        pluginData.setPluginId(pluginId);
        Mockito.when(this.pluginService.getPlugin(anyString())).thenReturn(pluginData);
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/store/plugins/testPluginId")
            .responseType(TypeUtils.parameterized(Result.class, new Type[] {PluginData.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("当根据动态查询条件模糊获取插件工具列表时，返回正确结果。")
    void shouldReturnOkWhenGetPluginTools() {
        PluginToolData pluginToolData = new PluginToolData();
        List<PluginToolData> pluginToolDataList = new ArrayList<>();
        ListResult<PluginToolData> pluginToolDataListResult = ListResult.create(pluginToolDataList, 1);
        Mockito.when(this.pluginToolService.getPluginTools(Mockito.any(PluginToolQuery.class)))
            .thenReturn(pluginToolDataListResult);
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/store/plugins/tools/search")
            .param("name", "testToolName")
            .param("mode", "or")
            .param("includeTags", "FIT")
            .param("excludeTags", "APP")
            .param("pageNum", "1")
            .param("pageSize", "10")
            .param("version", "version")
            .responseType(TypeUtils.parameterized(Result.class, new Type[] {List.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("当根据动态查询条件模糊获取插件工具列表时，返回正确结果。")
    void shouldReturnOkWhenGetPlugins() {
        PluginData pluginData = new PluginData();
        List<PluginData> pluginDataList = new ArrayList<>();
        ListResult<PluginData> pluginDataListResult = ListResult.create(pluginDataList, 1);
        Mockito.when(this.pluginService.getPlugins(Mockito.any(PluginQuery.class))).thenReturn(pluginDataListResult);
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/store/plugins/search")
            .param("mode", "or")
            .param("includeTags", "FIT")
            .param("excludeTags", "APP")
            .param("pageNum", "1")
            .param("pageSize", "10")
            .param("isBuiltin", "false")
            .responseType(TypeUtils.parameterized(Result.class, new Type[] {List.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }
}
