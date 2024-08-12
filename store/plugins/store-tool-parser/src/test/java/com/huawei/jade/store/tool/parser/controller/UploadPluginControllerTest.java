/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.controller;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.test.annotation.Mock;
import com.huawei.fitframework.test.annotation.MvcTest;
import com.huawei.jade.common.Result;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.entity.transfer.PluginToolData;
import com.huawei.jade.store.service.PluginService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
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

    @Fit
    private ObjectSerializer mockSerializer;

    private UploadPluginController uploadPluginControllerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        uploadPluginControllerUnderTest = new UploadPluginController(mockPluginService, mockSerializer);
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
    @DisplayName("删除插件抛出预期异常")
    void testDeletePluginWhenNoExtensionThenThrowException() {
        final PluginData pluginData = new PluginData();
        pluginData.setPluginId("pluginId");
        pluginData.setPluginName("pluginName");
        pluginData.setExtension(new HashMap<>());
        final PluginToolData pluginToolData = new PluginToolData();
        pluginToolData.setDescription("description");
        pluginToolData.setSchema(new HashMap<>());
        pluginToolData.setRunnables(new HashMap<>());
        pluginToolData.setTags(new HashSet<>(Arrays.asList("value")));
        pluginToolData.setName("name");
        pluginData.setPluginToolDataList(Arrays.asList(pluginToolData));
        when(mockPluginService.getPlugin("pluginId")).thenReturn(pluginData);
        when(mockPluginService.deletePlugin("pluginId")).thenReturn("result");
        Assertions.assertThrows(IllegalStateException.class,
            () -> uploadPluginControllerUnderTest.deletePlugin("pluginId"));
    }
}