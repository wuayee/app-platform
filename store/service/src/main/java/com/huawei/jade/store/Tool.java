/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store;

import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.store.support.DefaultWorkflowTool;
import com.huawei.jade.store.support.FitTool;
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
public interface Tool {
    /**
     * 获取工具的类型。
     *
     * @return 表示工具类型的 {@link String}。
     */
    String type();

    /**
     * 获取工具的元数据信息。
     *
     * @return 表示工具的元数据信息的 {@link Metadata}。
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
     * 创建一个 FIT 调用工具。
     *
     * @param brokerClient 表示 FIT 调用代理的的 {@link BrokerClient}。
     * @param serializer 表示 Json 序列化器的 {@link ObjectSerializer}。
     * @param metadata 表示工具元数据信息的 {@link ConfigurableMetadata}。
     * @return 表示创建的 FIT 调用工具的 {@link JsonTool}。
     * @throws IllegalArgumentException 当 {@code brokerClient}、{@code serializer} 或 {@code metadata} 为
     * {@code null} 时。
     */
    static JsonTool fit(BrokerClient brokerClient, ObjectSerializer serializer, ConfigurableMetadata metadata) {
        return new FitTool(brokerClient, serializer, metadata);
    }

    /**
     * 创建一个工作流工具。
     *
     * @param jsonTool 表示工作流工具入口调用的真实工具的 {@link JsonTool}。
     * @param metadata 表示工作流工具的元数据信息的 {@link ConfigurableMetadata}。
     * @return 表示创建的工具流工具的 {@link WorkflowTool}。
     * @throws IllegalArgumentException 当 {@code jsonTool} 或 {@code metadata} 为 {@code null} 时。
     */
    static WorkflowTool workflow(JsonTool jsonTool, Tool.ConfigurableMetadata metadata) {
        return new DefaultWorkflowTool(jsonTool, metadata);
    }

    /**
     * 表示工具的元数信息。
     */
    interface Metadata {
        /**
         * 获取工具的名字。
         *
         * @return 表示工具名字的 {@link String}。
         */
        String name();

        /**
         * 获取工具的描述。
         *
         * @return 表示工具描述的 {@link String}。
         */
        String description();

        /**
         * 获取工具的格式规范描述。
         *
         * @return 表示工具的格式规范描述的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         */
        Map<String, Object> schema();

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
         * 获取工具的参数列表中，必须的参数名字列表。
         *
         * @return 表示必须的参数名字列表的 {@link List}{@code <}{@link String}{@code >}。
         */
        List<String> requiredParameterNames();

        /**
         * 获取工具的返回值类型。
         *
         * @return 表示工具的返回值类型的 {@link Type}。
         */
        Type returnType();

        /**
         * 获取工具内执行的方法。
         * <p></p>
         *
         * @return 表示工具内执行的方法类型的 {@link Optional}{@code <}{@link Method}{@code >}。
         */
        Optional<Method> getMethod();
    }

    /**
     * 表示可配置的元数据信息。
     */
    interface ConfigurableMetadata extends Metadata {
        /**
         * 设置格式规范描述的属性。
         *
         * @param key 表示待设置的属性的键的 {@link String}。
         * @param value 表示待设置的属性的值的 {@link Object}。
         */
        void schemaProperty(String key, Object value);

        /**
         * 通过本地方法创建一个可配置的元数据信息。
         *
         * @param method 表示本地方法的 {@link Method}。
         * @return 表示通过本地方法创建一个可配置的元数据信息的 {@link ConfigurableMetadata}。
         * @throws IllegalArgumentException 当 {@code method} 为 {@code null} 时。
         */
        static ConfigurableMetadata fromMethod(Method method) {
            return new MethodToolMetadata(method);
        }

        /**
         * 通过工具的格式规范创建一个可配置的元数据信息。
         *
         * @param schema 表示工具的格式规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         * @return 表示通过工具的格式规范创建一个可配置的元数据信息的 {@link ConfigurableMetadata}。
         * @throws IllegalArgumentException 当 {@code schema} 为 {@code null} 时。
         */
        static ConfigurableMetadata fromSchema(Map<String, Object> schema) {
            return new SchemaToolMetadata(schema);
        }
    }
}
