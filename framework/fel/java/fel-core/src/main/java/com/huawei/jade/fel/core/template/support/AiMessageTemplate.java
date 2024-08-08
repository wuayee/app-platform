/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.template.support;

import com.huawei.fitframework.resource.web.Media;
import com.huawei.jade.fel.core.chat.ChatMessage;
import com.huawei.jade.fel.core.chat.support.AiMessage;
import com.huawei.jade.fel.core.template.StringTemplate;
import com.huawei.jade.fel.core.tool.ToolCall;

import java.util.List;

/**
 * 人工智能消息模板实现。
 *
 * @author 易文渊
 * @since 2024-04-25
 */
public class AiMessageTemplate extends AbstractMessageTemplate {
    private final List<ToolCall> toolCalls;

    /**
     * 使用 mustache 模板语法创建 {@link AiMessageTemplate} 的实例。
     *
     * @param template 表示使用 mustache 模板语法的 {@link String}。
     * @see <a href="https://mustache.github.io/">mustache</a>。
     */
    public AiMessageTemplate(String template, List<ToolCall> toolCalls) {
        this(StringTemplate.create(template), toolCalls);
    }

    /**
     * 使用字符串模板创建 {@link AiMessageTemplate} 的实例。
     *
     * @param template 表示字符串模板的 {@link StringTemplate}。
     */
    public AiMessageTemplate(StringTemplate template, List<ToolCall> toolCalls) {
        super(template);
        this.toolCalls = toolCalls;
    }

    @Override
    protected ChatMessage collect(String text, List<Media> ignore) {
        return new AiMessage(text, this.toolCalls);
    }
}