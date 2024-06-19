/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.utils;

import com.huawei.jade.store.entity.parser.MethodEntity;
import com.huawei.jade.store.entity.parser.ParameterEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.maven.surefire.shared.compress.archivers.tar.TarArchiveEntry;
import org.apache.maven.surefire.shared.compress.archivers.tar.TarArchiveInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

    private static List<String> toolFileParser(String filePath) throws IOException {
        if (filePath.endsWith(".jar")) {
            return parseJarFile(filePath);
        } else if (filePath.endsWith(".zip")) {
            return parseZipFile(filePath);
        } else if (filePath.endsWith(".tar")) {
            return parseTarFile(filePath);
        } else {
            throw new IllegalArgumentException("The given file could not be parsed");
        }
    }

    /**
     * 根据工具文件路径解析工具 schema 信息。
     *
     * @param filePath 待解析的压缩文件路径的 {@link String}。
     * @return 解析后的数据的 {@link List}{@code <}{@link MethodEntity}{@code >}。
     * @throws IOException 解析失败时抛出的异常。
     */
    public static List<MethodEntity> parseToolsJsonSchema(String filePath) throws IOException {
        List<MethodEntity> methodEntities = new ArrayList<MethodEntity>();
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> toolInfo = toolFileParser(filePath);
        for (String toolJsonString : toolInfo) {
            JsonNode toolNode = objectMapper.readTree(toolJsonString);
            JsonNode schemaNode = toolNode.path(SCHEMA);

            String methodName = schemaNode.path("name").asText();
            String methodDescription = schemaNode.path("description").asText();
            String returnDescription = schemaNode.path("return").path("description").asText();
            Object returnType = resolveParamType(schemaNode.path("return"));

            List<ParameterEntity> parameterEntities = parseParameters(schemaNode.path("parameters"));

            MethodEntity methodEntity = new MethodEntity(methodName, methodDescription);
            methodEntity.setReturnDescription(returnDescription);
            methodEntity.setReturnType(returnType);
            methodEntity.getParameterEntities().addAll(parameterEntities);

            methodEntities.add(methodEntity);
        }
        return methodEntities;
    }

    private static List<ParameterEntity> parseParameters(JsonNode parametersNode) {
        List<ParameterEntity> parameterEntities = new ArrayList<ParameterEntity>();
        JsonNode properties = parametersNode.path("properties");
        if (!properties.isMissingNode()) {
            properties.fields().forEachRemaining(entry -> {
                String paramName = entry.getKey();
                JsonNode paramNode = entry.getValue();

                String paramDescription = paramNode.path("description").asText();
                Object paramType = resolveParamType(paramNode);
                ParameterEntity parameterEntity = new ParameterEntity(paramName, paramType, paramDescription);
                parameterEntities.add(parameterEntity);
            });
        }
        return parameterEntities;
    }

    private static Object resolveParamType(JsonNode typeNode) {
        if ("array".equals(typeNode.path("type").asText())) {
            JsonNode itemsNode = typeNode.path("items");
            return itemsNode.path("type").asText() + "[]";
        }
        return typeNode.path("type").asText();
    }

    private static List<String> parseJarFile(String filePath) throws IOException {
        try (JarFile jarFile = new JarFile(filePath)) {
            JarEntry xmlEntry = jarFile.getJarEntry(JSON_FILE_PATH);
            if (xmlEntry == null) {
                throw new FileNotFoundException("json file not found in the JAR");
            }

            try (InputStream xmlInputStream = jarFile.getInputStream(xmlEntry)) {
                return parseJson(xmlInputStream);
            }
        }
    }

    private static List<String> parseZipFile(String filePath) throws IOException {
        try (ZipFile zipFile = new ZipFile(filePath)) {
            ZipEntry xmlEntry = zipFile.getEntry(JSON_FILE_PATH);
            if (xmlEntry == null) {
                throw new FileNotFoundException("json file not found in the ZIP");
            }

            try (InputStream xmlInputStream = zipFile.getInputStream(xmlEntry)) {
                return parseJson(xmlInputStream);
            }
        }
    }

    private static List<String> parseTarFile(String filePath) throws IOException {
        try (InputStream fileInputStream = Files.newInputStream(new File(filePath).toPath());
             TarArchiveInputStream tarInputStream = new TarArchiveInputStream(fileInputStream)) {
            TarArchiveEntry entry;
            while ((entry = tarInputStream.getNextTarEntry()) != null) {
                if (Objects.equals(entry.getName(), JSON_FILE_PATH)) {
                    return parseJson(tarInputStream);
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<String> parseJson(InputStream jsonInputStream) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(jsonInputStream, StandardCharsets.UTF_8))) {
            StringBuilder jsonString = getJsonValue(reader);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString.toString());
            JsonNode toolsNode = rootNode.get("tools");
            List<String> jsonList = new ArrayList<String>();
            for (JsonNode toolNode : toolsNode) {
                String toolJsonString = objectMapper.writeValueAsString(toolNode);
                jsonList.add(toolJsonString);
            }
            return jsonList;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to parse JSON from input stream", e);
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