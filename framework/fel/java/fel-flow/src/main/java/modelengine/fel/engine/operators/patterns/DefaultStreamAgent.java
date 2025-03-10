/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.engine.operators.patterns;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.support.ChatMessages;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.engine.flows.AiFlows;
import modelengine.fel.engine.flows.AiProcessFlow;
import modelengine.fel.engine.operators.models.ChatChunk;
import modelengine.fel.engine.operators.models.ChatStreamModel;
import modelengine.fel.core.tool.ToolProvider;
import modelengine.fit.waterflow.domain.context.StateContext;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.CollectionUtils;

import java.util.Collections;

/**
 * {@link AbstractAgent} 的默认流式实现。
 *
 * @author 刘信宏
 * @since 2024-05-17
 */
public class DefaultStreamAgent extends AbstractAgent<Prompt, Prompt> {
    private static final String AGENT_MSG_KEY = "stream_agent_request";
    private static final String GOTO_NODE_ID = "ahead_llm_node";

    private final ToolProvider toolProvider;
    private final ChatStreamModel model;
    private final String agentMsgKey;

    /**
     * 使用工具提供者和大模型服务对象初始化 {@link DefaultStreamAgent}。
     *
     * @param toolProvider 表示工具提供者的 {@link ToolProvider}。
     * @param chatStreamModel 表示聊天流式模型推理服务对象的 {@link ChatModel}。
     * @param option 表示聊天大模型超参数的 {@link ChatOption}
     * @throws IllegalArgumentException 当 {@code toolProvider} 、 {@code chatStreamModel} 和 {@code options}
     * 任一个为 {@code null} 时。
     */
    public DefaultStreamAgent(ToolProvider toolProvider, ChatModel chatStreamModel, ChatOption option) {
        this(toolProvider, chatStreamModel, option, AGENT_MSG_KEY);
    }

    /**
     * 使用工具提供者和大模型服务对象初始化 {@link DefaultStreamAgent}。
     *
     * @param toolProvider 表示工具提供者的 {@link ToolProvider}。
     * @param chatStreamModel 表示聊天流式模型推理服务对象的 {@link ChatModel}。
     * @param option 表示聊天大模型超参数的 {@link ChatOption}
     * @param agentMsgKey agentMsgKey 表示 Agent 响应的所在自定义键的 {@link String}。
     * @throws IllegalArgumentException
     * <ln>
     *     <li>当 {@code toolProvider} 、 {@code chatStreamModel} 和 {@code options} 任一个为 {@code null} 时。</li>
     *     <li>当 {@code agentMsgKey} 为 {@code null} 、空字符串或只有空白字符的字符串时。</li>
     * </ln>
     */
    public DefaultStreamAgent(ToolProvider toolProvider, ChatModel chatStreamModel, ChatOption option,
            String agentMsgKey) {
        this.toolProvider = Validation.notNull(toolProvider, "The tool provider cannot be null.");
        this.model = new ChatStreamModel(chatStreamModel, option);
        this.agentMsgKey = Validation.notBlank(agentMsgKey, "The agent message key cannot be blank.");
    }

    @Override
    protected AiProcessFlow<Prompt, Prompt> buildFlow() {
        // 流式model有内置window节点，无法直接跳转到model节点
        return AiFlows.<Prompt>create()
                .just((input, ctx) -> ctx.setState(this.agentMsgKey, ChatMessages.from(input.messages())))
                .id(GOTO_NODE_ID)
                .generate(this.model)
                .reduce(ChatChunk::new, AbstractAgent::defaultReduce)
                .delegate(this::handleTool)
                .conditions()
                .matchTo(message -> CollectionUtils.isNotEmpty(message.toolCalls()),
                        node -> node.map(this::getAgentMsg).to(GOTO_NODE_ID))
                .others(node -> node.map(this::getAgentMsg))
                .close();
    }

    private Prompt getAgentMsg(ChatMessage input, StateContext ctx) {
        Validation.notNull(ctx, "The state context cannot be null.");
        return ctx.getState(this.agentMsgKey);
    }

    private ChatMessage handleTool(ChatMessage input, StateContext ctx) {
        Validation.notNull(ctx, "The state context cannot be null.");

        ChatMessages lastRequest = ctx.getState(this.agentMsgKey);
        lastRequest.add(Validation.notNull(input, "The input message cannot be null."));
        lastRequest.addAll(toolCallHandle(this.toolProvider, input, Collections.emptyMap()).messages());
        return input;
    }
}
