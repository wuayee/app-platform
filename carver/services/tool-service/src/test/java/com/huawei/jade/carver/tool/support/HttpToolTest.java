/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.support;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.okhttp.OkHttpClassicClientFactory;
import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fit.value.fastjson.FastJsonValueHandler;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.value.ValueFetcher;
import com.huawei.jade.carver.tool.Tool;
import com.huawei.jade.carver.tool.ToolFactory;
import com.huawei.jade.carver.tool.support.entity.Address;
import com.huawei.jade.carver.tool.support.entity.Education;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link HttpTool} 的单元测试。
 *
 * @author 何天放
 * @since 2024-06-12
 */
public class HttpToolTest {
    private static final String TOOL_PATH = "tool/";
    private static Integer port = null;

    private static int getLocalAvailablePort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException("Get local available port failed.", e);
        }
    }

    @BeforeAll
    static void setupAll() {
        if (port == null) {
            port = getLocalAvailablePort();
        }
        TestFitRuntime.INSTANCE.start(port);
    }

    @AfterAll
    static void teardownAll() {
        TestFitRuntime.INSTANCE.stop();
    }

    private static Tool.Info readToolInfo(String fileName) {
        URL resource = HttpToolTest.class.getClassLoader().getResource(TOOL_PATH + fileName);
        String actualFile = "test/resources/tool/" + fileName;
        if (resource == null) {
            throw new IllegalStateException(StringUtils.format("No tool info file. [file={0}]", actualFile));
        }
        String httpToolInfo;
        try (InputStream in = resource.openStream()) {
            httpToolInfo = IoUtils.content(in);
        } catch (IOException exception) {
            throw new IllegalStateException(StringUtils.format("Failed to read tool info file. [file={0}]", actualFile),
                    exception);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map;
        try {
            map = objectMapper.readValue(httpToolInfo, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(StringUtils.format("Failed to covert tool info file to map. [file={0}]",
                    actualFile), exception);
        }
        Map<String, Object> runnables = cast(map.get("runnables"));
        Map<String, Object> httpRunnable = cast(runnables.get("HTTP"));
        String domain = cast(httpRunnable.get("domain"));
        httpRunnable.put("domain", domain.replace("{port}", port.toString()));
        runnables.put("HTTP", httpRunnable);
        return Tool.Info.custom()
                .name(cast(map.getOrDefault("name", "")))
                .uniqueName(cast(map.getOrDefault("uniqueName", "")))
                .tags(new HashSet<>(cast(map.getOrDefault("tags", new ArrayList<>()))))
                .schema(cast(map.getOrDefault("schema", new HashMap<>())))
                .runnables(runnables)
                .build();
    }

    private static Tool createTool(Tool.Info info) {
        ObjectSerializer jsonSerializer = new JacksonObjectSerializer(null, null, null);
        Map<String, ObjectSerializer> serializers =
                MapBuilder.<String, ObjectSerializer>get().put("json", jsonSerializer).build();
        ValueFetcher valueFetcher = new FastJsonValueHandler();
        HttpClassicClientFactory httpClassicClientFactory = new OkHttpClassicClientFactory(serializers, valueFetcher);
        ToolFactory factory = ToolFactory.http(httpClassicClientFactory, jsonSerializer, valueFetcher);
        Tool.Metadata toolMetadata = Tool.Metadata.fromSchema(info.schema());
        return factory.create(info, toolMetadata);
    }

    @Test
    @DisplayName("测试返回值为 Map 成功")
    void shouldReturnMap() {
        Tool.Info info = readToolInfo("map.json");
        Tool tool = createTool(info);

        Address address = Address.create("jiangsu", "suzhou", 3205);
        Education education = Education.create("QUST", "UCAS");
        Map<String, Object> result = cast(tool.execute("Alice",
                26,
                address,
                education,
                Stream.of("0123-4567-8888", "0123-4567-9999").collect(Collectors.toList())));
        Map<String, Object> addressResult = cast(result.get("address"));
        Map<String, Object> educationResult = cast(result.get("education"));
        List<String> phoneNumbers = cast(result.get("phoneNumbers"));
        assertThat(result.get("name")).isEqualTo("Alice");
        assertThat(result.get("age")).isEqualTo(26);
        assertThat(addressResult.get("province")).isEqualTo("jiangsu");
        assertThat(addressResult.get("city")).isEqualTo("suzhou");
        assertThat(addressResult.get("number")).isEqualTo(3205);
        assertThat(educationResult.get("bachelor")).isEqualTo("QUST");
        assertThat(educationResult.get("master")).isEqualTo("UCAS");
        assertThat(phoneNumbers).contains("0123-4567-8888", "0123-4567-9999");
    }

    @Test
    @DisplayName("测试返回值为 String 成功")
    void shouldReturnString() {
        Tool.Info info = readToolInfo("string.json");
        Tool tool = createTool(info);

        String result = cast(tool.execute(Stream.of("abc", "def", "ghi").collect(Collectors.toList())));
        assertThat(result).isEqualTo("abc,def,ghi");
    }

    @Test
    @DisplayName("测试返回值为 Integer 成功")
    void shouldReturnInteger() {
        Tool.Info info = readToolInfo("integer.json");
        Tool tool = createTool(info);

        Integer result = cast(tool.execute(Stream.of(1, 2, 3).collect(Collectors.toList())));
        assertThat(result).isEqualTo(6);
    }

    @Test
    @DisplayName("测试返回值为 Null 成功")
    void shouldReturnNull() {
        Tool.Info info = readToolInfo("void.json");
        Tool tool = createTool(info);

        String result = cast(tool.execute());
        assertThat(result).isEqualTo(null);
    }
}