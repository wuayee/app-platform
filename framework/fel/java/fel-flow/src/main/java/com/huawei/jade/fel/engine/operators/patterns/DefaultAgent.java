/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.patterns;

import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.chat.ChatMessages;
import com.huawei.jade.fel.chat.ChatModelService;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.operators.models.ChatBlockModel;
import com.huawei.jade.fel.tool.ToolProvider;

/**
 * {@link Agent} 的默认实现。
 *
 * @author 刘信宏
 * @since 2024-04-12
 */
public class DefaultAgent extends Agent<Prompt, Prompt> {
    private static final String AGENT_MSG_KEY = "agent_request";

    /**
     * 使用工具提供者和大模型服务对象初始化 {@link DefaultAgent}。
     *
     * @param toolProvider 表示工具提供者的 {@link ToolProvider}。
     * @param chatModel 表示表示聊天模型推理服务对象的 {@link ChatModelService}。
     * @param options 表示聊天大模型超参数的 {@link ChatOptions}
     * @throws IllegalArgumentException 当 {@code toolProvider} 、 {@code chatModel} 和 {@code options}
     * 任一个为 {@code null} 时。
     */
    public DefaultAgent(ToolProvider toolProvider, ChatModelService chatModel, ChatOptions options) {
        super(() -> buildFlow(toolProvider, new ChatBlockModel<>(chatModel, options)));
    }

    private static AiProcessFlow<Prompt, Prompt> buildFlow(ToolProvider toolProvider, ChatBlockModel<Prompt> model) {
        Validation.notNull(toolProvider, "Tool provider cannot be null.");

        return AiFlows.<Prompt>create()
                .just(((input, context) -> context.setState(AGENT_MSG_KEY, ChatMessages.from(input.messages()))))
                .generate(model).id("llm")
                .delegate((input, context) -> {
                    ChatMessages lastRequest = context.getState(AGENT_MSG_KEY);
                    lastRequest.add(input);
                    lastRequest.addAll(Agent.toolCallHandle(toolProvider, input).messages());
                    return input;
                })
                .conditions()
                .match(input -> isFinish(toolProvider, input), node -> node.map(resultProcess()))
                .matchTo(AiMessage::isToolCall, node -> node.map(resultProcess()).to("llm"))
                .others()
                .close();
    }

    private static boolean isFinish(ToolProvider toolProvider, AiMessage message) {
        return !message.isToolCall() || Agent.containAsyncTool(toolProvider, message);
    }

    private static Operators.ProcessMap<AiMessage, Prompt> resultProcess() {
        return (message, ctx) -> ctx.getState(AGENT_MSG_KEY);
    }
}
