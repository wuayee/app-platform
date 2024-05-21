/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store;

import com.huawei.fitframework.pattern.builder.BuilderFactory;
import com.huawei.jade.store.support.MethodToolMetadata;
import com.huawei.jade.store.support.SchemaToolMetadata;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 表示大模型的工具。
 *
 * @author 季聿阶
 * @since 2024-04-05
 */
public interface Tool {
    /**
     * 获取商品信息。
     *
     * @return 表示商品信息的 {@link Info}。
     */
    Info info();

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
     * 表示工具信息。
     */
    interface Info {
        /**
         * 获取工具的名字。
         *
         * @return 表示工具的名字的 {@link String}。
         */
        String name();

        /**
         * 获取工具的唯一名字。
         *
         * @return 表示工具的唯一名字的 {@link String}。
         */
        String uniqueName();

        /**
         * 获取工具的描述。
         *
         * @return 表示工具的描述的 {@link String}。
         */
        String description();

        /**
         * 获取工具的来源。
         *
         * @return 表示工具的描述的 {@link String}。
         */
        String source();

        /**
         * 获取工具的标签集合。
         *
         * @return 表示工具的标签集合的 {@link Set}{@code <}{@link String}{@code >}。
         */
        Set<String> tags();

        /**
         * 获取工具的格式规范描述。
         *
         * @return 表示工具的格式规范描述的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
         */
        Map<String, Object> schema();

        /**
         * 获取工具的运行规范描述。
         *
         * @return 表示工具的格式规范描述的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
         */
        Map<String, Object> runnables();

        /**
         * {@link Info} 的构建器。
         */
        interface Builder {
            /**
             * 向当前构建器中设置工具的名字。
             *
             * @param name 表示待设置的工具名字的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder name(String name);

            /**
             * 向当前构建器中设置工具的唯一名字。
             *
             * @param uniqueName 表示待设置的工具唯一名字的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder uniqueName(String uniqueName);

            /**
             * 向当前构建器中设置工具的描述。
             *
             * @param description 表示待设置的工具描述的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder description(String description);

            /**
             * 向当前构建器中设置工具的标签集合。
             *
             * @param tags 表示待设置的工具标签集合的 {@link Set}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder tags(Set<String> tags);

            /**
             * 向当前构建器中设置工具的格式规范描述。
             *
             * @param schema 表示工具格式规范描述的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder schema(Map<String, Object> schema);

            /**
             * 向当前构建器中设置工具的运行规范描述。
             *
             * @param runnables 表示工具格式规范描述的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder runnables(Map<String, Object> runnables);

            /**
             * 向当前构建器中设置工具的来源。
             *
             * @param source 表示待设置的工具来源的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder source(String source);

            /**
             * 构建对象。
             *
             * @return 表示构建出来的对象的 {@link Info}。
             */
            Info build();
        }

        /**
         * 获取 {@link Info} 的构建器。
         *
         * @return 表示 {@link Info} 的构建器的 {@link Builder}。
         */
        static Builder custom() {
            return custom(null);
        }

        /**
         * 获取 {@link Info} 的构建器，同时将指定对象的值进行填充。
         *
         * @param value 表示指定对象的 {@link Info}。
         * @return 表示 {@link Info} 的构建器的 {@link Builder}。
         */
        static Builder custom(Info value) {
            return BuilderFactory.get(Info.class, Info.Builder.class).create(value);
        }
    }

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
