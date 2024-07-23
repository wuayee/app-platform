/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.store.entity.transfer.PluginData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link FileParser} 的单元测试。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-15
 */
@DisplayName("测试 FileParser 类")
public class FileParserTest {
    private List<Object> toolList = null;

    @BeforeEach
    void setup() throws IOException {
        String sourceFolderPath = "src/test/resources/tools.json";
        JacksonObjectSerializer jacksonObjectSerializer = new JacksonObjectSerializer(null, null, null);
        try (InputStream in = Files.newInputStream(Paths.get(sourceFolderPath))) {
            Map<String, Object> jsonData = jacksonObjectSerializer.deserialize(in, Map.class);
            this.toolList = ObjectUtils.cast(jsonData.get("tools"));
        }
    }

    @Test
    @DisplayName("解析 json 文件，匹配数据成功")
    void givenValidJsonFileThenGetPluginDataSuccessfully() {
        assert this.toolList != null;
        assertThat(this.toolList.size()).isEqualTo(3);
        Map<String, Object> tool = ObjectUtils.cast(this.toolList.get(0));
        Map<String, Object> schema = ObjectUtils.cast(tool.get("schema"));
        assertThat(schema.get("name")).isEqualTo("add list");
        assertThat(schema.get("description")).isEqualTo("This method adds two list");

        tool = ObjectUtils.cast(this.toolList.get(1));
        schema = ObjectUtils.cast(tool.get("schema"));
        assertThat(schema.get("name")).isEqualTo("add itself");
        assertThat(schema.get("description")).isEqualTo("This method adds two integers");

        tool = ObjectUtils.cast(this.toolList.get(2));
        schema = ObjectUtils.cast(tool.get("schema"));
        assertThat(schema.get("name")).isEqualTo("name for tool");
        assertThat(schema.get("description")).isEqualTo("description for tool.");
    }

    @Test
    @DisplayName("给定有效工具名，数据匹配成功")
    void givenValidToolNamesThenGetToolDataSuccessfully() {
        String validToolNames = "add list,add itself";
        List<PluginData> list = new ArrayList<>();
        for (Object toolInfo : this.toolList) {
            Map<String, Object> toolMap = ObjectUtils.cast(toolInfo);
            PluginData pluginData = FileParser.getPluginData(toolMap, validToolNames);
            if (pluginData.getName() != null) {
                list.add(pluginData);
            }
        }
        assertThat(list.size()).isEqualTo(2);
        PluginData pluginData = list.get(0);
        assertThat(pluginData.getName()).isEqualTo("add list");
        assertThat(pluginData.getDescription()).isEqualTo("This method adds two list");
        pluginData = list.get(1);
        assertThat(pluginData.getName()).isEqualTo("add itself");
        assertThat(pluginData.getDescription()).isEqualTo("This method adds two integers");
    }
}