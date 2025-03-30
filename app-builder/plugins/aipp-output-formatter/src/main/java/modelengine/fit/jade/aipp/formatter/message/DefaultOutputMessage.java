/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter.message;

import modelengine.fit.jade.aipp.formatter.MessageItem;
import modelengine.fit.jade.aipp.formatter.OutputMessage;
import modelengine.fit.jade.aipp.formatter.constant.FormatterConstant;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认应用响应消息。
 *
 * @author 刘信宏
 * @since 2024-11-24
 */
public class DefaultOutputMessage implements OutputMessage {
    private final List<MessageItem> items;

    /**
     * 使用应用响应数据列表构造 {@link DefaultOutputMessage}。
     *
     * @param items 表示应用响应数据列表的 {@link List}{@code <}{@link MessageItem}{@code >}。
     */
    public DefaultOutputMessage(List<MessageItem> items) {
        this.items = ObjectUtils.nullIf(items, Collections.emptyList());
    }

    /**
     * 使用应用响应数据构造 {@link DefaultOutputMessage}。
     *
     * @param item 表示应用响应数据的 {@link MessageItem}。
     * @return 表示应用响应消息的 {@link DefaultOutputMessage}。
     */
    public static DefaultOutputMessage from(MessageItem item) {
        Validation.notNull(item, "The message item cannot be null.");
        return new DefaultOutputMessage(Collections.singletonList(item));
    }

    @Nonnull
    @Override
    public List<MessageItem> items() {
        return this.items;
    }

    @Nonnull
    @Override
    public String text() {
        return this.items().stream().map(MessageItem::data).collect(Collectors.joining(
                FormatterConstant.OUTPUT_SEPARATOR));
    }
}
