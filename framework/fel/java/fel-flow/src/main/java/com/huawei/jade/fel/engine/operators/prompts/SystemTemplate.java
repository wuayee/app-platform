/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.prompts;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.chat.ChatMessages;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.core.template.StringTemplate;
import com.huawei.jade.fel.core.template.support.SystemMessageTemplate;
import com.huawei.jade.fel.core.util.Tip;
import com.huawei.jade.fel.engine.operators.CustomState;

/**
 * 系统提示词模板。仅使用文本数据渲染模板，忽略媒体数据。
 *
 * @author 刘信宏
 * @since 2024-04-12
 */
public class SystemTemplate implements PromptTemplate<Tip> {
    private final SystemMessageTemplate messageTemplate;

    /**
     * 使用 mustache 模板初始化 {@link SystemTemplate}。
     *
     * @param template 表示使用 mustache 模板语法的 {@link String}。
     * @throws IllegalArgumentException 当 {@code template} 为 {@code null}、空字符串或只有空白字符的字符串时。
     */
    public SystemTemplate(StringTemplate template) {
        Validation.notNull(template, "Template cannot be null.");
        messageTemplate = new SystemMessageTemplate(template);
    }

    @Override
    public Prompt invoke(CustomState<Tip> arg) {
        return ChatMessages.from(messageTemplate.render(arg.data().freeze()));
    }
}
