/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.support;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.jade.carver.tool.Tool;
import modelengine.jade.carver.tool.ToolSchema;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.json.schema.type.OneOfType;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示基于摘要信息构建的工具元数据。
 *
 * @author 王攀博
 * @since 2024-04-18
 */
public class SchemaToolMetadata implements Tool.Metadata {
    private static final Map<String, Type> JSON_SCHEMA_TYPE_TO_JAVA_TYPE = MapBuilder.<String, Type>get()
            .put("string", String.class)
            .put("integer", BigInteger.class)
            .put("number", BigDecimal.class)
            .put("boolean", Boolean.class)
            .put("object", Map.class)
            .put("array", List.class)
            .build();
    private static final String ONE_OF = "oneOf";

    private final Map<String, Object> parametersSchema;
    private final Map<String, Object> properties;
    private final Map<String, Object> returnSchema;
    private final List<String> orderNames;
    private final Map<String, Object> toolSchema;
    private final String definitionGroupName;
    private final String definitionName;
    private final String description;

    /**
     * 通过工具的格式规范初始化 {@link SchemaToolMetadata} 的新实例。
     *
     * @param definitionGroupName 表示定义组名称 {@link String}。
     * @param toolSchema 表示工具格式规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public SchemaToolMetadata(String definitionGroupName, Map<String, Object> toolSchema) {
        this.definitionGroupName = notNull(definitionGroupName, "Definition group name cannot be null.");
        this.toolSchema = notNull(toolSchema, "The tool schema cannot be null.");
        this.definitionName = notNull(cast(toolSchema.get(ToolSchema.NAME)), "Definition name cannot be null.");
        this.parametersSchema =
                notNull(cast(toolSchema.get(ToolSchema.PARAMETERS)), "The parameters json schema cannot be null.");
        this.properties = cast(this.parametersSchema.get(ToolSchema.PARAMETERS_PROPERTIES));
        this.returnSchema = getIfNull(cast(toolSchema.get(ToolSchema.RETURN_SCHEMA)), Collections::emptyMap);
        this.orderNames = getIfNull(ObjectUtils.<List<String>>cast(toolSchema.get(ToolSchema.PARAMETERS_ORDER)),
                () -> new ArrayList<>(this.properties.keySet()));
        this.description = cast(toolSchema.get(ToolSchema.DESCRIPTION));
    }

    private static Type convertJsonSchemaTypeToJavaType(String schemaType) {
        Type javaType = JSON_SCHEMA_TYPE_TO_JAVA_TYPE.get(schemaType);
        return notNull(javaType,
                () -> new IllegalStateException(StringUtils.format("Unsupported json schema type. [type={0}]",
                        schemaType)));
    }

    @Override
    public List<Type> parameterTypes() {
        int propertiesSize = this.properties.size();
        List<String> parameterNames = this.parameterOrder();
        Validation.isTrue(propertiesSize == parameterNames.size(),
                "The size of properties must equals to the parameter names. "
                        + "[propertiesSize={0}, parameterNamesSize={1}]",
                propertiesSize,
                parameterNames.size());
        List<Type> types = new ArrayList<>();
        for (String parameterName : parameterNames) {
            Map<String, Object> property = cast(this.properties.get(parameterName));
            notNull(property, "No property. [name={0}]", parameterName);
            types.add(this.getTypeFromProperty(property));
        }
        return types;
    }

    private Type getTypeFromProperty(Map<String, Object> property) {
        if (property.containsKey(ONE_OF)) {
            List<Map<String, Object>> subProperties = cast(property.get(ONE_OF));
            List<Type> types = subProperties.stream().map(this::getSingleTypeFromProperty).collect(Collectors.toList());
            return new OneOfType(types);
        }
        return this.getSingleTypeFromProperty(property);
    }

    private Type getSingleTypeFromProperty(Map<String, Object> property) {
        String propertyType = cast(property.get(ToolSchema.PROPERTIES_TYPE));
        return convertJsonSchemaTypeToJavaType(propertyType);
    }

    @Override
    public List<String> parameterOrder() {
        if (CollectionUtils.isNotEmpty(this.orderNames)) {
            return Collections.unmodifiableList(this.orderNames);
        }
        return new ArrayList<>(this.properties.keySet());
    }

    @Override
    public int parameterIndex(String name) {
        List<String> parameterNames = this.parameterOrder();
        for (int i = 0; i < parameterNames.size(); i++) {
            if (StringUtils.equalsIgnoreCase(name, parameterNames.get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public List<String> requiredParameters() {
        if (MapUtils.isEmpty(this.parametersSchema)) {
            return Collections.emptyList();
        }
        List<String> required = cast(this.parametersSchema.get(ToolSchema.PARAMETERS_REQUIRED));
        if (CollectionUtils.isEmpty(required)) {
            return Collections.emptyList();
        }
        return required;
    }

    @Override
    public Map<String, Object> returnType() {
        return this.returnSchema;
    }

    @Override
    public Optional<Method> getMethod() {
        return Optional.empty();
    }

    @Override
    public Map<String, Object> schema() {
        return this.toolSchema;
    }

    @Override
    public String definitionName() {
        return this.definitionName;
    }

    @Override
    public String definitionGroupName() {
        return this.definitionGroupName;
    }

    @Override
    public String description() {
        return this.description;
    }
}
