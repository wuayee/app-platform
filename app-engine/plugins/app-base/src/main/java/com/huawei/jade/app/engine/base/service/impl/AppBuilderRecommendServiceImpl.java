/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.jade.app.engine.base.service.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.SerializationException;
import com.huawei.jade.app.engine.base.service.AppBuilderRecommendService;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatModelService;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.core.util.Tip;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.operators.models.ChatBlockModel;
import com.huawei.jade.fel.engine.operators.prompts.Prompts;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * 猜你想问serviceImpl
 *
 * @author y00858250
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
    public List<String> queryRecommends(String ques, String ans) {
        log.info("query recommends start.\n Last Q: {}\nLast A: {}", ques, ans);
        String historyPrompt = "Here are the chat histories between user and assistant, "
                + "inside <history></history> XML tags.\n<history>\n{{history}}\n</history>\n\n";

        String recommendPrompt = "Please predict the three most likely questions that human would ask, "
                + "and keeping each question under 20 characters.\n"
                + "The output must be an array in JSON format following the specified schema:\n"
                + "[\"question1\",\"question2\",\"question3\"]\n";

        AiProcessFlow<Tip, String> flow = AiFlows.<Tip>create()
                .prompt(Prompts.human(historyPrompt + recommendPrompt))
                .generate(new ChatBlockModel<Prompt>(chatModelService).bind(ChatOptions.builder()
                        .model("Qwen-72B")
                        .build()))
                .map(ChatMessage::text)
                .close();

        List<String> res;
        try {
            String chatHistory = "User: " + ques + '\n' + "Assistant: " + ans + '\n';
            String response = flow.converse().offer(Tip.from("history", chatHistory)).await();

            res = JSONArray.parseArray(response, String.class);
        } catch (SerializationException e) {
            log.error("{}\nparse model response error", e.getMessage());
            return new ArrayList<>();
        }

        return res;
    }
}
