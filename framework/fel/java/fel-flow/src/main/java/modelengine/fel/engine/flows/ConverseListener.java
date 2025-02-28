/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.engine.flows;

import modelengine.fit.waterflow.domain.context.FlowContext;
import modelengine.fit.waterflow.domain.stream.nodes.Retryable;

import java.util.List;

/**
 * 流程对话监听器。
 *
 * @author 刘信宏
 * @since 2024-04-26
 */
public interface ConverseListener<T> {
    /**
     * 对话成功回调。
     *
     * @param flowId 表示流程id的 {@link String}。
     * @param data 表示流程输出的数据的 {@link T}。
     */
    void onSuccess(String flowId, T data);

    /**
     * 流程异常回调。
     *
     * @param exception 表示本次对话异常的 {@link Exception}。
     * @param retryable 表示本次对话重试句柄的 {@link Retryable}{@code <}{@link Object}{@code >}。
     * @param contexts 表示本次对话上下文的 {@link List }{@code <}{@link FlowContext}{@code <}{@link Object}{@code >>}。
     */
    void onFlowError(Exception exception, Retryable<Object> retryable, List<FlowContext<Object>> contexts);

    /**
     * 对话异常回调。
     *
     * @param exception 表示本次对话异常的 {@link Exception}。
     */
    void onConverseError(Exception exception);

    /**
     * 判断对话是否结束。
     *
     * @return 如果当前对话已经结束，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isCompleted();
}
