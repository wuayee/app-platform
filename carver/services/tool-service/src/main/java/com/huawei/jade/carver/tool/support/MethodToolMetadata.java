/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.json.schema.JsonSchemaManager;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.carver.tool.Tool;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示基于方法构建的工具元数据。
 *
 * @author 王攀博
 * @since 2024-04-18
 */
public class MethodToolMetadata implements Tool.Metadata {
    private final Method method;

    /**
     * 通过本地方法初始化 {@link MethodToolMetadata} 的新实例。
     *
     * @param method 表示本地方法的 {@link Method}。
     */
    public MethodToolMetadata(Method method) {
        this.method = notNull(method, "The functional method cannot be null.");
    }

    @Override
    public List<Type> parameters() {
        return Stream.of(this.method.getGenericParameterTypes()).collect(Collectors.toList());
    }

    @Override
    public List<String> parameterNames() {
        return Stream.of(this.method.getParameters()).map(this::parameterName).collect(Collectors.toList());
    }

    @Override
    public int parameterIndex(String name) {
        List<String> parameterNames = this.parameterNames();
        for (int i = 0; i < parameterNames.size(); i++) {
            String parameterName = parameterNames.get(i);
            if (StringUtils.equals(name, parameterName)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object parameterDefaultValue(String name) {
        Parameter[] parameters = this.method.getParameters();
        for (Parameter parameter : parameters) {
            if (parameter.getName().equals(name)) {
                Property property = parameter.getDeclaredAnnotation(Property.class);
                if (property != null) {
                    return property.defaultValue();
                }
            }
        }
        return null;
    }

    @Override
    public List<String> requiredParameterNames() {
        return Stream.of(this.method.getParameters()).filter(parameter -> {
            if (parameter.getType().isPrimitive()) {
                return true;
            }
            Property annotation = parameter.getDeclaredAnnotation(Property.class);
            if (annotation == null) {
                return false;
            }
            return annotation.required();
        }).map(this::parameterName).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> returnType() {
        return JsonSchemaManager.create().createSchema(this.method.getReturnType()).toJsonObject();
    }

    @Override
    public Optional<Method> getMethod() {
        return Optional.ofNullable(this.method);
    }

    private String parameterName(Parameter parameter) {
        Property annotation = parameter.getDeclaredAnnotation(Property.class);
        if (annotation != null && StringUtils.isNotBlank(annotation.name())) {
            return annotation.name();
        }
        return parameter.getName();
    }
}
