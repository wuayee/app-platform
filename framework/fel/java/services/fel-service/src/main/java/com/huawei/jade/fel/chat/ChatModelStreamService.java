/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat;

import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.flowable.Choir;
import com.huawei.jade.fel.chat.protocol.ChatCompletion;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;

/**
 * 表示聊天模型流式推理服务。
 *
 * @author 刘信宏
 * @since 2024-05-15
 */
public interface ChatModelStreamService {
    /**
     * 调用聊天模型流式生成结果。
     *
     * @param request 表示聊天请求的 {@link ChatCompletion}。
     * @return 表示聊天模型生成序列的 {@link Choir}{@code <}{@link FlatChatMessage}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.fel.chat.streamGenerate")
    Choir<FlatChatMessage> generate(ChatCompletion request);
}
