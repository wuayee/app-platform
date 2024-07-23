/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.utils;

import com.huawei.jade.model.service.gateway.entity.ChatMessage;
import com.huawei.jade.model.service.gateway.entity.FunctionCall;
import com.huawei.jade.model.service.gateway.entity.FunctionCallModel;
import com.huawei.jade.model.service.gateway.entity.FunctionDefinition;
import com.huawei.jade.model.service.gateway.entity.ToolCall;
import com.huawei.jade.model.service.gateway.entity.ToolCallResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 工具方法集合
 *
 * @author 陈思远
 * @since 2024-07-05
 */
@Slf4j
public class ModelMessageUtilis {
    /**
     * 判断是否含有中文字符
     *
     * @param data 输入信息
     * @return 是否包含中文字符
     */
    public static boolean hasChineseChars(String data) {
        return data != null && data.matches(".*[\\u4e00-\\u9fff]+.*");
    }

    /**
     * 按模板组装函数信息
     *
     * @param function 函数定义
     * @return 组装好的函数信息字符串
     */
    public static String getFunctionDescription(FunctionDefinition function) {
        String lineSeparator = System.lineSeparator();
        String toolDescTemplateZh = new StringBuilder().append("### {nameForHuman}").append(lineSeparator)
                .append(lineSeparator).append("{nameForModel}: {descriptionForModel} ")
                .append("输入参数：{parameters} {argsFormat}").toString();

        String toolDescTemplateEn = new StringBuilder().append("### {nameForHuman}").append(lineSeparator)
                .append(lineSeparator).append("{nameForModel}: {descriptionForModel} ")
                .append("Parameters: {parameters} {argsFormat}").toString();
        String toolDesc;
        if (hasChineseChars(function.getName())) {
            toolDesc = toolDescTemplateZh;
        } else {
            toolDesc = toolDescTemplateEn;
        }

        String nameForHuman = Optional.ofNullable(function.getName()).orElse("Unnamed Function");
        String nameForModel = Optional.ofNullable(function.getName()).orElse("Unnamed Function");
        String argsFormat = "";

        ObjectMapper mapper = new ObjectMapper();
        String parametersJson = "";
        try {
            parametersJson = mapper.writeValueAsString(function.getParameters().orElse(null));
        } catch (JsonProcessingException e) {
            log.error("Error serializing function parameters", e);
        }

        return toolDesc.replace("{nameForHuman}", nameForHuman).replace("{nameForModel}", nameForModel)
                .replace("{descriptionForModel}", function.getDescription()).replace("{parameters}", parametersJson)
                .replace("{argsFormat}", argsFormat).trim();
    }

    /**
     * 按照模板拼装函数调用字符串，并插入到用户传入的messages中
     *
     * @param functionCallModel 函数调用工具类
     * @param messages 用户调用大模型的messages
     * @param tools 用户传入的工具列表，包含一个函数列表
     * @return 拼装函数调用后的新messages
     */
    public static List<ChatMessage> prependToolsSystem(FunctionCallModel functionCallModel, List<ChatMessage> messages,
                                                       List<ToolCall> tools) {
        String toolDescTemplate = functionCallModel.getFnCallTemplateEn();

        for (ChatMessage message : messages) {
            if ("user".equals(message.getRole())) {
                if (message.getContent().isPresent() && hasChineseChars(message.getContent().get())) {
                    toolDescTemplate = functionCallModel.getFnCallTemplateZh();
                }
                break;
            }
        }

        StringBuilder toolDescs = new StringBuilder();
        for (ToolCall tool : tools) {
            toolDescs.append(getFunctionDescription(tool.getFunction())).append(System.lineSeparator())
                    .append(System.lineSeparator());
        }

        StringBuilder toolNames = new StringBuilder();
        for (ToolCall tool : tools) {
            toolNames.append(tool.getFunction().getName()).append(",");
        }
        if (toolNames.length() > 0) {
            toolNames.setLength(toolNames.length() - 1); // Remove last comma
        }

        String toolSystem = toolDescTemplate.replace("{tool_descs}", toolDescs.toString()).replace("{tool_names}",
                toolNames.toString());

        if (!"system".equals(messages.get(0).getRole())) {
            messages.add(0, new ChatMessage("system", Optional.of("You are a helpful assistant"), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty()));
        }

        String content = messages.get(0).getContent().orElse("") + toolSystem;
        messages.get(0).setContent(Optional.of(content));

        return messages;
    }

