/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.support;

import com.huawei.jade.store.entity.transfer.PluginData;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 根据文件路径解析文件。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-18
 */
public class FileParser {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String RUNNABLES = "runnables";
    private static final String SCHEMA = "schema";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";

    /**
     * 表示基于工具名列表匹配工具数据。
     *
     * @param toolNode 给定的工具元数据的 {@link JsonNode}。
     * @param toolNames 给定的工具名列表的 {@link Set}{@code <}{@link String}{@code >}。
     * @return 匹配成功的工具数据。
     * @throws IOException 表示解析数据失败时抛出异常。
     */
    public static PluginData getPluginData(JsonNode toolNode, Set<String> toolNames) throws IOException {
        JsonNode schemaNode = toolNode.path(SCHEMA);
        String methodName = schemaNode.path(NAME).asText();
        if (!toolNames.contains(methodName)) {
            return new PluginData();
        }
        String methodDescription = schemaNode.path(DESCRIPTION).asText();
        PluginData pluginData = new PluginData();
        pluginData.setName(methodName);
        pluginData.setDescription(methodDescription);
        pluginData.setRunnables(getToolInfo(toolNode, RUNNABLES));
        pluginData.setSchema(getToolInfo(toolNode, SCHEMA));

        JsonNode tagsInfo = toolNode.path("tags");
        Set<String> tags =
                StreamSupport.stream(tagsInfo.spliterator(), false).map(JsonNode::asText).collect(Collectors.toSet());
        pluginData.setTags(tags);
        return pluginData;
    }

    /**
     * 表示将 json 数据的对应字段的信息转换为 map 类型的数据。
     *
     * @param toolInfo 给定的工具元数据的 {@link JsonNode}。
     * @param fieldName 给定的字段名的 {@link String}。
     * @return 字段名下的数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @throws IOException 表示解析数据失败时抛出异常。
     */
    public static Map<String, Object> getToolInfo(JsonNode toolInfo, String fieldName) throws IOException {
        return OBJECT_MAPPER.readValue(toolInfo.path(fieldName).traverse(),
                new TypeReference<Map<String, Object>>() {});
    }
}