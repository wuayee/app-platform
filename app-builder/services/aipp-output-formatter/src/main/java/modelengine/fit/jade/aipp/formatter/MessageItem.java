/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter;

import modelengine.fitframework.inspection.Nonnull;

import java.util.Map;

/**
 * 应用响应数据单元。
 *
 * @author 刘信宏
 * @since 2024-11-22
 */
public interface MessageItem {
    /**
     * 获取应用响应数据单元类型。
     *
     * @return 表示应用响应数据单元类型的 {@link ItemType}。
     */
    @Nonnull
    ItemType type();

    /**
     * 获取响应文本数据。
     *
     * @return 表示响应文本的 {@link String}。
     */
    @Nonnull
    String data();

    /**
     * 获取溯源信息。
     *
     * @return 表示溯源信息的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    @Nonnull
    Map<String, Object> reference();
}
