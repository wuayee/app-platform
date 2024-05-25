/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.activities.processors;

import com.huawei.fit.waterflow.domain.context.StateContext;
import com.huawei.jade.fel.tool.ToolContext;

/**
 * 工具执行器，将数据进行加工和转换后继续流转，同时可以转换类型。执行器内可消费自定义流程上下文和工具上下文。
 *
 * @author 刘信宏
 * @since 2024-05-24
 */
@FunctionalInterface
public interface AiToolProcessMap<T, R> {
    /**
     * 数据转换接口。
     *
     * @param input 表示输入数据的 {@link T}。
     * @param context 表示流程自定义上下文的 {@link StateContext}。
     * @param toolContext 表示自定义工具上下文的 {@link ToolContext}，
     * 通过 {@link com.huawei.jade.fel.engine.flows.Conversation#bind(ToolContext)} 绑定到流程。
     * @return 表示转换后数据的 {@link R}。
     */
    R process(T input, StateContext context, ToolContext toolContext);
}
