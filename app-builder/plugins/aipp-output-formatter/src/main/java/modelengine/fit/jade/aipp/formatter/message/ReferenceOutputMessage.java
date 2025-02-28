/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter.message;

import modelengine.fit.jade.aipp.formatter.MessageItem;
import modelengine.fit.jade.aipp.formatter.OutputMessage;
import modelengine.fit.jade.aipp.formatter.constant.FormatterConstant;
import modelengine.fit.jade.aipp.formatter.message.item.ReferenceMessageItem;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 溯源应用响应消息。
 *
 * @author 刘信宏
 * @since 2024-11-24
 */
public class ReferenceOutputMessage implements OutputMessage {
    private final List<MessageItem> items;
    private final ObjectSerializer serializer;

    /**
     * 使用序列化器和应用响应数据列表构造 {@link ReferenceOutputMessage}。
     *
     * @param serializer 表示对象序列化器的 {@link ObjectSerializer}。
     * @param items 表示应用响应数据列表的 {@link List}{@code <}{@link MessageItem}{@code >}。
     */
    public ReferenceOutputMessage(ObjectSerializer serializer, List<MessageItem> items) {
        this.serializer = Validation.notNull(serializer, "The serializer cannot be null");
        this.items = ObjectUtils.nullIf(items, Collections.emptyList());
    }

    @Nonnull
    @Override
    public List<MessageItem> items() {
        return this.items;
    }

    @Nonnull
    @Override
    public String text() {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> references = new HashMap<>();
        for (int index = 0; index < this.items.size(); index++) {
            String separator =
                    (index == this.items.size() - 1) ? StringUtils.EMPTY : FormatterConstant.OUTPUT_SEPARATOR;
            MessageItem messageItem = this.items.get(index);
            sb.append(messageItem.data());
            sb.append(separator);
            references.putAll(messageItem.reference());
        }
        return this.serializer.serialize(new ReferenceMessageItem(sb.toString(), references));
    }
}
