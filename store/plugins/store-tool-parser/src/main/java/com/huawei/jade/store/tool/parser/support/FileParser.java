/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.support;

import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.store.entity.transfer.PluginData;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 根据文件路径解析文件。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-18
 */
public class FileParser {
    private static final String RUNNABLES = "runnables";
    private static final String SCHEMA = "schema";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String TAGS = "tags";
    private static final String COMMA = ",";
    private static final String EXTENSIONS = "extensions";

    /**
     * 表示基于工具名列表匹配工具数据。
     *
     * @param toolFile 给定的工具元数据的 {@link File}。
     * @param toolNames 给定的工具名列表的 {@link Set}{@code <}{@link String}{@code >}。
     * @return 匹配成功的工具数据。
     */
    public static PluginData getPluginData(Map<String, Object> toolFile, String toolNames) {
        Set<String> toolNamesSet = Arrays.stream(toolNames.trim().split(COMMA)).collect(Collectors.toSet());
        Map<String, Object> schemaNode = ObjectUtils.cast(toolFile.get(SCHEMA));
        Object name = schemaNode.get(NAME);
        PluginData pluginData = new PluginData();
        pluginData.setSchema(schemaNode);
        if (name instanceof String) {
            String methodName = (String) name;
            if (!toolNamesSet.contains(methodName)) {
                return new PluginData();
            }
            pluginData.setName(methodName);
        }

        Object methodDescription = schemaNode.get(DESCRIPTION);
        if (methodDescription instanceof String) {
            pluginData.setDescription((String) methodDescription);
        }

        Map<String, Object> runnables = ObjectUtils.cast(toolFile.get(RUNNABLES));
        pluginData.setRunnables(runnables);
        Map<String, Object> extensions = ObjectUtils.cast(toolFile.get(EXTENSIONS));
        List<String> tags = ObjectUtils.cast(extensions.get(TAGS));
        pluginData.setTags(new HashSet<>(tags));
        return pluginData;
    }
}