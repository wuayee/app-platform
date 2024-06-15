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
    String HISTORY_OBJ = "history_obj";

    /**
     * 表示用户原始问题的键。
     */
    String HISTORY_INPUT = "history_input";

    /**
     * 表示流程实例运行标识的键。
     */
    String FLOW_SESSION = "flow_session";

    /**
     * 表示委托单元动态参数的键。
     */
    String PATTERN_ARGS = "pattern_args";

    /**
     * 表示模型超参数的键。
     */
    String CHAT_OPTIONS = "chat_options";

    /**
     * 表示流程对话监听器的键。
     */
    String CONVERSE_LISTENER = "converse_listener";

    /**
     * 表示流式响应信息消费者的键。
     */
    String STREAMING_CONSUMER = "streaming_consumer";

    /**
     * 表示流式模型节点处理器。
     */
    String STREAMING_PROCESSOR = "streaming_processor";

    /**
     * 表示流式模型节点处理器。
     */
    String STREAMING_FLOW_CONTEXT = "streaming_flow_context";
}
