/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter.message.item;

import modelengine.fit.jade.aipp.formatter.ItemType;
import modelengine.fit.jade.aipp.formatter.MessageItem;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Collections;
import java.util.Map;

/**
 * 溯源响应数据。
 *
 * @author 刘信宏
 * @since 2024-11-22
 */
public class ReferenceMessageItem implements MessageItem {
    private final ItemType type = ItemType.TEXT_WITH_REFERENCE;
    private final String data;
    private final Map<String, Object> reference;

    /**
     * 构造应用响应消息。
     *
     * @param data 表示模型响应文本的 {@link String}。
     * @param reference 表示溯源信息的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}
     */
    public ReferenceMessageItem(String data, Map<String, Object> reference) {
        this.data = Validation.notNull(data, "The output data cannot be null.");
        this.reference = ObjectUtils.nullIf(reference, Collections.emptyMap());
    }

    @Nonnull
    @Override
    public ItemType type() {
        return this.type;
    }

    @Override
    @Nonnull
    public String data() {
        return this.data;
    }

    @Override
    @Nonnull
    public Map<String, Object> reference() {
        return Collections.unmodifiableMap(this.reference);
    }
}
