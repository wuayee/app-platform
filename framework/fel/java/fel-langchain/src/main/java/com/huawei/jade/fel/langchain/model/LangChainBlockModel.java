/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.langchain.model;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.core.model.BlockModel;
import com.huawei.jade.fel.langchain.runnable.LangChainRunnable;
import com.huawei.jade.fel.service.langchain.LangChainRunnableService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * LangChain 阻塞模型。
 *
 * @author 刘信宏
 * @since 2024-06-07
 */
public class LangChainBlockModel implements BlockModel<Prompt, ChatMessage> {
    private static final String LANG_CHAIN_CHAT_MODEL_TASK = "langchain.chat_model";

    private final LangChainRunnable runnable;

    /**
     * 使用数据处理器初始化 {@link LangChainBlockModel}。
     *
     * @param runnableService 表示 LangChain 算子代理服务的 {@link LangChainRunnableService}。
     * @param fitableId 表示实例名称的 {@link String}。
     * @throws IllegalArgumentException
     * <ul>
     *     <li>当 {@code runnableService} 为 {@code null} 时。</li>
     *     <li>当 {@code fitableId} 为 {@code null} 、空字符串或只有空白字符的字符串时。</li>
     * </ul>
     */
    public LangChainBlockModel(LangChainRunnableService runnableService, String fitableId) {
        this.runnable = new LangChainRunnable(runnableService, LANG_CHAIN_CHAT_MODEL_TASK, fitableId);
    }

    @Override
    public ChatMessage invoke(Prompt input) {
        Validation.notNull(input, "The input prompt cannot be null.");
        List<Map<String, Object>> lcMessages = input.messages().stream()
                .map(FlatChatMessage::new)
                .map(ObjectUtils::toJavaObject)
                .map(ObjectUtils::<Map<String, Object>>cast)
                .collect(Collectors.toList());

        Map<String, Object> result = ObjectUtils.cast(this.runnable.invoke(lcMessages));
        return ObjectUtils.toCustomObject(result, FlatChatMessage.class);
    }
}
