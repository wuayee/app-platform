/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.prompts;

import static com.huawei.jade.fel.engine.util.StateKey.HISTORY_INPUT;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatMessages;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.character.HumanMessage;
import com.huawei.jade.fel.core.template.support.HumanMessageTemplate;
import com.huawei.jade.fel.core.util.Tip;
import com.huawei.jade.fel.engine.operators.AiRunnableArg;
import com.huawei.jade.fel.engine.operators.CustomState;

import java.util.Optional;

/**
 * 人类提示词模板。文本数据渲染模板，媒体数据直接与提示词一起组装为 {@link Prompt}。
 *
 * @author 刘信宏
 * @since 2024-04-12
 */
public class HumanTemplate implements PromptTemplate<Tip> {
    private final HumanMessageTemplate messageTemplate;
    private String historyInputKey = null;

    /**
     * 使用 mustache 模板初始化 {@link HumanTemplate}。
     *
     * @param template 表示使用 mustache 模板语法的 {@link String}。
     * @throws IllegalArgumentException 当 {@code template} 为 {@code null}、空字符串或只有空白字符的字符串时。
     */
    public HumanTemplate(String template) {
        Validation.notBlank(template, "Template cannot be blank.");
        messageTemplate = new HumanMessageTemplate(template);
    }

    /**
     * 设置原始用户问题所在的键，用于保存历史记录。
     *
     * @param key 表示原始用户问题所在键的 {@link String}。
     * @return 表示提示词模板的 {@link PromptTemplate}{@code <}{@link Tip}{@code >}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 、空字符串或只有空白字符的字符串时。
     */
    public PromptTemplate<Tip> memory(String key) {
        Validation.notBlank(key, "Memory key cannot be blank.");
        this.historyInputKey = key;
        return this;
    }

    @Override
    public Prompt invoke(CustomState<Tip> arg) {
        AiRunnableArg<Tip> aiArg = ObjectUtils.cast(arg);
        ChatMessage chatMessage = messageTemplate.render(aiArg.data().freeze());

        aiArg.setInnerState(HISTORY_INPUT, Optional.ofNullable(historyInputKey)
                .map(key -> ObjectUtils.<ChatMessage>cast(new HumanMessage(aiArg.data().freeze().get(key))))
                .orElse(chatMessage));
        return ChatMessages.from(chatMessage);
    }
}
