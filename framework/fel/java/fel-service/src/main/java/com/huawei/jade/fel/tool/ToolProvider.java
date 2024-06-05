/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;

import java.util.List;
import java.util.Map;

/**
 * 表示工具提供者功能的接口。
 *
 * @author 易文渊
 * @since 2024-4-7
 */
public interface ToolProvider {
    /**
     * 调用指定工具。
     *
     * @param toolCall 表示拥有工具调用参数的 {@link ToolCall}。
     * @param toolContext 表示自定义工具上下文的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示工具调用结果的 {@link FlatChatMessage}。
     */
    @Genericable("com.huawei.jade.fel.spi.tool.call")
    FlatChatMessage call(ToolCall toolCall, Map<String, Object> toolContext);

    /**
     * 根据工具名列表获取工具对象列表。
     *
     * @param name 表示工具名的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示工具对象列表列表的 {@link List}{@code <}{@link Tool}{@code >}。
     */
    @Genericable("com.huawei.jade.fel.spi.tool.get")
    List<Tool> getTool(List<String> name);
}