/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.generator;

import modelengine.jade.schema.SchemaGenerator;
import modelengine.jade.schema.exception.JsonSchemaInvalidException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.util.StringUtils;

import java.util.Iterator;

/**
 * 表示 {@link SchemaGenerator} 的默认实现。
 *
 * @author 兰宇晨
 * @since 2024-08-10
 */
@Component
public class SchemaGeneratorImpl implements SchemaGenerator {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String buildFieldSchema(JsonNode fieldNode, String fieldName) throws JsonProcessingException {
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
                throw new JsonSchemaInvalidException("Unsupported node type: " + fieldType);
        }
        return fieldSchema.toString();
    }

    private String buildSchema(JsonNode node) throws JsonProcessingException {
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
    @Fitable(id = "default")
    public String generateSchema(String json) {
        try {
            JsonNode root = this.objectMapper.readTree(json);
            return buildSchema(root);
        } catch (JsonProcessingException e) {
            throw new JsonSchemaInvalidException(StringUtils.format(
                "The json '{0}' is invalid to generate schema, error: {1}.",
                json, e.getMessage()));
        }
    }
}