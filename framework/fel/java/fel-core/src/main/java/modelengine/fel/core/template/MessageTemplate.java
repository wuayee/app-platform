/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.core.template;

import modelengine.fel.chat.ChatMessage;

import java.util.Map;

/**
 * 消息模板接口定义。
 *
 * @author 易文渊
 * @since 2024-04-25
 */
public interface MessageTemplate extends GenericTemplate<Map<String, MessageContent>, ChatMessage> {}