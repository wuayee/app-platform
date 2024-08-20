/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.community.model.openai;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notEmpty;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.entity.ObjectEntity;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.resource.UrlUtils;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.community.model.openai.api.OpenAiApi;
import com.huawei.jade.fel.community.model.openai.config.OpenAiConfig;
import com.huawei.jade.fel.community.model.openai.entity.chat.OpenAiChatCompletionRequest;
import com.huawei.jade.fel.community.model.openai.entity.chat.OpenAiChatCompletionResponse;
import com.huawei.jade.fel.community.model.openai.entity.embed.OpenAiEmbedding;
import com.huawei.jade.fel.community.model.openai.entity.embed.OpenAiEmbeddingRequest;
import com.huawei.jade.fel.community.model.openai.entity.embed.OpenAiEmbeddingResponse;
import com.huawei.jade.fel.community.model.openai.util.HttpUtils;
import com.huawei.jade.fel.core.chat.ChatMessage;
import com.huawei.jade.fel.core.chat.ChatModel;
import com.huawei.jade.fel.core.chat.ChatOption;
import com.huawei.jade.fel.core.chat.Prompt;
import com.huawei.jade.fel.core.embed.EmbedModel;
import com.huawei.jade.fel.core.embed.EmbedOption;
import com.huawei.jade.fel.core.embed.Embedding;

import java.io.IOException;
import java.util.List;

/**
 * 表示 openai 模型服务。
 *
 * @author 易文渊
 * @since 2024-08-07
 */
@Component
public class OpenAiModel implements EmbedModel, ChatModel {
    private final HttpClassicClientFactory httpClientFactory;
    private final HttpClassicClientFactory.Config config;
    private final String baseUrl;
    private final String defaultApiKey;
    private final ObjectSerializer serializer;

    /**
     * 创建 openai 嵌入模型服务的实例。
     *
     * @param httpClientFactory 表示 http 客户端工厂的 {@link HttpClassicClientFactory}。
     * @param config 表示 openai http 配置的 {@link OpenAiConfig}。
     * @param serializer 表示对象序列化器的 {@link ObjectSerializer}。
     * @throws IllegalArgumentException 当 {@code httpClientFactory}、{@code config} 为 {@code null} 时。
     */
    public OpenAiModel(HttpClassicClientFactory httpClientFactory, OpenAiConfig config, ObjectSerializer serializer) {
        notNull(config, "The config cannot be null.");
        this.httpClientFactory = notNull(httpClientFactory, "The http client factory cannot be null.");
        this.config = HttpClassicClientFactory.Config.builder()
                .connectTimeout(config.getConnectTimeout())
                .socketTimeout(config.getReadTimeout())
                .build();
        this.serializer = notNull(serializer, "The serializer cannot be null.");
        this.baseUrl = config.getApiBase();
        this.defaultApiKey = config.getApiKey();
    }

    @Override
    public List<Embedding> generate(List<String> inputs, EmbedOption option) {
        notEmpty(inputs, "The input cannot be empty.");
        notNull(option, "The embed option cannot be null.");
        notBlank(option.model(), "The embed model name cannot be null.");
        HttpClassicClientRequest request = this.httpClientFactory.create(this.config)
                .createRequest(HttpRequestMethod.POST, UrlUtils.combine(this.baseUrl, OpenAiApi.EMBEDDING_ENDPOINT));
        HttpUtils.setBearerAuth(request, StringUtils.blankIf(option.apiKey(), this.defaultApiKey));
        request.jsonEntity(new OpenAiEmbeddingRequest(inputs, option.model()));
        Class<OpenAiEmbeddingResponse> clazz = OpenAiEmbeddingResponse.class;
        try (HttpClassicClientResponse<OpenAiEmbeddingResponse> response = request.exchange(clazz)) {
            return response.objectEntity()
                    .map(entity -> CollectionUtils.<Embedding, OpenAiEmbedding>asParent(entity.object().data()))
                    .orElseThrow(() -> new FitException("The response body is abnormal."));
        } catch (IOException e) {
            throw new FitException(e);
        }
    }

    @Override
    public Choir<ChatMessage> generate(Prompt prompt, ChatOption chatOption) {
        notNull(prompt, "The prompt cannot be null.");
        notNull(chatOption, "The chat option cannot be null.");
        HttpClassicClientRequest request = this.httpClientFactory.create(this.config)
                .createRequest(HttpRequestMethod.POST, UrlUtils.combine(this.baseUrl, OpenAiApi.CHAT_ENDPOINT));
        HttpUtils.setBearerAuth(request, StringUtils.blankIf(chatOption.apiKey(), this.defaultApiKey));
        request.jsonEntity(new OpenAiChatCompletionRequest(prompt, chatOption));
        return chatOption.stream() ? this.createChatStream(request) : this.createChatCompletion(request);
    }

    private Choir<ChatMessage> createChatStream(HttpClassicClientRequest request) {
        return request.<String>exchangeStream(String.class)
                .filter(str -> !StringUtils.equals(str, "[DONE]"))
                .map(str -> this.serializer.<OpenAiChatCompletionResponse>deserialize(str,
                        OpenAiChatCompletionResponse.class))
                .map(OpenAiChatCompletionResponse::message);
    }

    private Choir<ChatMessage> createChatCompletion(HttpClassicClientRequest request) {
        try (HttpClassicClientResponse<OpenAiChatCompletionResponse> response = request.exchange(
                OpenAiChatCompletionResponse.class)) {
            OpenAiChatCompletionResponse chatCompletionResponse = response.objectEntity()
                    .map(ObjectEntity::object)
                    .orElseThrow(() -> new FitException("The response body is abnormal."));
            return Choir.just(chatCompletionResponse.message());
        } catch (IOException e) {
            throw new FitException(e);
        }
    }
}