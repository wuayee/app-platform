/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store;

import com.huawei.jade.store.support.MethodToolMetadata;
import com.huawei.jade.store.support.SchemaToolMetadata;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 表示大模型的工具。
 *
 * @author 季聿阶
 * @since 2024-04-05
 */
public interface Tool extends Item {
    /**
     * 获取工具的元数据。
     *
     * @return 表示工具元数据的 {@link Metadata}。
     */
    Metadata metadata();

    /**
     * 调用工具。
     *
     * @param args 表示调用工具的参数列表的 {@link Object}{@code []}。
     * @return 表示调用工具的结果的 {@link Object}。
     */
    Object call(Object... args);

    /**
     * 使用 Json 格式的参数调用工具。
     *
     * @param jsonArgs 表示调用工具的 Json 格式的参数的 {@link String}。
     * @return 表示调用工具的 Json 格式的结果的 {@link String}。
     */
    String callByJson(String jsonArgs);

    /**
     * 表示工具的元数信息。
     */
    interface Metadata {
        /**
         * 获取工具的参数类型列表。
         *
         * @return 表示工具的参数类型列表的 {@link List}{@code <}{@link Type}{@code >}。
         */
        List<Type> parameters();

        /**
         * 获取工具的参数名字列表。
         *
         * @return 表示工具的参数名字列表的 {@link List}{@code <}{@link String}{@code >}。
         */
        List<String> parameterNames();

        /**
         * 获取工具参数列表中指定名字的参数的位置下标。
         *
         * @param name 表示指定参数名字的 {@link String}。
         * @return 表示指定名字参数的位置下标的 {@code int}。
         */
        int parameterIndex(String name);

        /**
         * 获取工具参数列表中指定名字的参数的默认值。
         *
         * @param name 表示指定参数名字的 {@link String}。
         * @return 表示指定名字参数的默认值的 {@link Object}。
         */
        Object parameterDefaultValue(String name);

        /**
         * 获取工具的参数列表中，必须的参数名字列表。
         *
         * @return 表示必须的参数名字列表的 {@link List}{@code <}{@link String}{@code >}。
         */
        List<String> requiredParameterNames();

        /**
         * 获取工具的返回值类型。
         *
         * @return 表示工具返回值类型 JsonSchema 格式的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         */
        Map<String, Object> returnType();

        /**
         * 获取工具内执行的方法。
         * <p></p>
         *
         * @return 表示工具内执行的方法类型的 {@link Optional}{@code <}{@link Method}{@code >}。
         */
        Optional<Method> getMethod();

        /**
         * 通过本地方法创建一个可配置的元数据信息。
         *
         * @param method 表示本地方法的 {@link Method}。
         * @return 表示通过本地方法创建一个可配置的元数据信息的 {@link Metadata}。
         * @throws IllegalArgumentException 当 {@code method} 为 {@code null} 时。
         */
        static Metadata fromMethod(Method method) {
            return new MethodToolMetadata(method);
        }

        /**
         * 通过工具的格式规范创建一个可配置的元数据信息。
         *
         * @param schema 表示工具的格式规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         * @return 表示通过工具的格式规范创建一个可配置的元数据信息的 {@link Metadata}。
         * @throws IllegalArgumentException 当 {@code schema} 为 {@code null} 时。
         */
        static Metadata fromSchema(Map<String, Object> schema) {
            return new SchemaToolMetadata(schema);
        }
    }
}
