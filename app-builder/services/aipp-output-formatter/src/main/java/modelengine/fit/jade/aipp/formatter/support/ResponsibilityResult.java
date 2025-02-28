/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter.support;

import modelengine.fit.jade.aipp.formatter.MessageItem;
import modelengine.fit.jade.aipp.formatter.OutputMessage;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Validation;

import java.util.List;

/**
 * 应用响应格式化器职责链的响应结果。
 *
 * @author 刘信宏
 * @since 2024-11-22
 */
public class ResponsibilityResult implements OutputMessage {
    private final List<MessageItem> items;
    private final String text;
    private final String owner;

    /**
     * 构造应用响应消息。
     *
     * @param message 表示应用响应消息的 {@link MessageItem}。
     * @param owner 表示责任匹配的格式化器名称的 {@link String}。
     */
    public ResponsibilityResult(OutputMessage message, String owner) {
        Validation.notNull(message, "The output message cannot be null.");
        this.items = message.items();
        this.text = message.text();
        this.owner = Validation.notNull(owner, "The output formatter owner cannot be null.");
    }

    @Nonnull
    @Override
    public List<MessageItem> items() {
        return this.items;
    }

    @Nonnull
    @Override
    public String text() {
        return this.text;
    }

    @Nonnull
    public String owner() {
        return this.owner;
    }
}
