/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.fitframework.util.ObjectUtils.getIfNull;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.store.FunctionalTool;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 表示函数工具的带 Json 规范描述的抽象实现。
 *
 * @author 季聿阶
 * @since 2024-04-06
 */
public abstract class AbstractSchemaFunctionalTool extends AbstractTool implements FunctionalTool {
    private static final Map<String, Type> JSON_SCHEMA_TYPE_TO_JAVA_TYPE = MapBuilder.<String, Type>get()
            .put("string", String.class)
            .put("integer", BigInteger.class)
            .put("number", BigDecimal.class)
            .put("boolean", Boolean.class)
            .put("object", Map.class)
            .put("array", List.class)
            .build();

    private final Map<String, Object> toolSchema;
    private final Map<String, Object> parametersSchema;
    private final Map<String, Object> returnSchema;

    /**
     * 通过工具格式规范来初始化 {@link AbstractTool} 的新实例。
     *
     * @param toolSchema 表示工具格式规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    protected AbstractSchemaFunctionalTool(Map<String, Object> toolSchema) {
        super(FunctionalTool.TYPE, toolSchema);
        this.toolSchema = notNull(toolSchema, "The tool schema cannot be null.");
        this.toolSchema.put("type", FunctionalTool.TYPE);
        this.parametersSchema =
                notNull(cast(toolSchema.get("parameters")), "The parameters json schema cannot be null.");
        this.returnSchema = getIfNull(cast(toolSchema.get("return")), Collections::emptyMap);
    }

    private static Type convertJsonSchemaTypeToJavaType(String schemaType) {
        Type javaType = JSON_SCHEMA_TYPE_TO_JAVA_TYPE.get(schemaType);
        return notNull(javaType,
                () -> new IllegalStateException(StringUtils.format("Unsupported json schema type. [type={0}]",
                        schemaType)));
    }

    @Override
    public Map<String, Object> schema() {
        return this.toolSchema;
    }

    @Override
    public List<Type> parameters() {
        Map<String, Object> properties = cast(this.parametersSchema.get("properties"));
        List<String> parameterNames = this.parameterNames();
        Validation.isTrue(properties.size() == parameterNames.size(),
                "The size of properties must equals to the parameter names. "
                        + "[propertiesSize={0}, parameterNamesSize={1}]",
                properties.size(),
                parameterNames.size());
        List<Type> types = new ArrayList<>();
        for (String parameterName : parameterNames) {
            Map<String, Object> property = cast(properties.get(parameterName));
            notNull(property, "No property. [name={0}]", parameterName);
            String propertyType = cast(property.get("type"));
            types.add(convertJsonSchemaTypeToJavaType(propertyType));
        }
        return types;
    }

    @Override
    public List<String> parameterNames() {
        if (MapUtils.isEmpty(this.parametersSchema)) {
            return Collections.emptyList();
        }
        List<String> order = cast(this.parametersSchema.get("order"));
        if (CollectionUtils.isEmpty(order)) {
            return Collections.emptyList();
        }
        return order;
    }

    @Override
    public int parameterIndex(String name) {
        List<String> parameterNames = this.parameterNames();
        for (int i = 0; i < parameterNames.size(); i++) {
            if (StringUtils.equalsIgnoreCase(name, parameterNames.get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public List<String> requiredParameterNames() {
        if (MapUtils.isEmpty(this.parametersSchema)) {
            return Collections.emptyList();
        }
        List<String> required = cast(this.parametersSchema.get("required"));
        if (CollectionUtils.isEmpty(required)) {
            return Collections.emptyList();
        }
        return required;
    }

    @Override
    public Type returnType() {
        if (MapUtils.isEmpty(this.returnSchema)) {
            return Object.class;
        }
        String returnType = cast(this.returnSchema.get("type"));
        return convertJsonSchemaTypeToJavaType(returnType);
    }
}
