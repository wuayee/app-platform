/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.utils;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.store.tool.parser.entity.MethodEntity;
import com.huawei.jade.store.tool.parser.entity.ParameterEntity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 根据文件路径解析文件。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-18
 */
public class ParseFileByPath {
    private static final String JSON_FILE_PATH = "FIT-INF/tools.json";
    private static final String SCHEMA = "schema";
    private static final String RUNNABLES = "runnables";
    private static final String JAR = ".jar";
    private static final String ZIP = ".zip";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String RETURN = "return";
    private static final String PARAMETERS = "parameters";
    private static final String PROPERTIES = "properties";
    private static final String TYPE = "type";
    private static final String ARRAY = "array";
    private static final String ITEMS = "items";
    private static final String SQUARE_BRACKETS = "[]";
    private static final String TOOLS = "tools";

    /**
     * 根据文件路径解析文件信息。
     *
     * @param filePath 给定文件路径的 {@link String}。
     * @return 根据文件解析出的数据列表的 {@link List}{@code <}{@link String}{@code >}。
     * @throws IOException 解析文件失败时抛出的异常。
     */
    private static List<String> toolFileParser(String filePath) throws IOException {
        if (filePath.endsWith(JAR)) {
            return parseJarFile(filePath);
        } else if (filePath.endsWith(ZIP)) {
            return parseZipFile(filePath);
        } else {
            throw new IllegalArgumentException(
                    StringUtils.format("The given file could not be parsed. [file={0}]", filePath));
        }
    }

    /**
     * 解析工具数据中的所有 schema 信息。
     *
     * @param toolInfo 表示工具信息的 {@link String}。
     * @return 解析后的数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @throws IOException 解析失败时抛出的异常。
     */
    private static Map<String, Object> getSchemaInfos(String toolInfo) throws IOException {
        return getToolInfo(toolInfo, SCHEMA);
    }

    /**
     * 解析工具数据中的 runnables 信息。
     *
     * @param toolInfo 表示工具的信息的 {@link String}。
     * @return 解析后的数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @throws IOException 解析失败时抛出的异常。
     */
    private static Map<String, Object> getRunnableInfos(String toolInfo) throws IOException {
        return getToolInfo(toolInfo, RUNNABLES);
    }

    private static Map<String, Object> getToolInfo(String toolInfo, String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode toolNode = objectMapper.readTree(toolInfo);
        JsonNode schemaNode = toolNode.path(fileName);
        return objectMapper.readValue(schemaNode.traverse(), new TypeReference<Map<String, Object>>() {});
    }

    /**
     * 根据工具文件路径解析工具 schema 信息。
     *
     * @param filePath 待解析的压缩文件路径的 {@link String}。
     * @return 解析后的数据的 {@link List}{@code <}{@link MethodEntity}{@code >}。
     * @throws IOException 解析失败时抛出的异常。
     */
    public static List<MethodEntity> parseToolSchema(String filePath) throws IOException {
        List<MethodEntity> methodEntities = new ArrayList<MethodEntity>();
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> toolInfos = toolFileParser(filePath);
        for (String toolInfo : toolInfos) {
            JsonNode toolNode = objectMapper.readTree(toolInfo);
            JsonNode schemaNode = toolNode.path(SCHEMA);

            String methodName = schemaNode.path(NAME).asText();
            String methodDescription = schemaNode.path(DESCRIPTION).asText();
            String returnDescription = schemaNode.path(RETURN).path(DESCRIPTION).asText();
            String returnType = resolveParamType(schemaNode.path(RETURN));

            List<ParameterEntity> parameterEntities = parseParameters(schemaNode.path(PARAMETERS));
            MethodEntity methodEntity = new MethodEntity();
            methodEntity.setMethodName(methodName);
            methodEntity.setMethodDescription(methodDescription);
            methodEntity.setReturnDescription(returnDescription);
            methodEntity.setReturnType(returnType);
            methodEntity.getParameterEntities().addAll(parameterEntities);

            Map<String, Object> runnableInfos = getRunnableInfos(toolInfo);
            methodEntity.setTags(runnableInfos.keySet());
            methodEntity.setRunnablesInfo(runnableInfos);
            methodEntity.setSchemaInfo(getSchemaInfos(toolInfo));
            methodEntity.setTargetFilePath(filePath);
            methodEntities.add(methodEntity);
        }
        return methodEntities;
    }

    private static List<ParameterEntity> parseParameters(JsonNode parametersNode) {
        List<ParameterEntity> parameterEntities = new ArrayList<ParameterEntity>();
        JsonNode properties = parametersNode.path(PROPERTIES);
        if (properties.isMissingNode()) {
            return parameterEntities;
        }
        properties.fields().forEachRemaining(entry -> {
            String paramName = entry.getKey();
            JsonNode paramNode = entry.getValue();

            String paramDescription = paramNode.path(DESCRIPTION).asText();
            String paramType = resolveParamType(paramNode);
            ParameterEntity parameterEntity = new ParameterEntity();
            parameterEntity.setType(paramType);
            parameterEntity.setName(paramName);
            parameterEntity.setDescription(paramDescription);
            parameterEntities.add(parameterEntity);
        });
        return parameterEntities;
    }

    private static String resolveParamType(JsonNode typeNode) {
        if (ARRAY.equals(typeNode.path(TYPE).asText())) {
            JsonNode itemsNode = typeNode.path(ITEMS);
            return itemsNode.path(TYPE).asText() + SQUARE_BRACKETS;
        }
        return typeNode.path(TYPE).asText();
    }

    private static List<String> parseJarFile(String filePath) throws IOException {
        try (JarFile jarFile = new JarFile(filePath)) {
            JarEntry jarEntry = jarFile.getJarEntry(JSON_FILE_PATH);
            notNull(jarEntry,
                    StringUtils.format("The json file not found in the given jar file. [file={0}]", filePath));

            try (InputStream xmlInputStream = jarFile.getInputStream(jarEntry)) {
                return parseJson(xmlInputStream);
            }
        }
    }

    private static List<String> parseZipFile(String filePath) throws IOException {
        try (ZipFile zipFile = new ZipFile(filePath)) {
            ZipEntry zipEntry = zipFile.getEntry(JSON_FILE_PATH);
            if (zipEntry == null) {
                throw new FileNotFoundException(
                        StringUtils.format("The json file not found in the given zip file. [file={0}]", filePath));
            }

            try (InputStream xmlInputStream = zipFile.getInputStream(zipEntry)) {
                return parseJson(xmlInputStream);
            }
        }
    }

    private static List<String> parseJson(InputStream jsonInputStream) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(jsonInputStream, StandardCharsets.UTF_8))) {
            StringBuilder jsonString = getJsonValue(reader);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString.toString());
            JsonNode toolsNode = rootNode.get(TOOLS);
            List<String> jsonList = new ArrayList<>();
            for (JsonNode toolNode : toolsNode) {
                String toolJsonString = objectMapper.writeValueAsString(toolNode);
                jsonList.add(toolJsonString);
            }
            return jsonList;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to parse json from input stream.", e);
        }
    }

    private static StringBuilder getJsonValue(BufferedReader reader) throws IOException {
        StringBuilder jsonString = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonString.append(line);
        }
        return jsonString;
    }
}