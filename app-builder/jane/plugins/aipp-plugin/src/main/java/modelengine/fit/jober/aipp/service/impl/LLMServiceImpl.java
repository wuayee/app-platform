/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.support.ChatMessages;
import modelengine.fel.core.chat.support.HumanMessage;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.jober.aipp.enums.LlmModelNameEnum;
import modelengine.fit.jober.aipp.service.LLMService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.voice.service.VoiceService;

import java.util.List;

/**
 * 大模型服务实现类
 *
 * @author 孙怡菲
 * @since 2024-05-10
 */
@Component
public class LLMServiceImpl implements LLMService {
    private static final Logger log = Logger.get(LLMServiceImpl.class);

    private final ChatModel chatModel;

    private final String appengineEndPoint;

    private final String pathPrefix;

    private final VoiceService voiceService;

    private final HttpClassicClientFactory httpClientFactory;

    public LLMServiceImpl(@Value("${app-engine.endpoint}") String endpoint,
            @Value("${app-engine.pathPrefix}") String pathPrefix,
            @Fit ChatModel chatModel, @Fit VoiceService voiceService, @Fit HttpClassicClientFactory httpClientFactory) {
        this.chatModel = chatModel;
        this.appengineEndPoint = endpoint;
        this.pathPrefix = pathPrefix;
        this.voiceService = voiceService;
        this.httpClientFactory = httpClientFactory;
    }

    @Override
    public String askModelWithText(String prompt, LlmModelNameEnum model) {
        ChatMessages chatMessages = ChatMessages.from(new HumanMessage(prompt));
        ChatOption option = ChatOption.custom().model(model.getValue()).stream(false).build();
        return askModel(chatMessages, option);
    }

    @Override
    public String askModelWithText(String prompt, int maxTokens, double temperature, LlmModelNameEnum model) {
        ChatMessages chatMessages = ChatMessages.from(new HumanMessage(prompt));
        ChatOption option = ChatOption.custom()
                .model(model.getValue())
                .temperature(temperature)
                .maxTokens(maxTokens)
                .stream(false)
                .build();
        return askModel(chatMessages, option);
    }

    private String askModel(Prompt prompt, ChatOption option) {
        List<ChatMessage> messages = chatModel.generate(prompt, option).blockAll();
        if (CollectionUtils.isEmpty(messages)) {
            log.error("chat model response is empty.");
            return StringUtils.EMPTY;
        }
        String ans = messages.get(0).text();
        log.info("question={} ans={}", ObjectUtils.<String>cast(prompt.messages().get(0).text()), ans);
        return ans;
    }
}
