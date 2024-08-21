/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fitframework.flowable.Choir;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatModelStreamService;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.chat.character.ToolMessage;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.core.memory.CacheMemory;
import com.huawei.jade.fel.core.util.Tip;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.flows.Conversation;
import com.huawei.jade.fel.engine.operators.models.ChatChunk;
import com.huawei.jade.fel.engine.operators.models.StreamingConsumer;
import com.huawei.jade.fel.engine.operators.patterns.AbstractAgent;
import com.huawei.jade.fel.engine.operators.patterns.DefaultStreamAgent;
import com.huawei.jade.fel.engine.operators.prompts.Prompts;
import com.huawei.jade.fel.tool.Tool;
import com.huawei.jade.fel.tool.ToolCall;
import com.huawei.jade.fel.tool.ToolProvider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        @Test
        void shouldOkWhenCreateAiFlowWithStreamAgent() {
            AbstractAgent<Prompt, Prompt> agent = getStreamAgent(buildChatStreamModel(null));
            AiProcessFlow<Tip, String> flow = getAgentFlow(agent);

            StringBuilder accResult = new StringBuilder();
            StringBuilder chunkResult = new StringBuilder();
            // converse
            ChatOptions options = ChatOptions.builder().temperature(0.8).build();
            Conversation<Tip, String> agentConverse = flow.converse()
                    .bind(new CacheMemory())
                    .bind(options)
                    .bind(getStreamingConsumer(chunkResult, accResult));

            AtomicReference<String> answer = new AtomicReference<>();
            agentConverse.doOnSuccess(answer::set).offer(Tip.fromArray("calculate 40*50")).await();
            assertThat(chunkResult.toString()).isEqualTo("toolcall0123");
            assertThat(accResult.toString()).isEqualTo("toolcall\n0\n01\n012\n0123\n");
            assertThat(answer.get()).isEqualTo("calculate 40*50\ntoolcall\n" + TOOL_DEFAULT_VALUE + "\n0123");
            answer.set(null);

            agentConverse.doOnSuccess(answer::set).offer(Tip.fromArray("calculate 60*70")).await();
            // 历史记录保存两次对话的问题和最终答案，不包含工具调用结果
            assertThat(answer.get()).isEqualTo("calculate 40*50\n0123\ncalculate 60*70\n0123");
        }

        @Test
        void shouldThrowWhenStreamAgentWithException() {
            String expectedMsg = "model exception";
            AbstractAgent<Prompt, Prompt> agent = getStreamAgent(buildChatStreamModel(expectedMsg));
            AiProcessFlow<Tip, String> flow = getAgentFlow(agent);

            AtomicInteger converseErrCnt = new AtomicInteger();
            assertThatThrownBy(() -> flow.converse()
                    .doOnError(e -> converseErrCnt.getAndIncrement())
                    .offer(Tip.fromArray("calculate 40*50"))
                    .await()).isInstanceOf(IllegalStateException.class).message().isEqualTo(expectedMsg);
            // delegate 场景也仅触发一次对话异常回调
            assertThat(converseErrCnt.get()).isEqualTo(1);
        }

        private StreamingConsumer<ChatMessage, ChatChunk> getStreamingConsumer(StringBuilder chunkResult,
                StringBuilder accResult) {
            return (acc, chunk) -> {
                if (chunk.isEnd()) {
                    return;
                }
                chunkResult.append(chunk.text());
                accResult.append(acc.text()).append("\n");
            };
        }

        private AbstractAgent<Prompt, Prompt> getStreamAgent(ChatModelStreamService model) {
            ToolProvider toolProvider = getToolProvider();
            return new DefaultStreamAgent(toolProvider, model, new ChatOptions());
        }

        private ChatModelStreamService buildChatStreamModel(String exceptionMsg) {
            List<ToolCall> toolCalls = Collections.singletonList(new ToolCall());

            AtomicInteger step = new AtomicInteger();
            return request -> Choir.create(emitter -> {
                if (exceptionMsg != null) {
                    emitter.fail(new IllegalStateException(exceptionMsg));
                }
                if (step.getAndIncrement() == 0) {
                    emitter.emit(FlatChatMessage.from(new AiMessage("toolcall", toolCalls)));
                    emitter.complete();
                    return;
                }
                for (int i = 0; i < 4; i++) {
                    emitter.emit(FlatChatMessage.from(new AiMessage(String.valueOf(i))));
                }
                emitter.complete();
            });
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
                return FlatChatMessage.from(new ToolMessage("1", TOOL_DEFAULT_VALUE));
            }

            @Override
            public List<Tool> getTool(List<String> name) {
                Tool tool = new Tool();
                return Collections.singletonList(tool);
            }
        };
    }
}
