/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.prompts;

import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.fitframework.util.ObjectUtils.getIfNull;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatMessages;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.core.Pattern;
import com.huawei.jade.fel.core.memory.Memory;
import com.huawei.jade.fel.engine.util.StateKey;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 历史记录提示词模板。文本数据渲染模板，媒体数据直接与提示词一起组装为 {@link Prompt}。
 *
 * @author 刘信宏
 * @since 2024-06-12
 */
public class HistoryTemplate<T> implements PromptTemplate<T> {
    private final Map<String, Object> args;

    public HistoryTemplate() {
        this(Collections.emptyMap());
    }

    private HistoryTemplate(Map<String, Object> args) {
        this.args = Validation.notNull(args, "The args cannot be null.");
    }

    @Override
    public Prompt invoke(T input) {
        FlowSession session = getIfNull(cast(this.args.get(StateKey.FLOW_SESSION)), FlowSession::new);
        Memory memory = session.getInnerState(StateKey.HISTORY_OBJ);
        List<ChatMessage> messages = Optional.ofNullable(memory)
                .map(Memory::messages)
                .orElseGet(Collections::emptyList);
        return ChatMessages.from(messages);
    }

    @Override
    public Pattern<T, Prompt> bind(Map<String, Object> args) {
        return new HistoryTemplate<>(args);
    }
}
