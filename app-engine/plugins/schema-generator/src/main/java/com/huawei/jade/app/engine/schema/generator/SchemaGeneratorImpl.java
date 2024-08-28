/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.schema.generator;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import com.huawei.jade.app.engine.schema.exception.JsonInvalidException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.util.Iterator;

/**
 * 表示 {@link SchemaGenerator} 的默认实现。
 *
 * @author 兰宇晨
 * @since 2024-08-10
 */
@Component
public class SchemaGeneratorImpl implements SchemaGenerator {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static String buildFieldSchema(JsonNode fieldNode, String fieldName) throws JsonProcessingException {
        JsonNodeType fieldType = fieldNode.getNodeType();
        StringBuilder fieldSchema = new StringBuilder("\"");

        switch (fieldType) {
            case BOOLEAN:
                fieldSchema.append(fieldName).append("\":{\"type\":\"boolean\"}");
                break;
            case NUMBER:
                fieldSchema.append(fieldName)
                        .append("\":{\"type\":")
                        .append(fieldNode.isIntegralNumber() ? "\"integer\"" : "\"number\"")
                        .append("}");
                break;
            case OBJECT:
                fieldSchema.append(fieldName).append("\":").append(buildSchema(fieldNode));
                break;
            case STRING:
                fieldSchema.append(fieldName).append("\":{\"type\":\"string\"}");
                break;
            case ARRAY:
            default:
                throw new JsonInvalidException(fieldNode.toString(), "Unsupported node type: " + fieldType);
        }
        return fieldSchema.toString();
    }

    private static String buildSchema(JsonNode node) throws JsonProcessingException {
        StringBuilder schemaBuilder = new StringBuilder();

        schemaBuilder.append("{\"type\":\"object\",\"properties\":{");

        Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode fieldNode = node.get(fieldName);
            schemaBuilder.append(buildFieldSchema(fieldNode, fieldName));

            if (fieldNames.hasNext()) {
                schemaBuilder.append(",");
            }
        }
        schemaBuilder.append("}}");
        return schemaBuilder.toString();
    }

    @Override
    @Fitable(id = "com.huawei.jade.app.engine.schema.generator.SchemaValidatorImpl.generateSchema")
    public String generateSchema(String json) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(json);
            return buildSchema(root);
        } catch (JsonProcessingException e) {
            throw new JsonInvalidException(json, e.getMessage(), e);
        }
    }
}