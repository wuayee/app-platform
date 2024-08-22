/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fel;

import com.huawei.fit.jober.aipp.constants.AippConst;
import modelengine.fitframework.annotation.Bean;
import modelengine.fitframework.annotation.Component;
import modelengine.fel.chat.ChatModelStreamService;
import modelengine.fel.chat.ChatOptions;
import modelengine.fel.chat.Prompt;
import modelengine.fel.engine.operators.patterns.AbstractAgent;
import modelengine.fel.tool.ToolProvider;

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
     * @param chatModel 表示模型流式服务的 {@link ChatModelStreamService}。
     * @return 返回 WaterFlow 场景的 Agent 服务的 {@link AbstractAgent}{@code <}{@link Prompt}{@code ,
     * }{@link Prompt}{@code >}。
     */
    @Bean(AippConst.WATER_FLOW_AGENT_BEAN)
    public AbstractAgent<Prompt, Prompt> getWaterFlowAgent(ToolProvider toolProvider,
            ChatModelStreamService chatModel) {
        return new WaterFlowAgent(toolProvider, chatModel, ChatOptions.builder().temperature(0.0D).build());
    }
}