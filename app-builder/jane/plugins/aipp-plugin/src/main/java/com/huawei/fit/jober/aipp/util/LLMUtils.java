/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import com.huawei.fit.jober.aipp.common.exception.AippJsonDecodeException;
import com.huawei.fit.jober.aipp.enums.LlmModelNameEnum;
import com.huawei.fit.jober.aipp.service.LLMService;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.model.openai.client.OpenAiClient;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionRequest;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionResponse;
import com.huawei.jade.fel.model.openai.entity.chat.message.OpenAiChatMessage;
import com.huawei.jade.fel.model.openai.entity.chat.message.Role;

import java.io.IOException;
import java.util.Collections;
import java.util.Stack;

/**
 * 为大模型提供一些附加功能
 *
 * @author 熊以可
 * @since 2024-01-26
 */
public class LLMUtils {
    private static final Logger log = Logger.get(LLMUtils.class);

    /**
     * 向LLM生成json
     *
     * @param llmService 表示提供LLM服务的{@link LLMService}
     * @param prompt 表示LLM提示词的{@link String}
     * @param maxToken 表示LLM的最大token数量
     * @param model 表示使用的模型类型的{@link LlmModelNameEnum}
     * @return Json结果
     * @throws AippJsonDecodeException 从LLM获得json结果异常
     */
    public static String askModelForJson(LLMService llmService, String prompt, int maxToken, LlmModelNameEnum model)
            throws AippJsonDecodeException {
        try {
            String answer = llmService.askModelWithText(prompt, maxToken, 0.2d, model);
            return tryFixLlmJsonString(answer);
        } catch (IOException e) {
            log.error("fail on llm service. reason: {}", e.getMessage());
            throw new AippJsonDecodeException(e.getMessage());
        }
    }

    /**
     * 尝试修复大模型json字符串
     *
     * @param answer 回答
     * @return json字符串
     * @throws IOException IO异常
     */
    public static String tryFixLlmJsonString(String answer) throws IOException {
        int startIndex = answer.indexOf('{');
        int finalIndex = answer.lastIndexOf('}');
        String jsonString;
        try {
            jsonString = answer.substring(startIndex, finalIndex + 1);  // get rid of ```json at head and ``` at tail
        } catch (IndexOutOfBoundsException e1) {
            log.error("cannot find json root object in {}", answer);
            try {
                jsonString = answer.substring(answer.indexOf('['), answer.lastIndexOf(']') + 1);
            } catch (IndexOutOfBoundsException e2) {
                log.error("cannot find json root array in {}", answer);
                throw new IOException(String.format("bad json: no root object or array found in answer: %s", answer));
            }
        }
        if (JsonUtils.isValidJson(jsonString)) {
            return jsonString;
        }

        jsonString = tryCompleteJson(jsonString);
        if (JsonUtils.isValidJson(jsonString)) {
            return jsonString;
        }
        jsonString = "[" + jsonString + "]";
        // JSONArray的场景可能会有问题
        if (JsonUtils.isValidJson(jsonString)) {
            return jsonString;
        }
        log.error("no way to fix {}", answer);
        return "{}";
    }

    private static String tryCompleteJson(String jsonString) {
        StringBuilder result = new StringBuilder();
        boolean isInString = false;
        boolean isEscaped = false;
        Stack<Character> symbolStack = new Stack<>();
        for (char currentChar : jsonString.toCharArray()) {
            if (isInString) {
                if (currentChar == '\"' && !isEscaped) {
                    isInString = false;
                } else if (currentChar == '\n' && !isEscaped) {
                    result.append("\\n");
                    continue;
                } else if (currentChar == '\\') {
                    isEscaped = !isEscaped;
                } else {
                    isEscaped = false;
                }
            } else {
                if (currentChar == '\"') {
                    isInString = true;
                }
                String dealParentThesisResult = dealParentThesis(symbolStack, currentChar);
                if (!StringUtils.isBlank(dealParentThesisResult)) {
                    log.error("cannot determine the starting symbols for {}. stack:{}. json: \n{}",
                            currentChar,
                            symbolStack,
                            jsonString);
                    return "{}";
                }
            }
            result.append(currentChar);
        }
        if (isInString) {  // string is not closed at the end
            result.append('\"');
        }
        while (!symbolStack.isEmpty()) {
            result.append(symbolStack.pop());
        }
        return result.toString();
    }

    private static String dealParentThesis(Stack<Character> symbolStack, char currentChar) {
        if (currentChar == '{') {
            symbolStack.add('}');
            return StringUtils.EMPTY;
        }
        if (currentChar == '[') {
            symbolStack.add(']');
            return StringUtils.EMPTY;
        }
        if (currentChar == '}' || currentChar == ']') {
            if (!symbolStack.isEmpty() && symbolStack.peek() == currentChar) {
                symbolStack.pop();
            } else {
                return "{}";
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 使用大模型来获得总结
     *
     * @param openAiClient 表示modelio客户端接口{@link OpenAiClient}
     * @param prompt 表示待大模型进行总结的内容的{@link String}
     * @param model 表示大模型类型的{@link LlmModelNameEnum}
     * @param maxTokens 表示大模型的最大tokens的参数的{@link int}
     * @return 大模型的返回结果
     * @throws IOException 大模型处理异常
     */
    public static String askModelForSummary(OpenAiClient openAiClient, String prompt, LlmModelNameEnum model,
            int maxTokens) throws IOException {
        log.info("askModelForSummary with prompt: {}", prompt);
        OpenAiChatMessage msg = OpenAiChatMessage.builder().role(Role.USER.name()).content(prompt).build();
        OpenAiChatCompletionRequest request = OpenAiChatCompletionRequest.builder()
                .model(model.getValue())
                .messages(Collections.singletonList(msg))
                .maxTokens(maxTokens)
                .build();
        OpenAiChatCompletionResponse chatCompletion = openAiClient.createChatCompletion(request);
        if (CollectionUtils.isEmpty(chatCompletion.getChoices())) {
            log.error("openAiClient response has empty choices.");
            return StringUtils.EMPTY;
        }
        return ObjectUtils.cast(chatCompletion.getChoices().get(0).getMessage().getContent());
    }
}