/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter;

import modelengine.fitframework.inspection.Nonnull;

import java.util.List;

/**
 * 应用响应消息。
 *
 * @author 刘信宏
 * @since 2024-11-24
 */
public interface OutputMessage {
    /**
     * 获取应用响应数据。
     *
     * @return 表示应用响应数据的 {@link List}{@code <}{@link MessageItem}{@code >}。
     */
    @Nonnull
    List<MessageItem> items();

    /**
     * 获取应用响应文本。
     *
     * @return 表示响应文本数据的 {@link String}。
     */
    @Nonnull
    String text();
}
