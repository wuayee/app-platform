/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.chat;

import modelengine.fel.chat.protocol.ChatCompletion;
import modelengine.fel.chat.protocol.FlatChatMessage;
import modelengine.fitframework.annotation.Genericable;

/**
 * 表示聊天模型推理服务。
 *
 * @author 易文渊
 * @since 2024-04-13
 */
public interface ChatModelService {
    /**
     * 调用聊天模型一次性生成结果。
     *
     * @param chatCompletion 表示聊天请求的 {@link ChatCompletion}。
     * @return 表示聊天模型生成结果的 {@link FlatChatMessage}。
     */
    @Genericable(id = "com.huawei.jade.fel.chat.generate")
    FlatChatMessage generate(ChatCompletion chatCompletion);
}