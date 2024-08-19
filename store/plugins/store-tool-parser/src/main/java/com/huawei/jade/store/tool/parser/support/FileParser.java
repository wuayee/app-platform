/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.store.entity.transfer.PluginToolData;

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
 * @author 杭潇
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
    public static PluginToolData getPluginData(Map<String, Object> toolFile, String toolNames) {
        notNull(toolFile, "Tool metadata cannot be null.");
        notNull(toolNames, "The input tool names cannot be null.");

        Set<String> toolNamesSet = Arrays.stream(toolNames.trim().split(COMMA)).collect(Collectors.toSet());

        Map<String, Object> schemaNode = notNull(ObjectUtils.cast(toolFile.get(SCHEMA)), "Tool schema cannot be null.");
        PluginToolData pluginData = new PluginToolData();
        pluginData.setSchema(schemaNode);
        String methodName = validateSchemaStringField(schemaNode, NAME);
        if (!toolNamesSet.contains(methodName)) {
            return pluginData;
        }
        pluginData.setName(methodName);
        pluginData.setDescription(validateSchemaStringField(schemaNode, DESCRIPTION));
        Map<String, Object> runnables = notNull(ObjectUtils.cast(toolFile.get(RUNNABLES)),
            "Tool runnables cannot be null.");
        pluginData.setRunnables(runnables);
        List<String> tags;
        if (toolFile.get(TAGS) != null) {
            tags = notNull(ObjectUtils.cast(toolFile.get(TAGS)), "Tool tags cannot be null.");
        } else {
            Map<String, Object> extensions = notNull(ObjectUtils.cast(toolFile.get(EXTENSIONS)),
                "Tool extensions cannot be null.");
            tags = notNull(ObjectUtils.cast(extensions.get(TAGS)), "Tool tags cannot be null.");
        }
        pluginData.setTags(new HashSet<>(tags));
        return pluginData;
    }

    private static String validateSchemaStringField(Map<String, Object> schemaNode, String fieldName) {
        Object field = notNull(schemaNode.get(fieldName), "Tool schema field value cannot be null. [field={0}]",
            fieldName);
        if (field instanceof String) {
            return notBlank((String) field, "Tool schema field value cannot be blank. [field={0}]", fieldName);
        }
        throw new IllegalArgumentException(
            StringUtils.format("Failed to obtain the data in schema. [field={0}]", fieldName));
    }
}