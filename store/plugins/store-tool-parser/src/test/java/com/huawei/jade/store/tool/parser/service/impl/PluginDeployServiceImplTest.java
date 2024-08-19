/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.service.impl;

import static com.huawei.fitframework.util.ObjectUtils.cast;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.HttpMessage;
import com.huawei.fit.http.entity.Entity;
import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.http.entity.NamedEntity;
import com.huawei.fit.http.entity.support.DefaultNamedEntity;
import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fit.service.RegistryService;
import com.huawei.fit.service.entity.ApplicationInstance;
import com.huawei.fit.service.entity.FitableAddressInstance;
import com.huawei.fit.service.entity.FitableInfo;
import com.huawei.fitframework.parameterization.StringFormatException;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.FileUtils;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.entity.transfer.PluginToolData;
import com.huawei.jade.store.service.PluginService;
import com.huawei.jade.store.service.PluginToolService;
import com.huawei.jade.store.service.support.DeployStatus;
import com.huawei.jade.store.tool.parser.config.PluginDeployQueryConfig;
import com.huawei.jade.store.tool.parser.config.RegistryQueryPoolConfig;
import com.huawei.jade.store.tool.parser.exception.PluginDeployException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link PluginDeployServiceImpl} 的单元测试。
 *
 * @author 罗帅
 * @since 2024-8-13
 */
