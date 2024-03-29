/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.conf.ConfigLoadException;
import com.huawei.fitframework.conf.ConfigLoadingResult;
import com.huawei.fitframework.conf.YamlConfigLoader;
import com.huawei.fitframework.resource.Resource;
import com.huawei.fitframework.util.IoUtils;
import com.huawei.fitframework.util.StringUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

@DisplayName("测试 YamlConfigLoader 类")
class YamlConfigLoaderTest {
    private static final String RESOURCE_KEY = "test.yaml";

    private Resource resource;
    private InputStream resourceInputStream;

    @BeforeEach
    void setup() throws URISyntaxException, IOException {
        URL resourceUrl = YamlConfigLoaderTest.class.getClassLoader().getResource(RESOURCE_KEY);
        if (resourceUrl == null) {
            throw new IllegalStateException(StringUtils.format("Failed to lookup resource. [key={0}]", RESOURCE_KEY));
        }
        this.resourceInputStream = resourceUrl.openStream();
        this.resource = mock(Resource.class);
        when(this.resource.filename()).thenReturn(RESOURCE_KEY);
        when(this.resource.url()).thenReturn(resourceUrl);
        when(this.resource.read()).thenReturn(this.resourceInputStream);
    }

    @AfterEach
    void teardown() throws IOException {
        this.resourceInputStream.close();
    }

    @Test
    @DisplayName("当不是预期的扩展名时，加载失败")
    void shouldFailWhenExtensionIsUnexpected() {
        YamlConfigLoader loader = new YamlConfigLoader();
        when(resource.filename()).thenReturn("test.xml");
        ConfigLoadingResult result = loader.load(this.resource, null);
        assertFalse(result.loaded());
    }

    @Test
    @DisplayName("当是预期的扩展名时，加载成功")
    void shouldLoadConfigWhenExtensionIsExpected() {
        YamlConfigLoader loader = new YamlConfigLoader();
        ConfigLoadingResult result = loader.load(this.resource, null);
        assertTrue(result.loaded());
    }

    @Test
    @DisplayName("当配置不是一个对象时（如列表），抛出异常")
    void shouldThrowWhenConfigIsNotObject() throws IOException {
        YamlConfigLoader loader = new YamlConfigLoader();
        try (InputStream in = IoUtils.resource(YamlConfigLoaderTest.class.getClassLoader(), "list-config.yaml")) {
            when(this.resource.read()).thenReturn(in);
            ConfigLoadException exception =
                    assertThrows(ConfigLoadException.class, () -> loader.load(this.resource, null));
            String error = StringUtils.format("The content of config must be an object. [url={0}]", this.resource);
            assertEquals(error, exception.getMessage());
        }
    }

    @Test
    @DisplayName("当从资源的输入流中读取数据抛出 IO 异常时，抛出异常")
    void shouldThrowWhenIoExceptionOccursWhenReadFromInputStream() throws IOException {
        InputStream in = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException();
            }
        };
        when(this.resource.read()).thenReturn(in);
        YamlConfigLoader loader = new YamlConfigLoader();
        ConfigLoadException exception = assertThrows(ConfigLoadException.class, () -> loader.load(this.resource));
        String error = StringUtils.format("Failed to parse YAML from config resource. [url={0}]", this.resource);
        assertEquals(error, exception.getMessage());
    }

    @Test
    @DisplayName("应包含 YAML 的常规扩展名")
    void shouldContainYamlExtensions() {
        YamlConfigLoader loader = new YamlConfigLoader();
        Set<String> extensions = loader.extensions();
        assertEquals(2, extensions.size());
        assertTrue(extensions.contains(".yaml"));
        assertTrue(extensions.contains(".yml"));
    }
}
