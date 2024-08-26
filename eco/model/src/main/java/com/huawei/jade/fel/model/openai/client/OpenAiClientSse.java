/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai.client;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.security.Decryptor;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.resource.UrlUtils;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.LazyLoader;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.model.openai.api.OpenAiApi;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionRequest;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionResponse;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * OpenAI SSE 流式请求客户端。
 *
 * @author 刘信宏
 * @since 2024-08-22
 */
@Component
public class OpenAiClientSse {
    private static final Logger log = Logger.get(OpenAiClientSse.class);
    private static final int HTTP_CLIENT_TIMEOUT = 60 * 1000;
    private static final Map<String, Boolean> HTTPS_CONFIG_KEY_MAPS = MapBuilder.<String, Boolean>get()
            .put("client.http.secure.ignore-trust", Boolean.FALSE)
            .put("client.http.secure.ignore-hostname", Boolean.FALSE)
            .put("client.http.secure.trust-store-file", Boolean.FALSE)
            .put("client.http.secure.trust-store-password", Boolean.TRUE)
            .put("client.http.secure.key-store-file", Boolean.FALSE)
            .put("client.http.secure.key-store-password", Boolean.TRUE)
            .build();

    private final HttpClassicClientFactory httpClientFactory;
    private final Config config;
    private final Decryptor decryptor;
    private final ObjectSerializer serializer;
    private final BeanContainer container;
    private LazyLoader<HttpClassicClient> httpClient;
    private String baseUrl;

    /**
     * 构造 {@link OpenAiClientSse}。
     *
     * @param baseUrl 表示大模型服务端地址的 {@link String}。
     * @param config 表示配置信息的 {@link Config}。
     * @param httpClientFactory 表示 {@link HttpClassicClient} 的工厂类的 {@link HttpClassicClientFactory}。
     * @param serializer 表示对象序列化器的 {@link ObjectSerializer}。
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     */
    public OpenAiClientSse(@Value("${openai-url}") String baseUrl, Config config,
            HttpClassicClientFactory httpClientFactory, ObjectSerializer serializer, BeanContainer container) {
        this.baseUrl = baseUrl;
        this.httpClientFactory = httpClientFactory;
        this.config = config;
        this.container = container;
        this.decryptor = this.container.lookup(Decryptor.class)
                .map(BeanFactory::<Decryptor>get)
                .orElseGet(() -> encrypted -> encrypted);
        this.serializer = serializer;
        this.httpClient = new LazyLoader<>(this::getHttpClient);
    }

    /**
     * 表示流式请求。
     *
     * @param openaiRequest 表示请求体的 {@link OpenAiChatCompletionRequest}。
     * @return 表示流式响应的 {@link Choir}{@code <}{@link FlatChatMessage}{@code >}。
     */
    public Choir<FlatChatMessage> generate(OpenAiChatCompletionRequest openaiRequest) {
        notNull(openaiRequest, "The openai request cannot be null.");
        HttpClassicClientRequest request = this.httpClient.get()
                .createRequest(HttpRequestMethod.POST, UrlUtils.combine(this.baseUrl, OpenAiApi.CHAT_ENDPOINT));
        openaiRequest.setStream(true);
        request.jsonEntity(openaiRequest);
        return this.createChatStream(request);
    }

    private Choir<FlatChatMessage> createChatStream(HttpClassicClientRequest request) {
        return request.<String>exchangeStream(String.class)
                .filter(str -> !StringUtils.equals(str, "[DONE]"))
                .map(str -> this.serializer.<OpenAiChatCompletionResponse>deserialize(str,
                        OpenAiChatCompletionResponse.class))
                .map(OpenAiChatCompletionResponse::message)
                .map(FlatChatMessage::from);
    }

    private HttpClassicClient getHttpClient() {
        Map<String, Object> custom = HTTPS_CONFIG_KEY_MAPS.keySet()
                .stream()
                .filter(key -> config.keys().contains(Config.canonicalizeKey(key)))
                .collect(Collectors.toMap(key -> key, key -> {
                    Object value = this.config.get(key, Object.class);
                    if (HTTPS_CONFIG_KEY_MAPS.get(key).booleanValue()) {
                        value = this.decryptor.decrypt(ObjectUtils.cast(value));
                    }
                    return value;
                }));

        return this.httpClientFactory.create(HttpClassicClientFactory.Config.builder()
                .custom(custom)
                .connectTimeout(HTTP_CLIENT_TIMEOUT)
                .socketTimeout(HTTP_CLIENT_TIMEOUT)
                .build());
    }
}
