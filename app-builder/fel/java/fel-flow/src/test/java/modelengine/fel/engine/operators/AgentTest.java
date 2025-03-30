/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.engine.operators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.AllArgsConstructor;
import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.MessageType;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.support.AiMessage;
import modelengine.fel.core.chat.support.FlatChatMessage;
import modelengine.fel.core.chat.support.ToolMessage;
import modelengine.fel.core.memory.CacheMemory;
import modelengine.fel.core.tool.ToolCall;
import modelengine.fel.core.tool.ToolInfo;
import modelengine.fel.core.tool.ToolProvider;
import modelengine.fel.core.util.Tip;
import modelengine.fel.engine.flows.AiFlows;
import modelengine.fel.engine.flows.AiProcessFlow;
import modelengine.fel.engine.flows.Conversation;
import modelengine.fel.engine.operators.models.ChatChunk;
import modelengine.fel.engine.operators.models.StreamingConsumer;
import modelengine.fel.engine.operators.patterns.AbstractAgent;
import modelengine.fel.engine.operators.patterns.DefaultStreamAgent;
import modelengine.fel.engine.operators.prompts.Prompts;
import modelengine.fit.waterflow.domain.utils.SleepUtil;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.flowable.Emitter;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 智能体测试。
 *
 * @author 刘信宏
 * @since 2024-05-08
 */
public class AgentTest {
    private static final String TOOL_DEFAULT_VALUE = "tool_data";

    @Nested
    @DisplayName("流式agent测试")
    class StreamingAgentTest {
        @AllArgsConstructor
        class ChunkResult {
            private String data;
            private List<ToolCall> toolCalls;
            private LocalDateTime consumeTime;

            String data() {
                return this.data;
            }
        }

        @Test
        void shouldOkWhenCreateAiFlowWithStreamAgent() {
            AbstractAgent<Prompt, Prompt> agent = this.getStreamAgent(this.buildChatStreamModel(null));
            AiProcessFlow<Tip, String> flow = getAgentFlow(agent);

            List<ChunkResult> chunks = new ArrayList<>();
            // converse
            ChatOption options = ChatOption.custom().temperature(0.8).build();
            CacheMemory memory = new CacheMemory();
            Conversation<Tip, String> agentConverse =
                    flow.converse().bind(memory).bind(options).bind(this.getStreamingConsumer(chunks));

            AtomicReference<String> answer = new AtomicReference<>();
            agentConverse.doOnSuccess(answer::set).offer(Tip.fromArray("calculate 40*50")).await();
            assertThat(answer.get()).contains("calculate 40*50\ntoolcall\n" + TOOL_DEFAULT_VALUE + "\n0123");

            AtomicReference<String> newAnswer = new AtomicReference<>();
            assertThat(chunks).hasSize(6).map(item -> item.data).containsSequence("toolcall", "0", "1", "2", "3");
            // 校验第一次消费流式数据的时间在最后一次发送数据之前
            LocalDateTime lastEmitTime = LocalDateTime.parse(chunks.get(chunks.size() - 1).data);
            Optional<ChunkResult> firstDataConsume =
                    chunks.stream().filter(chunk -> CollectionUtils.isEmpty(chunk.toolCalls)).findFirst();
            assertThat(firstDataConsume).isPresent();
            assertThat(firstDataConsume.get().consumeTime.isBefore(lastEmitTime)).isTrue();

            // 校验历史记录
            agentConverse.doOnSuccess(newAnswer::set).offer(Tip.fromArray("calculate 60*70")).await();
            // 历史记录保存两次对话的问题和最终答案，不包含工具调用结果
            assertThat(memory.text()).contains("calculate 40*50\n", "calculate 60*70\n")
                    .doesNotContain(TOOL_DEFAULT_VALUE);
        }

        @Test
        void shouldOkWhenAgentWithStreamToolCall() {
            AbstractAgent<Prompt, Prompt> agent = this.getStreamAgent(this.buildChatStreamModelWithStreamTool());
            AiProcessFlow<Tip, List<ChatMessage>> flow = AiFlows.<Tip>create()
                    .prompt(Prompts.human("{{0}}"))
                    .delegate(agent)
                    .map(Prompt::messages)
                    .close();
            Conversation<Tip, List<ChatMessage>> agentConverse =
                    flow.converse().bind(ChatOption.custom().temperature(0.0).build());

            AtomicReference<List<ChatMessage>> answer = new AtomicReference<>();
            agentConverse.doOnSuccess(answer::set).offer(Tip.fromArray("calculate 40*50")).await();

            List<ChatMessage> messages = answer.get();
            assertThat(messages).hasSize(5).extracting(ChatMessage::type, ChatMessage::text)
                    .containsSequence(Tuple.tuple(MessageType.HUMAN, "calculate 40*50"),
                            Tuple.tuple(MessageType.AI, StringUtils.EMPTY),
                            Tuple.tuple(MessageType.TOOL, "id00id01id02id03"),
                            Tuple.tuple(MessageType.TOOL, "id10id11id12id13"));
            // 校验模型最后的回答
            ChatMessage ans = messages.get(messages.size() - 1);
            assertThat(ans.type()).isEqualTo(MessageType.AI);
            assertThat(ans.text()).contains("0123");
        }

