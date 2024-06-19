/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators;

import static com.huawei.jade.fel.engine.operators.patterns.SyncTipper.format;
import static com.huawei.jade.fel.engine.operators.patterns.SyncTipper.passThrough;
import static com.huawei.jade.fel.utils.FlowsTestUtils.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fit.waterflow.domain.utils.SleepUtil;
import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatMessages;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.core.formatters.OutputParser;
import com.huawei.jade.fel.core.formatters.json.JsonOutputParser;
import com.huawei.jade.fel.core.memory.CacheMemory;
import com.huawei.jade.fel.core.memory.Memory;
import com.huawei.jade.fel.core.util.Tip;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.flows.Conversation;
import com.huawei.jade.fel.engine.operators.models.ChatBlockModel;
import com.huawei.jade.fel.engine.operators.models.ChatStreamModel;
import com.huawei.jade.fel.engine.operators.prompts.Prompts;

import lombok.Data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 模型算子测试。
 *
 * @author 刘信宏
 * @since 2024-05-08
 */
public class ModelTest {
    private static final ObjectSerializer TEST_SERIALIZER = new JacksonObjectSerializer(null, null, null);

    @Data
    private static class ModelOutput {
        @Property(description = "model answer")
        private String ans;
    }

    @Test
    void shouldOkWhenBlockModelWithSettingMemoryKey() {
        List<ChatMessages> messages = new ArrayList<>();
        ChatBlockModel model = new ChatBlockModel(prompts -> FlatChatMessage.from(new AiMessage("model answer")));
        AiProcessFlow<Tip, ChatMessage> flow = AiFlows.<Tip>create()
            .prompt(Prompts.sys("sys msg"), Prompts.human("answer {{0}}").memory("0"))
            .generate(model)
            .close();

        Memory memory = new CacheMemory();
        Conversation<Tip, ChatMessage> session = flow.converse().bind(memory);
        session.doOnSuccess(data -> messages.add(ChatMessages.from(data))).offer(Tip.fromArray("question 1")).await();

        assertThat(messages).hasSize(1).map(m -> m.messages().size()).containsSequence(1);
        // 仅保存用户指定的内容
        assertThat(memory.text()).isEqualTo("human:question 1\n" + "ai:model answer");
    }

    @Test
    void shouldOkWhenBlockModelWithFormatter() {
        OutputParser<ModelOutput> parser =
            JsonOutputParser.create(TEST_SERIALIZER, ModelOutput.class);

        ChatBlockModel model = new ChatBlockModel(prompts ->
            FlatChatMessage.from(new AiMessage("{\"ans\":\"model answer\"}")));
        AiProcessFlow<Tip, ModelOutput> flow = AiFlows.<Tip>create()
            .runnableParallel(format("format", parser), passThrough())
            .prompt(Prompts.human("{{question}} {{format}}").memory("question"))
            .generate(model.bind(new ChatOptions()))
            .map(ChatMessage::text)
            .format(parser)
            .close();

        AtomicReference<ModelOutput> modelOutput = new AtomicReference<>();
        Memory memory = new CacheMemory();
        Conversation<Tip, ModelOutput> session = flow.converse().bind(memory);
        session.doOnSuccess(modelOutput::set).offer(Tip.from("question", "question 1")).await();

        assertThat(modelOutput.get()).isNotEqualTo(null);
        assertThat(modelOutput.get().getAns()).isEqualTo("model answer");
        assertThat(memory.text()).isEqualTo("human:question 1\n" + "ai:{\"ans\":\"model answer\"}");
    }

    @Nested
    @DisplayName("流式模型测试")
    class StreamingModelTest {
        private final ChatStreamModel model = new ChatStreamModel(input -> Choir.create(emitter -> {
            for (int i = 0; i < 4; i++) {
                emitter.emit(FlatChatMessage.from(new AiMessage(String.valueOf(i))));
                SleepUtil.sleep(10);
            }
            emitter.complete();
        }));

        private final AiProcessFlow<Tip, String> flow = AiFlows.<Tip>create()
            .prompt(Prompts.human("answer: {{0}}"))
            .generate(this.model)
            .reduce(() -> "", (acc, input) -> {
                acc += input.text();
                return acc;
            })
            .close();

