/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import com.huawei.fit.jober.aipp.common.exception.AippJsonDecodeException;
import com.huawei.fit.jober.aipp.service.LLMService;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.hllm.model.LlmModel;

import java.io.IOException;
import java.util.Stack;

/**
 * 为大模型提供一些附加功能
 *
 * @author x00649642
 * @since 2024-01-26
 */
public class LLMUtils {
    private static final Logger log = Logger.get(LLMUtils.class);

    public static String askModelForJson(LLMService llmService, String prompt, int maxToken, LlmModel model)
            throws AippJsonDecodeException {
        try {
            String answer = llmService.askModelWithText(prompt, maxToken, 0.2d, model);
            return tryFixLlmJsonString(answer);
        } catch (IOException e) {
            log.error("fail on llm service. reason: {}", e.getMessage());
            throw new AippJsonDecodeException(e.getMessage());
        }
    }

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
}