        @Test
        void shouldThrowWhenStreamAgentWithException() {
            String expectedMsg = "model exception";
            AbstractAgent<Prompt, Prompt> agent = this.getStreamAgent(this.buildChatStreamModel(expectedMsg));
            AiProcessFlow<Tip, String> flow = getAgentFlow(agent);

            AtomicInteger converseErrCnt = new AtomicInteger();
            assertThatThrownBy(() -> flow.converse()
                    .doOnError(e -> converseErrCnt.getAndIncrement())
                    .offer(Tip.fromArray("calculate 40*50"))
                    .await()).isInstanceOf(IllegalStateException.class).message().isEqualTo(expectedMsg);
            // delegate 场景也仅触发一次对话异常回调
            assertThat(converseErrCnt.get()).isEqualTo(1);
        }

        private StreamingConsumer<ChatMessage, ChatChunk> getStreamingConsumer(List<ChunkResult> chunks) {
            return (acc, chunk) -> {
                if (chunk.isEnd()) {
                    return;
                }
                chunks.add(new ChunkResult(chunk.text(), chunk.toolCalls(), LocalDateTime.now()));
            };
        }

        private AbstractAgent<Prompt, Prompt> getStreamAgent(ChatModel model) {
            ToolProvider toolProvider = getToolProvider();
            return new DefaultStreamAgent(toolProvider, model, ChatOption.custom().build());
        }

        private ChatModel buildChatStreamModel(String exceptionMsg) {
            List<ToolCall> toolCalls =
                    Collections.singletonList(ToolCall.custom().arguments(TOOL_DEFAULT_VALUE).id("id").build());

            AtomicInteger step = new AtomicInteger();
            return ((prompt, chatOption) -> Choir.create(emitter -> {
                if (exceptionMsg != null) {
                    emitter.fail(new IllegalStateException(exceptionMsg));
                }
                if (step.getAndIncrement() == 0) {
                    emitter.emit(new AiMessage("toolcall", toolCalls));
                    emitter.complete();
                    return;
                }
                this.emitAnswerData(emitter);
                emitter.complete();
            }));
        }

        private void emitAnswerData(Emitter<ChatMessage> emitter) {
            for (int i = 0; i < 4; i++) {
                emitter.emit(new AiMessage(String.valueOf(i)));
                SleepUtil.sleep(10);
            }
            // 发送时间戳，用于校验是否假流式
            emitter.emit(new AiMessage(LocalDateTime.now().toString()));
        }

        private ChatModel buildChatStreamModelWithStreamTool() {
            AtomicInteger step = new AtomicInteger();
            return ((prompt, chatOption) -> Choir.create(emitter -> {
                if (step.getAndIncrement() == 0) {
                    this.emitStreamToolCall(emitter, "id0", 0);
                    this.emitStreamToolCall(emitter, "id1", 1);
                    emitter.complete();
                    return;
                }
                this.emitAnswerData(emitter);
                emitter.complete();
            }));
        }

        private void emitStreamToolCall(Emitter<ChatMessage> emitter, String id, Integer index) {
            List<ToolCall> toolCallsHead =
                    Collections.singletonList(ToolCall.custom().index(index).id(id).name("name" + index).build());
            emitter.emit(new AiMessage(StringUtils.EMPTY, toolCallsHead));
            for (int i = 0; i < 4; i++) {
                List<ToolCall> toolCallsArg =
                        Collections.singletonList(ToolCall.custom().index(index).arguments(id + i).build());
                emitter.emit(new AiMessage(StringUtils.EMPTY, toolCallsArg));
                SleepUtil.sleep(10);
            }
        }
    }

    private AiProcessFlow<Tip, String> getAgentFlow(AbstractAgent<Prompt, Prompt> agent) {
        return AiFlows.<Tip>create()
                .prompt(Prompts.history(), Prompts.human("{{0}}"))
                .delegate(agent)
                .map(Prompt::text)
                .close();
    }

    private static ToolProvider getToolProvider() {
        return new ToolProvider() {
            @Override
            public FlatChatMessage call(ToolCall toolCall, Map<String, Object> toolContext) {
                return FlatChatMessage.from(new ToolMessage(toolCall.id(), toolCall.arguments()));
            }

            @Override
            public List<ToolInfo> getTool(List<String> name) {
                return Collections.singletonList(ToolInfo.custom().build());
            }
        };
    }
}
