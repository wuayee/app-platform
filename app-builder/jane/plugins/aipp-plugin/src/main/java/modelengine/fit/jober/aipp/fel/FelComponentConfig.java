/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.fel;

import modelengine.fit.jober.aipp.constants.AippConst;

import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.engine.operators.patterns.AbstractAgent;
import modelengine.fel.core.tool.ToolProvider;
import modelengine.fitframework.annotation.Bean;
import modelengine.fitframework.annotation.Component;

/**
 * FelComponentConfig
 *
 * @author 易文渊
 * @since 2024-04-23
 */
@Component
public class FelComponentConfig {
    /**
     * 注入 WaterFlow 场景的 Agent。
     *
     * @param toolProvider 表示工具服务的 {@link ToolProvider}。
     * @param chatModel 表示模型流式服务的 {@link ChatModel}。
     * @return 返回 WaterFlow 场景的 Agent 服务的 {@link AbstractAgent}{@code <}{@link Prompt}{@code ,
     * }{@link Prompt}{@code >}。
     */
    @Bean(AippConst.WATER_FLOW_AGENT_BEAN)
    public AbstractAgent<Prompt, Prompt> getWaterFlowAgent(ToolProvider toolProvider,
            ChatModel chatModel) {
        return new WaterFlowAgent(toolProvider, chatModel, ChatOption.custom().temperature(0.0D).build());
    }
}