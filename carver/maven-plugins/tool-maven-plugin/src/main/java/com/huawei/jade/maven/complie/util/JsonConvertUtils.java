/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.maven.complie.util;

import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.maven.complie.entity.MethodEntity;
import com.huawei.jade.maven.complie.entity.ParameterEntity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 将 {@link MethodEntity} 数据转换为 Json 数据的工具类。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-19
 */
public class JsonConvertUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 将 {@link MethodEntity} 数据转换为 {@link Map} 类型的数据。
     *
     * @param methodEntity 给定的方法实体值的 {@link MethodEntity}。
     * @return 表示解析后的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> convertMethodEntityObjectMap(MethodEntity methodEntity) {
        MapBuilder<String, Object> mapBuilder = new MapBuilder<>();
        return mapBuilder.put("schema", getSchema(methodEntity))
                .put("runnables", getRunnables(methodEntity))
                .put("extensions", getTags(methodEntity))
                .build();
    }

    private static Map<String, Object> getSchema(MethodEntity methodEntity) {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("name", methodEntity.getMethodName());
        schema.put("description", methodEntity.getMethodDescription());

        Map<String, Object> parameters = getParameters(methodEntity);
        schema.put("parameters", parameters);
        Object properties = parameters.get("properties");
        if (properties instanceof Map) {
            Map<?, ?> mapProperties = (Map<?, ?>) properties;
            schema.put("order", mapProperties.keySet());
        }

        Map<String, Object> returnDetails = new HashMap<>();
        if (methodEntity.getReturnDescription() != null) {
            returnDetails.put("description", methodEntity.getReturnDescription());
        }
        returnDetails.putAll(convertToMap(methodEntity.getReturnType()));
        schema.put("return", returnDetails);
        return schema;
    }

    private static Map<String, Object> getRunnables(MethodEntity methodEntity) {
        Map<String, Object> runnables = new HashMap<>();
        Map<String, Object> fitInfo = new HashMap<>();
        fitInfo.put("fitableId", methodEntity.getFitableId());
        fitInfo.put("genericableId", methodEntity.getGenericableId());
        runnables.put("FIT", fitInfo);
        return runnables;
    }

    private static Map<String, Object> getTags(MethodEntity methodEntity) {
        Map<String, Object> tags = new HashMap<>();
        tags.put("tags", methodEntity.getTags());
        return tags;
    }

    private static Map<String, Object> getParameters(MethodEntity methodEntity) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        Map<String, Object> variableInfo = new LinkedHashMap<>();
        Set<String> requiredKey = new LinkedHashSet<>();
        for (ParameterEntity parameterEntity : methodEntity.getParameterEntities()) {
            Map<String, Object> paramDetails = new HashMap<>();
            if (parameterEntity.getDescription() != null) {
                paramDetails.put("description", parameterEntity.getDescription());
            }
            if (!parameterEntity.getDefaultValue().isEmpty()) {
                paramDetails.put("default", parameterEntity.getDefaultValue());
            }
            if (parameterEntity.isRequired()) {
                requiredKey.add(parameterEntity.getName());
            }
            paramDetails.putAll(convertToMap(parameterEntity.getType()));
            variableInfo.put(parameterEntity.getName(), paramDetails);
        }
        parameters.put("properties", variableInfo);
        parameters.put("required", requiredKey);
        return parameters;
    }

    private static Map<String, Object> convertToMap(Object type) {
        try {
            return OBJECT_MAPPER.readValue(type.toString(), new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new IllegalStateException("Can not parse json string to map value.", e);
        }
    }
}
