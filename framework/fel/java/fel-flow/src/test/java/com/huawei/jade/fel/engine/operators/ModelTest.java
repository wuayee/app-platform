/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.jade.fel.chat.ChatMessages;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.core.memory.CacheMemory;
import com.huawei.jade.fel.core.memory.Memory;
import com.huawei.jade.fel.core.util.Tip;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.flows.Conversation;
import com.huawei.jade.fel.engine.operators.models.ChatBlockModel;
import com.huawei.jade.fel.engine.operators.prompts.Prompts;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 模型算子测试。
 *
 * @author 刘信宏
 * @since 2024-05-08
 */
public class ModelTest {
    @Test
    void shouldOkWhenBlockModelWithSettingMemoryKey() {
        List<ChatMessages> messages = new ArrayList<>();
        ChatBlockModel<ChatMessages> model =
                new ChatBlockModel<>(prompts -> new FlatChatMessage(new AiMessage("model answer")));
        AiProcessFlow<Tip, AiMessage> flow = AiFlows.<Tip>create()
                .prompt(Prompts.sys("sys msg"), Prompts.human("answer {{0}}").memory("0"))
                .generate(model)
                .close();

        Memory memory = new CacheMemory();
        Conversation<Tip, AiMessage> session = flow.converse().bind(memory);
        session.doOnSuccess(data -> messages.add(ChatMessages.from(data))).offer(Tip.fromArray("question 1")).await();

        assertThat(messages).hasSize(1);
        // 仅保存用户指定的内容
        assertThat(messages.get(0).messages()).hasSize(1);
        assertThat(memory.text()).isEqualTo("human:question 1\n" + "ai:model answer");
    }
}
