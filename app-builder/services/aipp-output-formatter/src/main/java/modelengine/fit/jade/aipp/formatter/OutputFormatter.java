/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter;

import modelengine.fitframework.inspection.Nonnull;

import java.util.Optional;

/**
 * 应用响应的格式化器。
 *
 * @author 刘信宏
 * @since 2024-11-21
 */
public interface OutputFormatter {
    /**
     * 获取格式化器的名称。
     *
     * @return 表示格式化器名称的 {@link String}。
     */
    @Nonnull
    String name();

    /**
     * 格式化响应数据。
     *
     * @param data 表示应用响应数据的 {@link Object}。
     * @return 表示应用响应消息的 {@link Optional}{@code <}{@link OutputMessage}{@code >}。
     */
    Optional<OutputMessage> format(Object data);
}
