/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.engine.operators;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fel.chat.ChatMessage;
import modelengine.fel.chat.ChatMessages;
import modelengine.fel.chat.Prompt;
import modelengine.fel.chat.character.AiMessage;
import modelengine.fel.chat.character.HumanMessage;
import modelengine.fel.core.memory.CacheMemory;
import modelengine.fel.core.memory.Memory;
import modelengine.fel.core.template.MessageContent;
import modelengine.fel.core.util.Tip;
import modelengine.fel.engine.flows.AiFlows;
import modelengine.fel.engine.flows.AiProcessFlow;
import modelengine.fel.engine.flows.Conversation;
import modelengine.fel.engine.operators.prompts.Prompts;
import modelengine.fitframework.resource.web.Media;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * 提示词测试。
 *
 * @author 刘信宏
 * @since 2024-05-08
 */
@DisplayName("测试提示词")
public class PromptTest {
    @Test
    void shouldOkWhenPromptWithNamedPlaceholder() {
        final StringBuilder answer = new StringBuilder();
        AiProcessFlow<Tip, Prompt> flow = AiFlows.<Tip>create()
                .prompt(Prompts.sys("{{someone}}"), Prompts.human("{{question}}"))
                .close(r -> answer.append(r.text()));

        Conversation<Tip, Prompt> conversation = flow.converse();
        conversation.offer(new Tip().add("someone", "will").add("question", "my question")).await();

        assertThat(answer.toString()).isEqualTo("will\nmy question");
    }

    @Test
    void shouldOkWhenPromptWithNumberPlaceholder() {
        final StringBuilder answer = new StringBuilder();
        AiProcessFlow<Tip, Prompt> flow = AiFlows.<Tip>create()
                .prompt(Prompts.human("introduce me books about {{0}} written by {{1}}"))
                .close(r -> answer.append(r.text()));

        Conversation<Tip, Prompt> conversation = flow.converse();
        conversation.offer(Tip.fromArray("javascript", "will")).await();

        assertThat(answer.toString()).isEqualTo("introduce me books about javascript written by will");
    }

    @Test
    void shouldOkWhenPromptWithHistoryPlaceholder() {
        StringBuilder sb = new StringBuilder();
        AiProcessFlow<Tip, Prompt> flow = AiFlows.<Tip>create()
                .prompt(Prompts.human("{{someone}}"), Prompts.history())
                .just(input -> sb.append(input.text()).append("\n"))
                .close();

        Memory memory = new CacheMemory() {
            @Override
            public List<ChatMessage> messages() {
                return Arrays.asList(new HumanMessage("question1"), new AiMessage("answer1"));
            }
        };
        Conversation<Tip, Prompt> conversation = flow.converse().bind(memory);
        conversation.offer(Tip.from("someone", "will")).await();
        assertThat(sb.toString()).isEqualTo("will\nquestion1\nanswer1\n");
    }

    @Test
    void shouldOkWhenPromptWithMedia() throws MalformedURLException {
        ChatMessages chatMessages = new ChatMessages();
        AiProcessFlow<Tip, Prompt> flow = AiFlows.<Tip>create()
                .prompt(Prompts.human("answer: {{someone}}"))
                .just(input -> chatMessages.addAll(input.messages()))
                .close();

        Conversation<Tip, Prompt> conversation = flow.converse();
        MessageContent contents =
            MessageContent.from("will", new Media(new URL("http://localhost/1.png")), new Media("image/png", "url1"));
        conversation.offer(Tip.from("someone", contents)).await();

        assertThat(chatMessages.messages()).hasSize(1);
        assertThat(chatMessages.messages().get(0).text()).isEqualTo("answer: will");
        assertThat(chatMessages.messages().get(0).medias()).hasSize(2);
    }
}
