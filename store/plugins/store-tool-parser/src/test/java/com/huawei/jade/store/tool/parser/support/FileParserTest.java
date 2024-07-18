/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.jade.store.entity.transfer.PluginData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 表示 {@link FileParser} 的单元测试。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-15
 */
@DisplayName("测试 FileParser 类")
public class FileParserTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    @DisplayName("解析 json 文件，匹配数据成功")
    void givenValidJsonFileThenGetPluginDataSuccessfully() throws IOException {
        Set<String> toolNames = new HashSet<>();
        toolNames.add("add itself");
        String sourceFolderPath = "src/test/resources/tools.json";
        Path filePath = Paths.get(sourceFolderPath);
        String jsonContent = new String(Files.readAllBytes(filePath));
        JsonNode jsonNode = OBJECT_MAPPER.readTree(jsonContent);
        JsonNode toolsNode = jsonNode.get("tools");
        List<PluginData> pluginsData = new ArrayList<>();
        for (JsonNode node : toolsNode) {
            PluginData pluginData = FileParser.getPluginData(node, toolNames);
            if (pluginData.getName() != null) {
                pluginsData.add(pluginData);
            }
        }
        assertThat(pluginsData.size()).isEqualTo(1);
        PluginData pluginData = pluginsData.get(0);
        assertThat(pluginData.getName()).isEqualTo("add itself");
        assertThat(pluginData.getDescription()).isEqualTo("This method adds two integers");
    }
}