/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.patterns;

import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.chat.ChatModelStreamService;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.chat.content.Media;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.operators.models.ChatChunk;
import com.huawei.jade.fel.engine.operators.models.ChatStreamModel;
import com.huawei.jade.fel.tool.ToolCall;
import com.huawei.jade.fel.tool.ToolProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link Agent} 的默认流式实现。
 *
 * @author 刘信宏
 * @since 2024-05-17
 */
public class DefaultStreamAgent extends Agent<Prompt, Prompt> {
    private static final String AGENT_MSG_KEY = "stream_agent_request";

    /**
     * 使用工具提供者和大模型服务对象初始化 {@link DefaultStreamAgent}。
     *
     * @param toolProvider 表示工具提供者的 {@link ToolProvider}。
     * @param chatStreamModel 表示聊天流式模型推理服务对象的 {@link ChatModelStreamService}。
     * @param options 表示聊天大模型超参数的 {@link ChatOptions}
     * @throws IllegalArgumentException 当 {@code toolProvider} 、 {@code chatStreamModel} 和 {@code options}
     * 任一个为 {@code null} 时。
     */
    public DefaultStreamAgent(ToolProvider toolProvider, ChatModelStreamService chatStreamModel, ChatOptions options) {
        this(toolProvider, chatStreamModel, options, AGENT_MSG_KEY);
    }

    /**
     * 使用工具提供者和大模型服务对象初始化 {@link DefaultStreamAgent}。
     *
     * @param toolProvider 表示工具提供者的 {@link ToolProvider}。
     * @param chatStreamModel 表示聊天流式模型推理服务对象的 {@link ChatModelStreamService}。
     * @param options 表示聊天大模型超参数的 {@link ChatOptions}
     * @param agentMsgKey agentMsgKey 表示 Agent 响应的所在自定义键的 {@link String}。
     * @throws IllegalArgumentException
     * <ln>
     *     <li>当 {@code toolProvider} 、 {@code chatStreamModel} 和 {@code options} 任一个为 {@code null} 时。</li>
     *     <li>当 {@code agentMsgKey} 为 {@code null} 、空字符串或只有空白字符的字符串时。</li>
     * </ln>
     */
    public DefaultStreamAgent(ToolProvider toolProvider, ChatModelStreamService chatStreamModel, ChatOptions options,
            String agentMsgKey) {
        super(() -> buildFlow(toolProvider, new ChatStreamModel<>(chatStreamModel, options), agentMsgKey));
    }

    private static AiProcessFlow<Prompt, Prompt> buildFlow(ToolProvider toolProvider, ChatStreamModel<Prompt> model,
            String agentMsgKey) {
        Validation.notNull(toolProvider, "Tool provider cannot be null.");
        Validation.notBlank(agentMsgKey, "Agent message key cannot be blank.");

        // 流式model有内置window节点，无法直接跳转到model节点
        return AiFlows.<Prompt>create()
                .just(Agent.putAgentMsg(agentMsgKey)).id("aheadLlm")
                .generate(model)
                .reduce(new ChatChunk(StringUtils.EMPTY), getReduceProcessor())
                .delegate(Agent.getToolProcessMap(toolProvider, agentMsgKey))
                .conditions()
                .match(input -> Agent.isFinish(toolProvider, input), node -> node.map(Agent.getAgentMsg(agentMsgKey)))
                .matchTo(AiMessage::isToolCall, node -> node.map(Agent.getAgentMsg(agentMsgKey)).to("aheadLlm"))
                .others()
                .close();
    }

    private static Operators.Reduce<ChatChunk, AiMessage> getReduceProcessor() {
        return (acc, input) -> {
            if (input.isEnd()) {
                return acc;
            }
            String text = acc.text() + input.text();
            List<Media> medias = Stream.concat(acc.medias().stream(), input.medias().stream())
                    .collect(Collectors.toList());
            List<ToolCall> toolCalls = new ArrayList<>(acc.toolCalls());
            toolCalls.addAll(input.toolCalls());
            acc = new ChatChunk(text, medias, toolCalls);
            return acc;
        };
    }
}
