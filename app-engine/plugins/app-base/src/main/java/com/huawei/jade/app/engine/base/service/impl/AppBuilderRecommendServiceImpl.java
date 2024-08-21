/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.jade.app.engine.base.service.impl;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.SerializationException;
import com.huawei.jade.app.engine.base.service.AppBuilderRecommendService;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatModelService;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.core.util.Tip;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.operators.models.ChatBlockModel;
import com.huawei.jade.fel.engine.operators.prompts.Prompts;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * 猜你想问serviceImpl
 *
 * @author 杨海波
 * @since 2024-05-25
 */
@Component
public class AppBuilderRecommendServiceImpl implements AppBuilderRecommendService {
    private static final Logger log = Logger.get(AppBuilderRecommendServiceImpl.class);

    private final ChatModelService chatModelService;

    public AppBuilderRecommendServiceImpl(ChatModelService chatModelService) {
        this.chatModelService = chatModelService;
    }

    @Override
    public List<String> queryRecommends(String ques, String ans, String model) {
        String historyPrompt = "Here are the chat histories between user and assistant, "
                + "inside <history></history> XML tags.\n<history>\n{{history}}\n</history>\n\n";

        String recommendPrompt = "Please predict the three most likely questions that human would ask, "
                + "and keeping each question under 20 characters.\n"
                + "Do not include any explanations, "
                + "only provide output that strictly following the specified JSON format:\n"
                + "[\"question1\",\"question2\",\"question3\"]\n";

        List<String> res;
        try {
            AiProcessFlow<Tip, String> flow = AiFlows.<Tip>create()
                .prompt(Prompts.human(historyPrompt + recommendPrompt))
                .generate(new ChatBlockModel(chatModelService)
                        .bind(ChatOptions.builder()
                                .model(model)
                                .temperature(0.3)
                                .build()))
                .map(ChatMessage::text)
                .close();

            String chatHistory = "User: " + ques + '\n' + "Assistant: " + ans + '\n';
            String response = flow.converse().offer(Tip.from("history", chatHistory)).await();

            res = JSONArray.parseArray(response, String.class);
        } catch (SerializationException | JSONException | IllegalStateException e) {
            log.error("{}\nparse model {} response error", e.getMessage(), model);
            return new ArrayList<>();
        }

        return res;
    }
}
