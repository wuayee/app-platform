/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.json.schema.JsonSchemaManager;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.store.FunctionalTool;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示函数工具的带本地方法抽象实现。
 *
 * @author 季聿阶
 * @since 2024-04-05
 */
public abstract class AbstractMethodFunctionalTool extends AbstractTool implements FunctionalTool {
    private final Method method;

    /**
     * 通过工具名字和本地方法来初始化 {@link AbstractTool} 的新实例。
     *
     * @param name 表示工具名字的 {@link String}。
     * @param method 表示函数方法的 {@link Method}。
     */
    protected AbstractMethodFunctionalTool(String name, Method method) {
        super(FunctionalTool.TYPE, name, getDescription(method));
        this.method = notNull(method, "The functional method cannot be null.");
    }

    private static String getDescription(Method method) {
        if (method == null) {
            return StringUtils.EMPTY;
        }
        Genericable annotation = method.getDeclaredAnnotation(Genericable.class);
        if (annotation == null) {
            return StringUtils.EMPTY;
        }
        return annotation.description();
    }

    @Override
    public Map<String, Object> schema() {
        return MapBuilder.<String, Object>get()
                .put("type", FunctionalTool.TYPE)
                .put("name", this.name())
                .put("description", this.description())
                .put("parameters", JsonSchemaManager.create().createSchema(this.method).toJsonObject())
                .build();
    }

    /**
     * 获取本地方法。
     *
     * @return 表示本地方法的 {@link Method}。
     */
    protected Method getMethod() {
        return this.method;
    }

    @Override
    public List<Type> parameters() {
        return Stream.of(this.method.getGenericParameterTypes()).collect(Collectors.toList());
    }

    @Override
    public List<String> parameterNames() {
        return Stream.of(this.method.getParameters()).map(Parameter::getName).collect(Collectors.toList());
    }

    @Override
    public int parameterIndex(String name) {
        List<String> parameterNames = this.parameterNames();
        for (int i = 0; i < parameterNames.size(); i++) {
            String parameterName = parameterNames.get(i);
            if (StringUtils.equalsIgnoreCase(name, parameterName)) {
                return i;
            }
        }
        return -1;
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
        }).map(Parameter::getName).collect(Collectors.toList());
    }

    @Override
    public Type returnType() {
        return this.method.getReturnType();
    }
}
