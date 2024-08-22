/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.model.openai.api;

import modelengine.fel.model.openai.entity.chat.OpenAiChatCompletionRequest;
import modelengine.fel.model.openai.entity.chat.OpenAiChatCompletionResponse;
import modelengine.fel.model.openai.entity.embed.OpenAiEmbeddingRequest;
import modelengine.fel.model.openai.entity.embed.OpenAiEmbeddingResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
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
     * 会话补全请求的端点。
     */
    String CHAT_ENDPOINT = "/v1/chat/completions";

    /**
     * Embedding 请求的端点。
     */
    String EMBEDDING_ENDPOINT = "/v1/embeddings";

    /**
     * 请求头模型密钥字段。
     */
    String AUTHORIZATION = "Authorization";

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
     * @param apiKey 模型接口密钥。
     * @param request OpenAI API格式的会话补全请求。
     * @return OpenAI API格式的响应。
     */
    @POST
    Call<OpenAiChatCompletionResponse> createChatCompletion(@Url String url, @Header(AUTHORIZATION) String apiKey,
                                                            @Body OpenAiChatCompletionRequest request);


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
     * @param apiKey 模型接口密钥。
     * @param request OpenAI API格式的 embedding 请求。
     * @return OpenAI API格式的 embedding 响应。
     */
    @POST
    Call<OpenAiEmbeddingResponse> createEmbeddings(@Url String url, @Header(AUTHORIZATION) String apiKey,
                                                   @Body OpenAiEmbeddingRequest request);


    /**
     * 发送文本补全请求（流式响应）。
     *
     * @param request OpenAI API格式的会话补全请求。
     * @return OpenAI API格式的流式响应。
     */
    @Streaming
    @POST(CHAT_ENDPOINT)
    Call<ResponseBody> createChatCompletionStream(@Body OpenAiChatCompletionRequest request);

    /**
     * 发送文本补全请求（流式响应），支持动态配置url。
     *
     * @param url 模型地址。
     * @param apiKey 模型接口密钥。
     * @param request OpenAI API格式的会话补全请求。
     * @return OpenAI API格式的流式响应。
     */
    @Streaming
    @POST
    Call<ResponseBody> createChatCompletionStream(@Url String url, @Header(AUTHORIZATION) String apiKey,
                                                  @Body OpenAiChatCompletionRequest request);
}
