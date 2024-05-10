/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fel;

import com.huawei.fitframework.annotation.Bean;
import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.fel.chat.ChatMessages;
import com.huawei.jade.fel.chat.ChatModelService;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.engine.operators.patterns.Agent;
import com.huawei.jade.fel.engine.operators.patterns.DefaultAgent;
import com.huawei.jade.fel.tool.ToolProvider;

/**
 * FelComponentConfig
 *
 * @author 易文渊
 * @since 2024-04-23
 */
@Component
public class FelComponentConfig {
    /**
     * 注入默认agent。
     *
     * @param toolProvider 表示工具服务的 {@link ToolProvider}。
     * @param chatModel 表示模型服务的 {@link ChatModelService}。
     * @return 返回默认agent服务的 {@link Agent}{@code <}{@link ChatMessages}{@code ,} {@link ChatMessages}{@code >}。
     */
    @Bean("defaultAgent")
    public Agent<Prompt, Prompt> getAgent(ToolProvider toolProvider, ChatModelService chatModel) {
        return new DefaultAgent(toolProvider, chatModel, ChatOptions.builder().temperature(0.0D).build());
    }
}