/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.Mock;
import com.huawei.fitframework.test.annotation.MvcTest;
import com.huawei.fitframework.test.domain.mvc.MockMvc;
import com.huawei.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import com.huawei.fitframework.test.domain.mvc.request.MockRequestBuilder;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.common.Result;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.entity.transfer.PluginToolData;
import com.huawei.jade.store.service.PluginService;
import com.huawei.jade.store.service.support.DeployStatus;
import com.huawei.jade.store.tool.parser.param.DeployParam;
import com.huawei.jade.store.tool.parser.service.PluginDeployService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 表示 {@link UploadPluginController} 的单元测试。
 *
 * @since 2024-08-07
 */
@MvcTest(classes = {UploadPluginController.class})
@DisplayName("测试 PluginController")
class UploadPluginControllerTest {
    @Mock
    private PluginService mockPluginService;

    @Mock
    private PluginDeployService pluginDeployService;

    @Fit
    private MockMvc mockMvc;

    private HttpClassicClientResponse<?> response;

    private UploadPluginController uploadPluginControllerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        uploadPluginControllerUnderTest = new UploadPluginController(pluginDeployService);
    }

    @Test
    @DisplayName("删除插件成功")
    void testDeletePlugin() {
        final PluginData pluginData = new PluginData();
        pluginData.setPluginId("pluginId");
        pluginData.setPluginName("pluginName");
        Map<String, Object> extension = new HashMap<>();
        extension.put("pluginFullName", "pluginFullName");
        extension.put("type", "java");
        pluginData.setExtension(extension);
        final PluginToolData pluginToolData = new PluginToolData();
        pluginToolData.setDescription("description");
        pluginToolData.setSchema(new HashMap<>());
        pluginToolData.setRunnables(new HashMap<>());
        pluginToolData.setTags(new HashSet<>(Arrays.asList("value")));
        pluginToolData.setName("name");
        pluginData.setPluginToolDataList(Arrays.asList(pluginToolData));
        when(mockPluginService.getPlugin("pluginId")).thenReturn(pluginData);
        when(mockPluginService.deletePlugin("pluginId")).thenReturn("result");
        final Result<String> result = uploadPluginControllerUnderTest.deletePlugin("pluginId");
        Assertions.assertEquals(result.getCode(), 0);
    }

    @Test
    @DisplayName("包为空添加插件失败")
    void shouldReturnErrorWhenAddEmptyPlugin() {
        String pluginId = "testPluginId";
        PluginData pluginData = new PluginData();
        pluginData.setPluginId(pluginId);
        pluginData.setPluginToolDataList(new ArrayList<>());
        Mockito.when(this.mockPluginService.addPlugin(any())).thenReturn(pluginId);
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/plugins/save")
            .param("toolNames", "testToolName")
            .responseType(TypeUtils.parameterized(Result.class, new Type[] {String.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("下发部署插件成功")
    void shouldReturnOkWhenDeployPlugin() {
        DeployParam deployParam = new DeployParam();
        deployParam.setPluginIds(Collections.singletonList("ids"));
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/plugins/deploy")
            .jsonEntity(deployParam)
            .responseType(TypeUtils.parameterized(Result.class, new Type[] {String.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("查询部署中插件成功")
    void shouldReturnOkWhenQueryDeployingPlugins() {
        String pluginId = "testPluginId";
        PluginData pluginData = new PluginData();
        pluginData.setPluginId(pluginId);
        pluginData.setPluginToolDataList(new ArrayList<>());
        when(this.pluginDeployService.queryPluginsByDeployStatus(any(DeployStatus.class))).thenReturn(
            Collections.singletonList(pluginData));
        when(this.pluginDeployService.queryCountByDeployStatus(any(DeployStatus.class))).thenReturn(1);
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/plugins/by-status/deploying")
            .responseType(TypeUtils.parameterized(Result.class, new Type[] {String.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }
}