/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.patterns;

import com.huawei.fitframework.inspection.Validation;
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
     * @param chatModel 表示聊天模型推理服务对象的 {@link ChatModelService}。
     * @param options 表示聊天大模型超参数的 {@link ChatOptions}
     * @throws IllegalArgumentException 当 {@code toolProvider} 、 {@code chatModel} 和 {@code options}
     * 任一个为 {@code null} 时。
     */
    public DefaultAgent(ToolProvider toolProvider, ChatModelService chatModel, ChatOptions options) {
        this(toolProvider, chatModel, options, AGENT_MSG_KEY);
    }

    /**
     * 指定 Agent 响应的自定义键，使用工具提供者和大模型服务对象初始化 {@link DefaultAgent}。
     *
     * @param toolProvider 表示工具提供者的 {@link ToolProvider}。
     * @param chatModel 表示聊天模型推理服务对象的 {@link ChatModelService}。
     * @param options 表示聊天大模型超参数的 {@link ChatOptions}
     * @param agentMsgKey agentMsgKey 表示 Agent 响应的所在自定义键的 {@link String}。
     * @throws IllegalArgumentException
     * <ln>
     *     <li>当 {@code toolProvider} 、 {@code chatModel} 和 {@code options} 任一个为 {@code null} 时。</li>
     *     <li>当 {@code agentMsgKey} 为 {@code null} 、空字符串或只有空白字符的字符串时。</li>
     * </ln>
     */
    public DefaultAgent(ToolProvider toolProvider, ChatModelService chatModel, ChatOptions options,
            String agentMsgKey) {
        super(() -> buildFlow(toolProvider, new ChatBlockModel<>(chatModel, options), agentMsgKey));
    }

    private static AiProcessFlow<Prompt, Prompt> buildFlow(ToolProvider toolProvider, ChatBlockModel<Prompt> model,
            String agentMsgKey) {
        Validation.notNull(toolProvider, "Tool provider cannot be null.");
        Validation.notBlank(agentMsgKey, "Agent message key cannot be blank.");

        return AiFlows.<Prompt>create()
                .just(Agent.putAgentMsg(agentMsgKey))
                .generate(model).id("llm")
                .delegate(Agent.getToolProcessMap(toolProvider, agentMsgKey))
                .conditions()
                .match(input -> Agent.isFinish(toolProvider, input), node -> node.map(Agent.getAgentMsg(agentMsgKey)))
                .matchTo(AiMessage::isToolCall, node -> node.map(Agent.getAgentMsg(agentMsgKey)).to("llm"))
                .others()
                .close();
    }
}
