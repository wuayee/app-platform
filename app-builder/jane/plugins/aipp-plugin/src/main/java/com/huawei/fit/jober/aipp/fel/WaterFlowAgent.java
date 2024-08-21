/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fel;

import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.waterflow.domain.context.StateContext;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatMessages;
import com.huawei.jade.fel.chat.ChatModelStreamService;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.operators.models.ChatChunk;
import com.huawei.jade.fel.engine.operators.models.ChatStreamModel;
import com.huawei.jade.fel.engine.operators.patterns.AbstractAgent;
import com.huawei.jade.fel.tool.ToolCall;
import com.huawei.jade.fel.tool.ToolProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * WaterFlow 场景的 {@link AbstractAgent} 实现。
 *
 * @author 刘信宏
 * @since 2024-06-04
 */
public class WaterFlowAgent extends AbstractAgent<Prompt, Prompt> {
    private static final String AGENT_MSG_KEY = "water_flow_agent_request";
    private static final String GOTO_NODE_ID = "ahead_llm_node";

    private final ToolProvider toolProvider;
    private final ChatStreamModel model;
    private final String agentMsgKey;

    public WaterFlowAgent(ToolProvider toolProvider, ChatModelStreamService chatStreamModel, ChatOptions options) {
        this.toolProvider = Validation.notNull(toolProvider, "The tool provider cannot be null.");
        this.model = new ChatStreamModel(chatStreamModel, options);
        this.agentMsgKey = AGENT_MSG_KEY;
    }

    @Override
    protected AiProcessFlow<Prompt, Prompt> buildFlow() {
        return AiFlows.<Prompt>create()
                .just((input, ctx) -> ctx.setState(this.agentMsgKey, ChatMessages.from(input.messages())))
                .id(GOTO_NODE_ID)
                .generate(this.model)
                .reduce(ChatChunk::new, AbstractAgent::defaultReduce)
                .delegate(this::handleTool)
                .conditions()
                .matchTo(this::shouldRepeated, node -> node.map(this::getAgentMsg).to(GOTO_NODE_ID))
                .others(node -> node.map(this::getAgentMsg))
                .close();
    }

    private ChatMessage handleTool(ChatMessage input, StateContext ctx) {
        Validation.notNull(ctx, "The state context cannot be null.");

        Map<String, Object> toolContext =
                ObjectUtils.getIfNull(ctx.getState(AippConst.TOOL_CONTEXT_KEY), Collections::emptyMap);
        ChatMessages lastRequest = ctx.getState(this.agentMsgKey);
        lastRequest.add(Validation.notNull(input, "The input message cannot be null."));
        lastRequest.addAll(AbstractAgent.toolCallHandle(this.toolProvider, input, toolContext).messages());
        return input;
    }

    private boolean shouldRepeated(ChatMessage message) {
        Validation.notNull(message, "The message cannot be null.");
        return CollectionUtils.isNotEmpty(message.toolCalls()) && !this.containAsyncTool(message);
    }

    private boolean containAsyncTool(ChatMessage message) {
        List<String> toolsName = Optional.ofNullable(message.toolCalls())
                .map(m -> m.stream().map(ToolCall::getName).collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
        if (toolsName.isEmpty()) {
            return false;
        }
        return this.toolProvider.getTool(toolsName).stream().anyMatch(tool -> {
            Map<String, Object> context = tool.getContext();
            if (MapUtils.isEmpty(context)) {
                return false;
            }
            Object isAsyncValue = context.get("isAsync");
            if (isAsyncValue == null) {
                return false;
            }
            return Boolean.parseBoolean(isAsyncValue.toString());
        });
    }

    private Prompt getAgentMsg(ChatMessage input, StateContext ctx) {
        Validation.notNull(ctx, "The state context cannot be null.");
        return ctx.getState(this.agentMsgKey);
    }
}
