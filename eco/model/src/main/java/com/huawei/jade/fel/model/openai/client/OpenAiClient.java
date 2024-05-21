/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai.client;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.jade.fel.model.openai.api.OpenAiApi;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionRequest;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionResponse;
import com.huawei.jade.fel.model.openai.entity.embed.OpenAiEmbeddingRequest;
import com.huawei.jade.fel.model.openai.entity.embed.OpenAiEmbeddingResponse;
import com.huawei.jade.fel.model.openai.utils.OpenAiMessageUtils;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * OpenAI 客户端，对 {@link OpenAiApi} 接口进行了一层封装，便于以对象形式进行接口调用。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@Component
public class OpenAiClient {
    /**
     * 客户端请求超时，暂定为 60 秒。
     */
    public static final int HTTP_CLIENT_TIMEOUT = 60;

    private static final Logger LOGGER = Logger.get(OpenAiClient.class);

    private final OpenAiApi api;

    private String openAiBaseUrl;

    private Boolean shouldSplicePath;

    /**
     * OpenAiClient 构造方法。
     *
     * @param baseUrl 大模型服务端地址。
     * @param shouldSplicePath 如果为 {@code true} ，那么会在 {@link OpenAiClient#openAiBaseUrl} 后拼接模型名称。
     */
    public OpenAiClient(@Value("${openai-url}") String baseUrl,
                        @Value("${url-path-splicing}") Boolean shouldSplicePath) {
        this.openAiBaseUrl = Validation.notBlank(baseUrl, "The OpenAI base URL is empty.");
        this.shouldSplicePath = Validation.notNull(shouldSplicePath, "The url-path-splicing option is null.");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(OpenAiClient.HTTP_CLIENT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(OpenAiClient.HTTP_CLIENT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().client(client)
                .baseUrl(this.openAiBaseUrl)
                .addConverterFactory(JacksonConverterFactory.create(OpenAiMessageUtils.OBJECT_MAPPER))
                .build();
        this.api = retrofit.create(OpenAiApi.class);
    }

    /**
     * 发送会话补全请求至大模型。
     *
     * @param request OpenAI API 格式的会话补全请求。
     * @return OpenAI API 格式的会话补全响应。
     * @throws IOException 响应未成功。
     */
    public OpenAiChatCompletionResponse createChatCompletion(OpenAiChatCompletionRequest request)
            throws IOException {
        Validation.notNull(request, "The request cannot be null");
        return createChatCompletion(getUrl(request.getModel(), OpenAiApi.CHAT_ENDPOINT), request);
    }

    /**
     * 发送会话补全请求至大模型。
     *
     * @param url 用户指定的模型地址。
     * @param request OpenAI API 格式的会话补全请求。
     * @return OpenAI API 格式的会话补全响应。
     * @throws IOException 响应未成功。
     */
    public OpenAiChatCompletionResponse createChatCompletion(String url, OpenAiChatCompletionRequest request)
            throws IOException {
        Validation.notNull(request, "The request cannot be null");
        request.setStream(false);
        Response<OpenAiChatCompletionResponse> response =
                api.createChatCompletion(url, request).execute();
        if (!response.isSuccessful()) {
            LOGGER.error(response.message());
            throw new IOException(response.message());
        }
        return response.body();
    }

    /**
     * 发送 embedding 请求至大模型。
     *
     * @param request OpenAI 格式的请求。
     * @return OpenAI 格式的 embedding 响应。
     * @throws IOException 响应未成功。
     */
    public OpenAiEmbeddingResponse createEmbeddings(OpenAiEmbeddingRequest request) throws IOException {
        Validation.notNull(request, "The request cannot be null");
        return createEmbeddings(getUrl(request.getModel(), OpenAiApi.EMBEDDING_ENDPOINT), request);
    }

    /**
     * 发送 embedding 请求至大模型。
     *
     * @param url 用户指定的模型地址。
     * @param request OpenAI 格式的请求。
     * @return OpenAI 格式的 embedding 响应。
     * @throws IOException 响应未成功。
     */
    public OpenAiEmbeddingResponse createEmbeddings(String url, OpenAiEmbeddingRequest request) throws IOException {
        Validation.notNull(request, "The request cannot be null");
        Response<OpenAiEmbeddingResponse> response =
                api.createEmbeddings(url, request).execute();
        if (!response.isSuccessful()) {
            LOGGER.error(response.message());
            throw new IOException(response.message());
        }
        return response.body();
    }

    /**
     * 发送会话补全请求至大模型（流式响应）。
     *
     * @param request OpenAI API 格式的会话补全请求。
     * @return OpenAI API 格式的会话补全流式响应。
     */
    public Call<ResponseBody> createChatCompletionStream(OpenAiChatCompletionRequest request) {
        Validation.notNull(request, "The request cannot be null");
        return createChatCompletionStream(getUrl(request.getModel(), OpenAiApi.CHAT_ENDPOINT), request);
    }

    /**
     * 发送会话补全请求至大模型（流式响应）。
     *
     * @param url 用户指定的模型地址。
     * @param request OpenAI API 格式的会话补全请求。
     * @return OpenAI API 格式的会话补全流式响应。
     */
    public Call<ResponseBody> createChatCompletionStream(String url, OpenAiChatCompletionRequest request) {
        Validation.notNull(request, "The request cannot be null");
        request.setStream(true);
        return api.createChatCompletionStream(url, request);
    }

    private String getUrl(String modelName, String endpoint) {
        Validation.notBlank(modelName, "The model name cannot be blank.");
        Validation.notNull(endpoint, "The endpoint cannot be null.");
        String url = shouldSplicePath ? openAiBaseUrl + modelName + "/" : openAiBaseUrl;
        url += endpoint;
        return url;
    }
}
