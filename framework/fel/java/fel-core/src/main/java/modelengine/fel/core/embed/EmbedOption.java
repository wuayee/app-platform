/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.core.embed;

import modelengine.fitframework.pattern.builder.BuilderFactory;

/**
 * 表示嵌入模型参数的实体。
 *
 * @author 易文渊
 * @since 2024-04-24
 */
public interface EmbedOption {
    /**
     * 获取模型名称。
     *
     * @return 表示模型名称的 {@link String}。
     */
    String model();

    /**
     * 获取服务密钥。
     *
     * @return 表示服务密钥的 {@link String}。
     */
    String apiKey();

    /**
     * 表示 {@link EmbedOption} 的构建器。
     */
    interface Builder {
        /**
         * 设置模型名称。
         *
         * @param model 表示模型名称的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder model(String model);

        /**
         * 设置服务密钥。
         *
         * @param apiKey 表示服务密钥的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder apiKey(String apiKey);

        /**
         * 构建 {@link EmbedOption} 实例。
         *
         * @return 返回构建成功的 {@link EmbedOption} 实例。
         */
        EmbedOption build();
    }

    /**
     * 获取 {@link Builder} 的实例。
     *
     * @return 表示构建器实例的 {@link Builder}。
     */
    static Builder custom() {
        return BuilderFactory.get(EmbedOption.class, Builder.class).create(null);
    }
}