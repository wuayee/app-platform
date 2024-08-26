/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.engine.operators.prompts;

import static modelengine.fel.engine.util.StateKey.HISTORY_INPUT;

import modelengine.fel.chat.ChatMessage;
import modelengine.fel.chat.ChatMessages;
import modelengine.fel.chat.Prompt;
import modelengine.fel.chat.character.HumanMessage;
import modelengine.fel.core.template.MessageContent;
import modelengine.fel.core.template.StringTemplate;
import modelengine.fel.core.template.support.HumanMessageTemplate;
import modelengine.fel.core.util.Tip;
import modelengine.fel.engine.util.AiFlowSession;
import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Map;
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
    public HumanTemplate(StringTemplate template) {
        Validation.notNull(template, "The template cannot be null.");
        this.messageTemplate = new HumanMessageTemplate(template);
    }

    /**
     * 设置原始用户问题所在的键，用于保存历史记录。
     *
     * @param key 表示原始用户问题所在键的 {@link String}。
     * @return 表示提示词模板的 {@link PromptTemplate}{@code <}{@link Tip}{@code >}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 、空字符串或只有空白字符的字符串时。
     */
    public PromptTemplate<Tip> memory(String key) {
        this.historyInputKey = Validation.notBlank(key, "The memory input key cannot be blank.");
        return this;
    }

    @Override
    public Prompt invoke(Tip input) {
        ChatMessage chatMessage = this.messageTemplate.render(input.freeze());
        Validation.isTrue(AiFlowSession.get().isPresent(), "The ai flow session cannot be empty.");
        FlowSession session = Validation.notNull(AiFlowSession.get().get(), "The flow session cannot be null.");
        session.setInnerState(HISTORY_INPUT, Optional.ofNullable(this.historyInputKey)
            .map(key -> {
                Map<String, MessageContent> values = input.freeze();
                return ObjectUtils.<ChatMessage>cast(new HumanMessage(values.get(key).text(),
                    values.get(key).medias()));
            })
            .orElse(chatMessage));
        return ChatMessages.from(chatMessage);
    }
}