    /**
     * 使用特定函数调用工具类，组装传入的函数与message
     *
     * @param functionCallModel 特定函数调用工具类
     * @param messages 用户传入的message
     * @return 使用特定函数调用工具类组装后的message
     */
    public static List<ChatMessage> preprocessFncallMessages(FunctionCallModel functionCallModel,
                                                             List<ChatMessage> messages) {
        List<ChatMessage> newMessages = new ArrayList<>();
        String messageRole = "assistant";

        for (ChatMessage msg : new ArrayList<>(messages)) {
            String role = msg.getRole();
            Optional<String> content = msg.getContent();

            if ("system".equals(role) || "user".equals(role)) {
                newMessages.add(msg);
            } else if (messageRole.equals(role)) {
                String updatedContent = content.orElse("");
                Optional<FunctionCall> fnCall = Optional.empty();
                if (msg.getToolCalls() != null && msg.getToolCalls().isPresent()
                        && !msg.getToolCalls().get().isEmpty()) {
                    fnCall = Optional.of(msg.getToolCalls().get().get(0).getFunction());
                }
                if (fnCall.isPresent()) {
                    String funcContent = "";
                    String fName = fnCall.get().getName();
                    String fArgs = fnCall.get().getArguments();
                    funcContent += System.lineSeparator() + functionCallModel.getFnName() + ": " + fName;
                    funcContent += System.lineSeparator() + functionCallModel.getFnArgs() + ": " + fArgs;
                    updatedContent += funcContent;
                }
                if (!newMessages.isEmpty() && newMessages.get(newMessages.size() - 1).getRole().equals(messageRole)) {
                    String lastContent = newMessages.get(newMessages.size() - 1).getContent().orElse("");
                    newMessages.get(newMessages.size() - 1).setContent(Optional.of(lastContent + updatedContent));
                } else {
                    newMessages.add(new ChatMessage(role, Optional.of(updatedContent), Optional.empty(),
                            Optional.empty(), Optional.empty(), Optional.empty()));
                }
            } else if ("tool".equals(role)) {
                if (content.isPresent()) {
                    String fResult = content.orElse("");
                    String lastContent = newMessages.get(newMessages.size() - 1).getContent().orElse("");
                    newMessages.get(newMessages.size() - 1)
                            .setContent(Optional.of(lastContent + System.lineSeparator()
                                    + functionCallModel.getFnResult() + ": "
                                    + fResult + System.lineSeparator() + functionCallModel.getFnExit() + ": "));
                }
            } else {
                throw new IllegalArgumentException("Unknown role: " + role);
            }
        }

        finalizeMessages(functionCallModel, newMessages, messageRole);

        return newMessages;
    }

    /**
     * 在处理完所有消息后，对结果进行必要的清理和合并操作
     *
     * @param functionCallModel 特定函数调用工具类
     * @param newMessages 处理后的消息列表
     * @param messageRole 当前处理的角色
     */
    private static void finalizeMessages(FunctionCallModel functionCallModel,
                                         List<ChatMessage> newMessages, String messageRole) {
        if (!newMessages.isEmpty() && newMessages.get(newMessages.size() - 1).getRole().equals(messageRole)) {
            String lastMsg = newMessages.get(newMessages.size() - 1).getContent().orElse("");
            if (lastMsg.endsWith(functionCallModel.getFnExit() + ": ")) {
                newMessages.get(newMessages.size() - 1)
                        .setContent(Optional.of(lastMsg.substring(0, lastMsg.length() - 2)));
            }
        }

        if (!newMessages.isEmpty() && newMessages.get(newMessages.size() - 1).getRole().equals(messageRole)) {
            String usr = newMessages.get(newMessages.size() - 2).getContent().orElse("");
            String bot = newMessages.get(newMessages.size() - 1).getContent().orElse("");
            newMessages.get(newMessages.size() - 2)
                    .setContent(Optional.of(usr + System.lineSeparator() + System.lineSeparator() + bot));
            newMessages.remove(newMessages.size() - 1);
        }
    }

    /**
     * 使用特定函数调用工具类，提取函数调用部分，组装为新message
     *
     * @param functionCallModel 特定函数调用工具类
     * @param msg 模型返回的messages
     * @return 使用特定函数调用工具类组装后的message
     */
    public static ChatMessage postprocessFncallMessages(FunctionCallModel functionCallModel, ChatMessage msg) {
        if ("system".equals(msg.getRole()) || "user".equals(msg.getRole())) {
            return msg;
        }

        String role = msg.getRole();
        String itemText = msg.getContent().orElse("");

        int i = itemText.indexOf(functionCallModel.getFnName() + ":");
        if (i < 0) {
            msg.setContent(functionCallModel.removeSpecialTokens(itemText));
            return msg;
        }

        if (i > 0) {
            itemText = itemText.substring(i);
        }

        String[] parts = itemText.split(functionCallModel.getFnName() + ":");
        List<ToolCallResponse> toolCallResponseList = new ArrayList<>();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }

            part = part.trim();
            i = part.indexOf(functionCallModel.getFnArgs() + ":");
            int j = part.indexOf(functionCallModel.getFnResult() + ":");
            String fnName;
            String fnArgs;
            if (i < 0) {
                fnName = part.trim();
            } else {
                fnName = part.substring(0, i).trim();
            }

            if (j < 0) {
                fnArgs = part.substring(i + (functionCallModel.getFnArgs() + ":").length()).trim();
            } else {
                fnArgs = part.substring(i + (functionCallModel.getFnArgs() + ":").length(), j).trim();
            }

            ToolCallResponse toolCallResponse = new ToolCallResponse(
                    UUID.randomUUID().toString(), "function",
                    new FunctionCall(functionCallModel.removeSpecialTokens(fnName).orElse(""),
                            functionCallModel.removeSpecialTokens(fnArgs).orElse("")));
            toolCallResponseList.add(toolCallResponse);
        }
        return new ChatMessage("assistant", Optional.of(""), Optional.of(""), Optional.empty(), Optional.of(""),
                Optional.of(toolCallResponseList));
    }
}
