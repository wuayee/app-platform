/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.model.openai.service;

import modelengine.fel.embed.EmbedModelService;
import modelengine.fel.embed.EmbedRequest;
import modelengine.fel.embed.EmbedResponse;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;
import modelengine.fel.model.openai.client.OpenAiClient;
import modelengine.fel.model.openai.entity.embed.OpenAiEmbedding;
import modelengine.fel.model.openai.entity.embed.OpenAiEmbeddingRequest;
import modelengine.fel.model.openai.entity.embed.OpenAiEmbeddingResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FEL embedding 接口的 OpenAI 实现。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@Component
public class OpenAiEmbedModelService implements EmbedModelService {
    private static final Logger LOGGER = Logger.get(OpenAiEmbedModelService.class);

    private OpenAiClient openAiClient;

    public OpenAiEmbedModelService(OpenAiClient client) {
        this.openAiClient = client;
    }

    /**
     * 将 FEL 输入转换为 OpenAI API 格式的 embedding 请求发送给大模型，并将大模型响应转换为 FEL embedding 格式的响应。
     *
     * @param request 表示嵌入请求的 {@link EmbedRequest} 。
     * @return 表示模型生成嵌入响应的 {@link EmbedResponse} 。
     */
    @Override
    @Fitable(id = "com.huawei.fit.jade.model.client.openai.embed.generate")
    public EmbedResponse generate(EmbedRequest request) {
        Validation.notNull(request, "Failed to generate embedding response: request is null.");
        Validation.notNull(request.getOptions(), "Failed to generate embedding response: request option is null.");
        String model = "";
        if (StringUtils.isNotBlank(request.getOptions().getModel())) {
            model = request.getOptions().getModel();
        } else {
            LOGGER.warn("Empty model name");
        }

        OpenAiEmbeddingRequest r = OpenAiEmbeddingRequest.builder()
                .model(model)
                .input(request.getInputs())
                .apiKey(request.getOptions().getApiKey())
                .build();

        try {
            OpenAiEmbeddingResponse response = this.openAiClient.createEmbeddings(r);

            return getEmbedResponse(response);
        } catch (IOException e) {
            LOGGER.error(e.toString());
            return new EmbedResponse();
        }
    }

    private EmbedResponse getEmbedResponse(OpenAiEmbeddingResponse response) {
        EmbedResponse result = new EmbedResponse();
        List<List<Float>> embeddings = new ArrayList<>();

        for (OpenAiEmbedding e : response.getData()) {
            embeddings.add(e.getEmbedding());
        }
        result.setEmbeddings(embeddings);
        result.setInputTokens(response.getUsage().getPromptTokens());
        return result;
    }
}
