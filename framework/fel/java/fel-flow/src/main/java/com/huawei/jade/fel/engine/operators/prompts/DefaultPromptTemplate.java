/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.prompts;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.core.chat.Prompt;
import com.huawei.jade.fel.core.chat.support.ChatMessages;
import com.huawei.jade.fel.core.template.MessageTemplate;
import com.huawei.jade.fel.core.util.Tip;

/**
 * 人类提示词模板。文本数据渲染模板，媒体数据直接与提示词一起组装为 {@link Prompt}。
 *
 * @author 刘信宏
 * @since 2024-04-12
 */
public class DefaultPromptTemplate implements PromptTemplate<Tip> {
    private final MessageTemplate messageTemplate;

    /**
     * 使用 mustache 模板初始化 {@link DefaultPromptTemplate}。
     *
     * @param messageTemplate 表示消息模板的 {@link MessageTemplate}。
     * @throws IllegalArgumentException 当 {@code template} 为 {@code null}、空字符串或只有空白字符的字符串时。
     */
    public DefaultPromptTemplate(MessageTemplate messageTemplate) {
        Validation.notNull(messageTemplate, "The template cannot be null.");
        this.messageTemplate = messageTemplate;
    }

    @Override
    public Prompt invoke(Tip input) {
        return ChatMessages.from(this.messageTemplate.render(input.freeze()));
    }
}
