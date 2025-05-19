/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool;

import modelengine.fitframework.pattern.builder.BuilderFactory;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示可调用工具的实体。
 *
 * @author 易文渊
 * @since 2024-4-8
 */
public interface Info {
    /**
     * 获取工具的命名空间。
     *
     * @return 表示工具分组的 {@link String}。
     */
    String namespace();

    /**
     * 获取工具的名称。
     *
     * @return 表示工具名称的 {@link String}。
     */
    String name();

    /**
     * 获取工具的描述。
     *
     * @return 表示工具描述的 {@link String}。
     */
    String description();

    /**
     * 获取工具的参数描述。
     *
     * @return 表示工具参数描述的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    Map<String, Object> parameters();

    /**
     * 获取工具的扩展属性。
     *
     * @return 表示工具元数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    Map<String, Object> extensions();

    /**
     * {@link Info} 的构建器。
     */
    interface Builder<B extends Builder<B>> {
        /**
         * 设置工具的命名空间。
         *
         * @param namespace 表示工具命名空间的 {@link String}。
         * @return 表示当前构建器的 {@link B}。
         */
        B namespace(String namespace);

        /**
         * 设置工具的名称。
         *
         * @param name 表示工具名称的 {@link String}。
         * @return 表示当前构建器的 {@link B}。
         */
        B name(String name);

        /**
         * 设置工具的参数描述。
         *
         * @param parameters 表示工具参数描述的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         * @return 表示当前构建器的 {@link B}。
         */
        B parameters(Map<String, Object> parameters);

        /**
         * 设置工具的描述。
         *
         * @param description 表示工具参数描述的 {@link String}。
         * @return 表示当前构建器的 {@link B}。
         */
        B description(String description);

        /**
         * 设置工具的扩展属性。
         *
         * @param extensions 表示工具扩展属性的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         * @return 表示当前构建器的 {@link B}。
         */
        B extensions(Map<String, Object> extensions);

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
        return BuilderFactory.get(Info.class, Builder.class).create(null);
    }

    /**
     * 获取工具的唯一标识符。
     *
     * @param toolInfo 表示工具信息的 {@link Info}。
     * @return 表示工具唯一标识符的 {@link String}。
     */
    static String identify(Info toolInfo) {
        return identify(toolInfo.namespace(), toolInfo.name());
    }

    /**
     * 获取工具的唯一标识符。
     *
     * @param namespace 表示工具命名空间的 {@link String}。
     * @param toolName 表示工具名的 {@link String}。
     * @return 表示工具唯一标识符的 {@link String}。
     */
    static String identify(String namespace, String toolName) {
        return StringUtils.format("{0}:{1}", namespace, toolName);
    }
}