/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.util;

/**
 * 会话实例信息中内置状态的键。
 *
 * @author 刘信宏
 * @since 2024-04-22
 */
public interface StateKey {
    /**
     * 表示历史记录对象句柄的键。
     */
    String HISTORY = "history";

    /**
     * 表示模型超参数的键。
     */
    String CHAT_OPTION = "chat_option";

    /**
     * 表示流程对话监听器的键。
     */
    String CONVERSE_LISTENER = "converse_listener";
}
