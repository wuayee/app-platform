/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.engine.operators.patterns;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.support.AiMessage;
import modelengine.fel.core.chat.support.ChatMessages;
import modelengine.fel.core.tool.ToolCall;
import modelengine.fel.engine.flows.AiFlows;
import modelengine.fel.engine.flows.AiProcessFlow;
import modelengine.fel.engine.operators.models.ChatFlowModel;
import modelengine.fel.engine.util.AiFlowSession;
import modelengine.fit.waterflow.domain.context.StateContext;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Agent 基类。
 *
 * @author 刘信宏
 * @since 2024-04-12
 */
public abstract class AbstractAgent extends AbstractFlowPattern<Prompt, ChatMessage> {
    private static final String AGENT_MEMORY = "agent_memory";
    private static final String LLM_NODE = "ahead_llm_node";

    private final ChatFlowModel model;
    private final String memoryId;

    /**
     * 使用工具提供者和大模型服务对象初始化 {@link AbstractAgent}。
     *
     * @param model 表示聊天流式模型推理服务对象的 {@link ChatModel}。
     * @param option 表示聊天大模型超参数的 {@link ChatOption}
     * @throws IllegalArgumentException 当 {@code toolProvider} 、 {@code chatStreamModel} 和 {@code options}
     * 任一个为 {@code null} 时。
     */
    public AbstractAgent(ChatModel model, ChatOption option) {
        this(model, option, AGENT_MEMORY);
    }

    /**
     * 使用工具提供者和大模型服务对象初始化 {@link AbstractAgent}。
     *
     * @param model 表示模型推理服务的 {@link ChatModel}。
     * @param option 表示聊天大模型超参数的 {@link ChatOption}
     * @param memoryId agentMsgKey 表示 Agent 响应的所在自定义键的 {@link String}。
     * @throws IllegalArgumentException <ln>
     * <li>当 {@code toolProvider} 、 {@code chatStreamModel} 和 {@code options} 任一个为 {@code null} 时。</li>
     * <li>当 {@code agentMsgKey} 为 {@code null} 、空字符串或只有空白字符的字符串时。</li>
     * </ln>
     */
    public AbstractAgent(ChatModel model, ChatOption option, String memoryId) {
        this.model = new ChatFlowModel(model, option);
        this.memoryId = notBlank(memoryId, "The agent message key cannot be blank.");
    }

    /**
     * 获取 Agent 缓存的会话消息。
     *
     * @return 表示会话消息的 {@link Prompt}。
     */
    public Prompt getAgentMemory() {
        return AiFlowSession.get()
                .map(session -> session.<ChatMessages>getState(this.memoryId))
                .orElseGet(ChatMessages::new);
    }

    /**
     * 执行工具调用。
     *
     * @param toolCalls 表示工具调用的 {@link List}{@code <}{@link ToolCall}{@code >}。
     * @return 表示工具调用结果的 {@link Prompt}。
     */
    protected abstract Prompt doToolCall(List<ToolCall> toolCalls);

    @Override
    protected AiProcessFlow<Prompt, ChatMessage> buildFlow() {
        return AiFlows.<Prompt>create()
                .just((input, ctx) -> ctx.setState(this.memoryId, ChatMessages.from(input)))
                .id(LLM_NODE)
                .generate(this.model)
                .conditions()
                .matchTo(ChatMessage::isToolCall,
                        node -> node.reduce(() -> new AiMessage(StringUtils.EMPTY, new ArrayList<>()), (acc, input) -> {
                            acc.toolCalls().addAll(input.toolCalls());
                            return acc;
                        }).just(this::handleTool).map(ignored -> getAgentMemory()).to(LLM_NODE))
                .others(node -> node)
                .close();
    }

    private void handleTool(ChatMessage message, StateContext ctx) {
        ChatMessages lastRequest = ctx.getState(this.memoryId);
        lastRequest.add(message);
        lastRequest.addAll(this.doToolCall(message.toolCalls()).messages());
    }
}
