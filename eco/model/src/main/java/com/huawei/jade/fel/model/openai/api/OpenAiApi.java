/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai.api;

import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionRequest;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionResponse;
import com.huawei.jade.fel.model.openai.entity.embed.OpenAiEmbeddingRequest;
import com.huawei.jade.fel.model.openai.entity.embed.OpenAiEmbeddingResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * 提供 OpenAI 客户端接口：发送 OpenAI API 格式的请求并接收响应。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-04-30
 */
public interface OpenAiApi {
    /**
     * 会话补全请求的端点
     */
    String CHAT_ENDPOINT = "/v1/chat/completions";

    /**
     * Embedding 请求的端点
     */
    String EMBEDDING_ENDPOINT = "/v1/embeddings";

    /**
     * 发送文本补全请求至模型。
     *
     * @param request OpenAI API格式的会话补全请求。
     * @return OpenAI API格式的响应。
     */
    @POST(CHAT_ENDPOINT)
    Call<OpenAiChatCompletionResponse> createChatCompletion(@Body OpenAiChatCompletionRequest request);

    /**
     * 发送文本补全请求至模型，支持动态配置url。
     *
     * @param url 模型地址
     * @param request OpenAI API格式的会话补全请求。
     * @return OpenAI API格式的响应。
     */
    @POST
    Call<OpenAiChatCompletionResponse> createChatCompletion(@Url String url, @Body OpenAiChatCompletionRequest request);

    /**
     * 发送 embedding 请求至模型。
     *
     * @param request OpenAI API格式的 embedding 请求。
     * @return OpenAI API格式的 embedding 响应。
     */
    @POST(EMBEDDING_ENDPOINT)
    Call<OpenAiEmbeddingResponse> createEmbeddings(@Body OpenAiEmbeddingRequest request);

    /**
     * 发送 embedding 请求至模型，支持动态配置url。
     *
     * @param url 模型地址
     * @param request OpenAI API格式的 embedding 请求。
     * @return OpenAI API格式的 embedding 响应。
     */
    @POST
    Call<OpenAiEmbeddingResponse> createEmbeddings(@Url String url, @Body OpenAiEmbeddingRequest request);
}