        @Test
        void shouldOkWhenStreamModelWithChoirEmitter() {
            AtomicReference<String> result = new AtomicReference<>();
            this.flow.converse()
                .doOnSuccess(result::set)
                .offer(Tip.fromArray("test streaming model")).await();
            assertThat(result.get()).isEqualTo("0123");
        }

        @Test
        void shouldOkWhenStreamModelWithMemory() {
            Memory memory = new CacheMemory();
            AtomicReference<String> result = new AtomicReference<>();
            this.flow.converse().doOnSuccess(result::set)
                .bind(memory)
                .offer(Tip.fromArray("test streaming model")).await();
            assertThat(result.get()).isEqualTo("0123");
            assertThat(memory.text()).isEqualTo("human:answer: test streaming model\n" + "ai:0123");

            // 指定需要保存的用户问题
            memory = new CacheMemory();
            result.set(null);
            AiFlows.<Tip>create().prompt(Prompts.human("answer: {{0}}").memory("0"))
                .generate(this.model)
                .reduce(() -> "", (acc, input) -> {
                    acc += input.text();
                    return acc;
                }).close()
                .converse().doOnSuccess(result::set).bind(memory)
                .offer(Tip.fromArray("test streaming model")).await();

            assertThat(memory.text()).isEqualTo("human:test streaming model\n" + "ai:0123");
            assertThat(result.get()).isEqualTo("0123");
        }

        @Test
        void shouldOkWhenStreamModelWithStreamingConsumer() {
            StringBuilder accResult = new StringBuilder();
            StringBuilder chunkResult = new StringBuilder();
            this.flow.converse()
                .bind((acc, chunk) -> {
                    if (chunk.isEnd()) {
                        return;
                    }
                    chunkResult.append(chunk.text());
                    accResult.append(acc.text()).append("\n");
                })
                .offer(Tip.fromArray("test streaming model")).await();
            assertThat(chunkResult.toString()).isEqualTo("0123");
            assertThat(accResult.toString()).isEqualTo("0\n01\n012\n0123\n");
        }

        @Test
        void shouldOkWhenStreamModelWithMultiConversation() {
            AtomicReference<List<String>> result = new AtomicReference<>();
            result.set(new ArrayList<>());
            AtomicInteger counter = new AtomicInteger();

            AiProcessFlow<Tip, String> currFlow =
                AiFlows.<Tip>create()
                    .prompt(Prompts.human("{{0}}"))
                    .generate(this.model)
                    .reduce(() -> "", (acc, input) -> {
                        acc += input.text();
                        return acc;
                    })
                    .map((m, ctx) -> {
                        result.updateAndGet(list -> {
                            list.add(m);
                            return list;
                        });
                        return m;
                    })
                    .close(r -> counter.incrementAndGet());

            currFlow.converse().offer(Tip.fromArray("test streaming model"));
            currFlow.converse().offer(Tip.fromArray("test streaming model2"));

            waitUntil(() -> counter.get() == 2, 1000);
            assertThat(result.get()).hasSize(2).containsSequence("0123", "0123");
        }

        @Test
        void shouldThrowWhenStreamModelWithException() {
            String expectedMsg = "test exception";
            ChatStreamModel exceptionModel = new ChatStreamModel(input -> Choir.create(emitter -> {
                emitter.fail(new Exception(expectedMsg));
            }));
            Memory memory = new CacheMemory();
            Conversation<Tip, String> exceptionConverse = AiFlows.<Tip>create()
                .prompt(Prompts.human("{{0}}"))
                .generate(exceptionModel)
                .reduce(() -> "", (acc, input) -> {
                    acc += input.text();
                    return acc;
                })
                .close().converse().bind(memory);

            assertThatThrownBy(() -> exceptionConverse.offer(Tip.fromArray("test streaming exception")).await())
                .isInstanceOf(IllegalStateException.class).message().isEqualTo(expectedMsg);
            assertThat(memory.text()).isEqualTo(StringUtils.EMPTY);
        }
    }
}
