/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.community.model.openai.api;

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
    String CHAT_ENDPOINT = "/chat/completions";

    /**
     * 嵌入请求的端点。
     */
    String EMBEDDING_ENDPOINT = "/embeddings";

    /**
     * 请求头模型密钥字段。
     */
    String AUTHORIZATION = "Authorization";
}