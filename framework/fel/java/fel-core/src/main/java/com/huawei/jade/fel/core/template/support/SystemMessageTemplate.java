/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.template.support;

import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.charactar.SystemMessage;
import com.huawei.jade.fel.chat.content.Contents;
import com.huawei.jade.fel.chat.content.MessageContent;
import com.huawei.jade.fel.chat.content.TextContent;
import com.huawei.jade.fel.core.template.StringTemplate;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 系统消息模板实现。
 *
 * @author 易文渊
 * @since 2024-04-25
 */
public class SystemMessageTemplate extends AbstractMessageTemplate {
    /**
     * 使用 mustache 模板语法创建 {@link SystemMessageTemplate} 的实例。
     *
     * @param template 表示使用 mustache 模板语法的 {@link String}。
     * @see <a href="https://mustache.github.io/">mustache</a>。
     */
    public SystemMessageTemplate(String template) {
        this(new DefaultStringTemplate(template));
    }

    /**
     * 使用字符串模板创建 {@link SystemMessageTemplate} 的实例。
     *
     * @param template 表示字符串模板的 {@link StringTemplate}。
     */
    public SystemMessageTemplate(StringTemplate template) {
        super(template);
    }

    @Override
    protected ChatMessage collect(Stream<MessageContent> contentStream) {
        return contentStream.filter(c -> c instanceof TextContent)
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        data -> new SystemMessage(Contents.from(data))));
    }
}