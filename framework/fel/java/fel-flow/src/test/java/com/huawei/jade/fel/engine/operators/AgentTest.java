/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.huawei.fit.waterflow.domain.utils.Mermaid;
import com.huawei.jade.fel.chat.ChatModelService;
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
import com.huawei.jade.fel.engine.operators.patterns.Agent;
import com.huawei.jade.fel.engine.operators.patterns.DefaultAgent;
import com.huawei.jade.fel.engine.operators.prompts.Prompts;
import com.huawei.jade.fel.tool.Tool;
import com.huawei.jade.fel.tool.ToolCall;
import com.huawei.jade.fel.tool.ToolProvider;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 智能体测试。
 *
 * @author 刘信宏
 * @since 2024-05-08
 */
public class AgentTest {
    @Test
    void shouldOkWhenCreateAiFlowWithAgent() throws InterruptedException {
        AtomicReference<Double> modelPara = new AtomicReference<>();
        ChatModelService model = getChatSyncModel(modelPara);
        Agent<Prompt, Prompt> agent = getAgent(model, false);

        AtomicReference<String> answer = new AtomicReference<>();
        AiProcessFlow<Tip, String> flow = AiFlows.<Tip>create()
                .prompt(Prompts.history(), Prompts.human("{{0}}"))
                .delegate(agent)
                .map(Prompt::text)
                .close(r -> answer.set(r.get().getData()), (e, r, f) -> {});
        // converse
        ChatOptions options = ChatOptions.builder().temperature(0.8).build();
        Conversation<Tip, String> agentConverse = flow.converse().bind(new CacheMemory()).bind(options);

        agentConverse.offer(Tip.fromArray("calculate 40*50")).await();
        assertThat(answer.get()).isEqualTo("calculate 40*50\ntoolcall\ntooldata\nmodel result1");
        assertThat(modelPara.get()).isCloseTo(0.8f, Percentage.withPercentage(1));
        answer.set(null);

        agentConverse.offer(Tip.fromArray("calculate 60*70")).await();
        assertThat(answer.get()).isEqualTo("calculate 40*50\nmodel result1\ncalculate 60*70\nmodel result1");
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldOkWhenAgentWithAsyncTools(boolean isAsyncTool) throws InterruptedException {
        AtomicReference<Double> modelPara = new AtomicReference<>();
        ChatModelService model = getChatSyncModel(modelPara);
        Agent<Prompt, Prompt> agent = getAgent(model, isAsyncTool);

        AiProcessFlow<Tip, String> flow = AiFlows.<Tip>create()
                .prompt(Prompts.history(), Prompts.human("{{0}}"))
                .delegate(agent)
                .map(Prompt::text)
                .close();

        ChatOptions options = ChatOptions.builder().temperature(0.8).build();
        String ans = flow.converse().bind(options).offer(Tip.fromArray("calculate 40*60")).await();
        String expectedAns = isAsyncTool
                ? "calculate 40*60\ntoolcall\ntooldata"
                : "calculate 40*60\ntoolcall\ntooldata\nmodel result1";
        assertThat(ans).isEqualTo(expectedAns);
        if (!isAsyncTool) {
            assertThat(modelPara.get()).isCloseTo(0.8, Percentage.withPercentage(1));
        }
    }

    @Test
    void shouldThrowWhenAsyncAgentWithException() {
        ChatModelService model = getExceptionChatSyncModel("model exception");
        Agent<Prompt, Prompt> agent = getAgent(model, false);

        AtomicReference<String> err = new AtomicReference<>();
        AiProcessFlow<Tip, String> flow = AiFlows.<Tip>create()
                .prompt(Prompts.human("{{0}}"))
                .delegate(agent)
                .map(Prompt::text)
                .close(r -> {}, (e, r, f) -> {
                    err.set(e.getMessage());
                });

        AtomicInteger converseErrCnt = new AtomicInteger();
        IllegalStateException e0 = assertThrows(IllegalStateException.class,
                () -> flow.converse().doOnError(e -> converseErrCnt.getAndIncrement())
                        .offer(Tip.fromArray("calculate 40*50")).await());
        assertThat(e0.getMessage()).isEqualTo("model exception");
        // 全局异常回调
        assertThat(err.get()).isEqualTo("model exception");
        // delegate 场景也仅触发一次对话异常回调
        assertThat(converseErrCnt.get()).isEqualTo(1);

    }

    private static Agent<Prompt, Prompt> getAgent(ChatModelService model, boolean isAsyncTool) {
        ToolProvider toolProvider = getToolProvider(isAsyncTool);
        return new DefaultAgent(toolProvider, model, new ChatOptions());
    }

    private static ToolProvider getToolProvider(boolean isAsyncTool) {
        return new ToolProvider() {
            @Override
            public FlatChatMessage call(ToolCall toolCall) {
                return new FlatChatMessage(new ToolMessage("", "tooldata"));
            }

            @Override
            public List<Tool> getTool(List<String> name) {
                return Collections.singletonList(new Tool(isAsyncTool, Collections.emptyMap()));
            }
        };
    }

    private static ChatModelService buildChatSyncModel(AtomicReference<Double> modelPara, String exceptionMsg) {
        List<ToolCall> toolCalls = Collections.singletonList(new ToolCall());

        AtomicInteger step = new AtomicInteger();
        return request -> {
            if (exceptionMsg != null) {
                throw new IllegalStateException(exceptionMsg);
            }
            if (step.getAndIncrement() == 0) {
                return new FlatChatMessage(new AiMessage("toolcall", toolCalls));
            } else {
                modelPara.set(request.getOptions().getTemperature());
                return new FlatChatMessage(new AiMessage("model result1"));
            }
        };
    }

    private static ChatModelService getExceptionChatSyncModel(String exceptionMsg) {
        return buildChatSyncModel(null, exceptionMsg);
    }

    private static ChatModelService getChatSyncModel(AtomicReference<Double> modelPara) {
        return buildChatSyncModel(modelPara, null);
    }
}