@ExtendWith(MockitoExtension.class)
class PluginDeployServiceImplTest {
    private final ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null);

    @Mock
    private PluginService mockPluginService;

    @Mock
    private RegistryService mockRegistryService;

    @Mock
    private PluginToolService mockPluginToolService;

    private PluginDeployServiceImpl pluginDeployServiceImplUnderTest;

    @BeforeEach
    void setUp() {
        PluginDeployQueryConfig pluginDeployQueryConfig = new PluginDeployQueryConfig();
        pluginDeployQueryConfig.setInterval(10);
        pluginDeployQueryConfig.setTimeout(60);
        RegistryQueryPoolConfig registryQueryPoolConfig = new RegistryQueryPoolConfig();
        registryQueryPoolConfig.setCorePoolSize(20);
        registryQueryPoolConfig.setMaximumPoolSize(20);
        registryQueryPoolConfig.setWorkQueueCapacity(10);
        pluginDeployServiceImplUnderTest = new PluginDeployServiceImpl(mockPluginService, serializer,
            mockRegistryService, mockPluginToolService, pluginDeployQueryConfig, registryQueryPoolConfig);
    }

    @AfterEach
    void teardown() {
        Path targetFile = Paths.get("src/test/resources/tool.tar");
        if (Files.exists(targetFile)) {
            FileUtils.delete(targetFile.toFile());
        }
        Path testFile = Paths.get("src/test/resources/test/");
        if (Files.exists(testFile)) {
            FileUtils.delete(testFile.toFile());
        }
    }

    private PluginData mockPluginData() {
        final PluginData pluginData = new PluginData();
        pluginData.setPluginId("pluginId");
        pluginData.setPluginName("pluginName");
        Map<String, Object> extension = new HashMap<>();
        extension.put("pluginFullName", "pluginFullName.zip");
        extension.put("type", "python");
        extension.put("name", "name");
        extension.put("pluginName", "pluginName");
        pluginData.setExtension(extension);
        pluginData.setDeployStatus(DeployStatus.UNDEPLOYED.name());
        pluginData.setPluginToolDataList(Arrays.asList(mockPluginToolData()));
        return pluginData;
    }

    private PluginToolData mockPluginToolData() {
        final PluginToolData pluginToolData = new PluginToolData();
        pluginToolData.setDescription("description");
        pluginToolData.setSchema(new HashMap<>());
        Map<String, Object> runnables = new HashMap<>();
        Map<String, Object> fitables = new HashMap<>();
        fitables.put("fitableId", "multi_fid");
        fitables.put("genericableId", "multi_gid");
        runnables.put("FIT", fitables);
        pluginToolData.setRunnables(runnables);
        pluginToolData.setTags(new HashSet<>(Arrays.asList("value")));
        pluginToolData.setName("name");
        return pluginToolData;
    }

    @Test
    @DisplayName("部署文件符合预期")
    void testDeployPlugins() {
        when(mockPluginService.getPlugin(Mockito.anyString())).thenReturn(mockPluginData());
        when(mockPluginService.getPlugins(DeployStatus.DEPLOYED)).thenReturn(
            Collections.singletonList(mockPluginData()));
        pluginDeployServiceImplUnderTest.deployPlugins(Arrays.asList("value"));
        verify(mockPluginService).updateDeployStatus(Arrays.asList("value"), DeployStatus.DEPLOYING);
    }

    @Test
    @DisplayName("查询插件工具注册结果符合预期")
    void testQueryToolsRegisterResult()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PluginDeployServiceImpl.class.getDeclaredMethod("queryToolsRegisterResult", List.class);
        method.setAccessible(true);
        final FitableAddressInstance fitableAddressInstance = new FitableAddressInstance();
        final FitableInfo fitable = new FitableInfo();
        fitable.setGenericableId("genericableId");
        fitable.setGenericableVersion("genericableVersion");
        fitable.setFitableId("fitableId");
        fitableAddressInstance.setFitable(fitable);
        final ApplicationInstance applicationInstance = new ApplicationInstance();
        fitableAddressInstance.setApplicationInstances(Arrays.asList(applicationInstance));
        final List<FitableAddressInstance> fitableAddressInstances = Arrays.asList(fitableAddressInstance);
        final FitableInfo fitableInfo = new FitableInfo();
        fitableInfo.setGenericableId("genericableId");
        fitableInfo.setGenericableVersion("genericableVersion");
        fitableInfo.setFitableId("fitableId");
        fitableInfo.setFitableVersion("fitableVersion");
        final List<FitableInfo> fitables = Arrays.asList(fitableInfo);
        when(mockRegistryService.queryFitables(fitables, "")).thenReturn(fitableAddressInstances);
        Object result = method.invoke(pluginDeployServiceImplUnderTest, fitables);
        assertThat(result).isInstanceOf(Boolean.class);
        boolean resAfterCast = cast(result);
        assertThat(resAfterCast).isTrue();
    }

    @Test
    @DisplayName("获取 FIT 消息符合预期")
    void testGetFitableInfo() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PluginDeployServiceImpl.class.getDeclaredMethod("getFitableInfo", PluginToolData.class);
        method.setAccessible(true);
        Object fitableInfo = method.invoke(pluginDeployServiceImplUnderTest, mockPluginToolData());
        assertThat(fitableInfo).isInstanceOf(FitableInfo.class);
        FitableInfo infoAfterCast = cast(fitableInfo);
        assertThat(infoAfterCast.getFitableVersion()).isEqualTo("1.0.0");
    }

    @Test
    @DisplayName("保存元数据信息抛出异常符合预期")
    void testSaveMetaData() throws NoSuchMethodException {
        when(mockPluginService.getPlugin(Mockito.anyString())).thenReturn(mockPluginData());
        Method method = PluginDeployServiceImpl.class.getDeclaredMethod("saveMetadata", File.class, String.class,
            File.class, File.class);
        method.setAccessible(true);
        String toolsJsonFile = "src/test/resources/tools.json";
        String validationFile = "src/test/resources/plugin.json";
        String toolFile = "src/test/resources/tool.zip";
        String toolName = "toolName";
        assertThatThrownBy(
            () -> method.invoke(pluginDeployServiceImplUnderTest, Paths.get(toolsJsonFile).toFile(), toolName,
                Paths.get(validationFile).toFile(), Paths.get(toolFile).toFile())).isInstanceOf(
            InvocationTargetException.class);
    }

    @Test
    @DisplayName("插件信息为空时部署插件符合预期")
    void testDeployPlugins_PluginServiceGetPluginReturnsNull() {
        when(mockPluginService.getPlugin("pluginId")).thenReturn(null);
        assertThatThrownBy(() -> pluginDeployServiceImplUnderTest.deployPlugins(Arrays.asList("value"))).isInstanceOf(
            PotentialStubbingProblem.class);
    }

    @Test
    @DisplayName("部署插件时查询插件状态成功")
    void testDeployPlugins_PluginServiceGetPluginsReturnsNoItems() {
        when(mockPluginService.getPlugin(Mockito.anyString())).thenReturn(mockPluginData());
        when(mockPluginService.getPlugins(DeployStatus.DEPLOYED)).thenReturn(Collections.emptyList());
        pluginDeployServiceImplUnderTest.deployPlugins(Arrays.asList("value"));
        verify(mockPluginService).updateDeployStatus(Arrays.asList("value"), DeployStatus.DEPLOYING);
    }

    @Test
    @DisplayName("删除插件成功")
    void testDeletePlugin() {
        when(mockPluginService.getPlugin("pluginId")).thenReturn(mockPluginData());
        final int result = pluginDeployServiceImplUnderTest.deletePlugin("pluginId");
        assertThat(result).isEqualTo(1);
        verify(mockPluginService).deletePlugin("pluginId");
    }

    @Test
    @DisplayName("获取已部署插件数量成功")
    void testQueryCountByDeployStatus() {
        when(mockPluginService.getPluginsCount(DeployStatus.DEPLOYED)).thenReturn(0);
        final int result = pluginDeployServiceImplUnderTest.queryCountByDeployStatus(DeployStatus.DEPLOYED);
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("通过部署状态查询插件信息符合预期")
    void testQueryPluginsByDeployStatus() {
        final PluginData pluginData1 = new PluginData();
        pluginData1.setPluginId("pluginId");
        pluginData1.setPluginName("pluginName");
        pluginData1.setExtension(new HashMap<>());
        pluginData1.setDeployStatus("deployStatus");
        final PluginToolData pluginToolData = new PluginToolData();
        pluginToolData.setDescription("description");
        pluginToolData.setSchema(new HashMap<>());
        pluginToolData.setRunnables(new HashMap<>());
        pluginToolData.setTags(new HashSet<>(Arrays.asList("value")));
        pluginToolData.setName("name");
        pluginData1.setPluginToolDataList(Arrays.asList(pluginToolData));
        final List<PluginData> pluginData = Arrays.asList(pluginData1);
        when(mockPluginService.getPlugins(DeployStatus.DEPLOYED)).thenReturn(pluginData);
        final List<PluginData> result = pluginDeployServiceImplUnderTest.queryPluginsByDeployStatus(
            DeployStatus.DEPLOYED);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("查询无满足要求的插件时返回空列表")
    void testQueryPluginsByDeployStatus_PluginServiceReturnsNoItems() {
        when(mockPluginService.getPlugins(DeployStatus.DEPLOYED)).thenReturn(Collections.emptyList());
        final List<PluginData> result = pluginDeployServiceImplUnderTest.queryPluginsByDeployStatus(
            DeployStatus.DEPLOYED);
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("保存插件时抛出异常符合预期")
    void testStoreTemporaryFile() throws NoSuchMethodException {
        Method method = PluginDeployServiceImpl.class.getDeclaredMethod("storeTemporaryFile", String.class,
            FileEntity.class, File.class);
        method.setAccessible(true);
        String targetFile = "src/test/resources/tool.tar";
        String fileName = "fileName";
        assertThatThrownBy(() -> method.invoke(pluginDeployServiceImplUnderTest, fileName, mock(FileEntity.class),
            Paths.get(targetFile).toFile())).isInstanceOf(InvocationTargetException.class);
    }

    @Test
    @DisplayName("上传错误后缀文件时抛出预期异常")
    void testUploadPluginsWithWrongName() {
        HttpMessage httpMessage = mock(HttpMessage.class);
        Entity entity = FileEntity.createAttachment(httpMessage, "tool.jar", mock(InputStream.class), 100);
        NamedEntity namedEntity = new DefaultNamedEntity(httpMessage, "generic", entity);
        assertThatThrownBy(() -> pluginDeployServiceImplUnderTest.uploadPlugins(Collections.singletonList(namedEntity),
            "toolName")).isInstanceOf(PluginDeployException.class);
    }

    @Test
    @DisplayName("上传插件时完整性校验失败符合预期")
    void testUploadPluginsWithWrongFoolFile() throws IOException {
        HttpMessage httpMessage = mock(HttpMessage.class);
        try (InputStream inputStream = new ByteArrayInputStream(
            "TestOfDefaultClientResponse".getBytes(StandardCharsets.UTF_8))) {
            Entity entity = FileEntity.createAttachment(httpMessage, "tool.zip", inputStream, 1);
            NamedEntity namedEntity = new DefaultNamedEntity(httpMessage, "generic", entity);
            assertThatThrownBy(
                () -> pluginDeployServiceImplUnderTest.uploadPlugins(Collections.singletonList(namedEntity),
                    "toolName")).isInstanceOf(StringFormatException.class);
        }
    }

    @Test
    @DisplayName("保存插件抛出异常符合预期")
    void testSavePlugin() throws IOException, NoSuchMethodException {
        Method method = PluginDeployServiceImpl.class.getDeclaredMethod("savePlugin", File.class, String.class);
        method.setAccessible(true);
        String targetFile = "src/test/resources/test";
        Path filePath = Paths.get(targetFile);
        FileUtils.ensureDirectory(filePath.toFile());
        File.createTempFile("tool", ".jar", filePath.toFile());
        Path tooFile = Paths.get(filePath.toFile().getParentFile().getPath(), "tools.json");
        Path pluginJsonFile = Paths.get(filePath.toFile().getParentFile().getPath(), "plugin.json");
        Files.copy(tooFile, filePath.resolve("tools.json"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(pluginJsonFile, filePath.resolve("plugin.json"), StandardCopyOption.REPLACE_EXISTING);
        assertThatThrownBy(() -> method.invoke(pluginDeployServiceImplUnderTest, filePath.toFile(),
            "add list,add itself")).isInstanceOf(InvocationTargetException.class);
    }
}