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

import java.util.Collections;
import java.util.Map;

/**
 * 应用默认响应数据。
 *
 * @author 刘信宏
 * @since 2024-11-22
 */
public class DefaultMessageItem implements MessageItem {
    private final String data;
    private final ItemType type = ItemType.TEXT;

    /**
     * 构造应用响应消息。
     *
     * @param data 表示模型响应文本的 {@link String}。
     */
    public DefaultMessageItem(String data) {
        this.data = Validation.notNull(data, "The output data cannot be null.");
    }

    @Nonnull
    @Override
    public ItemType type() {
        return this.type;
    }

    @Nonnull
    @Override
    public String data() {
        return this.data;
    }

    @Nonnull
    @Override
    public Map<String, Object> reference() {
        return Collections.emptyMap();
    }
}
