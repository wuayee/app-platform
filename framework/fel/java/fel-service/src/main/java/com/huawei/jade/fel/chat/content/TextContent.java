/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat.content;

import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

/**
 * 表示携带文本信息的 {@link MessageContent}。
 *
 * @author 刘信宏
 * @since 2024-04-12
 */
public class TextContent implements MessageContent {
    private final String data;

    /**
     * 使用文本创建 {@link TextContent} 的实例。
     *
     * @param text 表示文本信息的 {@link String}。
     */
    public TextContent(String text) {
        this.data = ObjectUtils.nullIf(text, StringUtils.EMPTY);
    }

    @Override
    public String data() {
        return this.data;
    }
}
