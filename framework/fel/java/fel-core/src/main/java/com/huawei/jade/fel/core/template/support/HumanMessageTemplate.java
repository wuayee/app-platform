/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.template.support;

import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.character.HumanMessage;
import com.huawei.jade.fel.chat.content.Content;
import com.huawei.jade.fel.chat.content.Contents;
import com.huawei.jade.fel.core.template.StringTemplate;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 人类消息模板实现。
 *
 * @author 易文渊
 * @since 2024-04-25
 */
public class HumanMessageTemplate extends AbstractMessageTemplate {
    /**
     * 使用 mustache 模板语法创建 {@link HumanMessageTemplate} 的实例。
     *
     * @param template 表示使用 mustache 模板语法的 {@link String}。
     * @see <a href="https://mustache.github.io/">mustache</a>。
     */
    public HumanMessageTemplate(String template) {
        this(new DefaultStringTemplate(template));
    }

    /**
     * 使用字符串模板创建 {@link HumanMessageTemplate} 的实例。
     *
     * @param template 表示字符串模板的 {@link StringTemplate}。
     */
    public HumanMessageTemplate(StringTemplate template) {
        super(template);
    }

    @Override
    protected ChatMessage collect(Stream<Content> contentStream) {
        return contentStream.collect(Collectors.collectingAndThen(Collectors.toList(),
                data -> new HumanMessage(Contents.from(data))));
    }
}