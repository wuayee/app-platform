/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.engine.operators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.support.AiMessage;
import modelengine.fel.core.chat.support.ChatMessages;
import modelengine.fel.core.chat.support.ToolMessage;
import modelengine.fel.core.tool.ToolCall;
import modelengine.fel.core.util.Tip;
import modelengine.fel.engine.flows.AiFlows;
import modelengine.fel.engine.flows.AiProcessFlow;
import modelengine.fel.engine.flows.Conversation;
import modelengine.fel.engine.operators.patterns.AbstractAgent;
import modelengine.fel.engine.operators.prompts.Prompts;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.util.CollectionUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 智能体测试。
 *
 * @author 刘信宏
 * @since 2024-05-08
 */
public class AgentTest {
    @Nested
    @DisplayName("抽象agent测试")
    class StreamingAgentTest {
        @Test
        void shouldOkWhenCreateAiFlowWithStreamAgent() {
            AbstractAgent agent = getStreamAgent(buildChatStreamModel(null));
            AiProcessFlow<Tip, String> flow = getAgentFlow(agent);

            StringBuilder chunkResult = new StringBuilder();
            // converse
            ChatOption option = ChatOption.custom().model("model").stream(true).temperature(0.8).build();
            Conversation<Tip, String> agentConverse = flow.converse().bind(option);
            agentConverse.doOnConsume(chunkResult::append).offer(Tip.fromArray("calculate 40*50")).await();
            assertThat(chunkResult.toString()).isEqualTo("0123");
        }

        @Test
        void shouldThrowWhenStreamAgentWithException() {
            String expectedMsg = "model exception";
            AbstractAgent agent = getStreamAgent(buildChatStreamModel(expectedMsg));
            AiProcessFlow<Tip, String> flow = getAgentFlow(agent);

            AtomicInteger converseErrCnt = new AtomicInteger();
            assertThatThrownBy(() -> flow.converse()
                    .doOnError(e -> converseErrCnt.getAndIncrement())
                    .offer(Tip.fromArray("calculate 40*50"))
                    .await()).isInstanceOf(IllegalStateException.class).message().isEqualTo(expectedMsg);
            // delegate 场景也仅触发一次对话异常回调
            assertThat(converseErrCnt.get()).isEqualTo(1);
        }

        private AbstractAgent getStreamAgent(ChatModel model) {
            return new AbstractAgent(model, ChatOption.custom().model("model").stream(true).temperature(0.8).build()) {
                @Override
                protected Prompt doToolCall(List<ToolCall> toolCall) {
                    if (CollectionUtils.isEmpty(toolCall)) {
                        return new ChatMessages();
                    }
                    return ChatMessages.from(new ToolMessage("0", "tool message."));
                }
            };
        }

        private ChatModel buildChatStreamModel(String exceptionMsg) {
            List<ToolCall> toolCalls =
                    Collections.singletonList(ToolCall.custom().id("id").arguments("arguments").name("name").build());

            AtomicInteger step = new AtomicInteger();
            return (prompt, option) -> Choir.create(emitter -> {
                if (exceptionMsg != null) {
                    emitter.fail(new IllegalStateException(exceptionMsg));
                }
                if (step.getAndIncrement() == 0) {
                    emitter.emit(new AiMessage("toolcall", toolCalls));
                    emitter.complete();
                    return;
                }
                for (int i = 0; i < 4; i++) {
                    emitter.emit(new AiMessage(String.valueOf(i)));
                }
                emitter.complete();
            });
        }
    }

    private AiProcessFlow<Tip, String> getAgentFlow(AbstractAgent agent) {
        return AiFlows.<Tip>create()
                .prompt(Prompts.history(), Prompts.human("{{0}}"))
                .delegate(agent)
                .map(ChatMessage::text)
                .close();
    }
}
