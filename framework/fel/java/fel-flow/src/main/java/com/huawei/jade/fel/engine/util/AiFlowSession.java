/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.util;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.core.Pattern;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link FlowSession} 线程变量。
 *
 * @author 易文渊
 * @since 2024-06-18
 */
public class AiFlowSession {
    private static final ThreadLocal<FlowSession> SESSION_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 获取线程变量 {@link FlowSession}。
     *
     * @return {@link FlowSession} 对象。
     */
    public static Optional<FlowSession> get() {
        return Optional.ofNullable(AiFlowSession.SESSION_THREAD_LOCAL.get());
    }

    /**
     * 在 {@link AiFlowSession} 上下文执行委托单元。
     *
     * @param pattern 表示委托单元的 {@link Pattern}{@code <}{@link I}{@code , }{@link O}{@code >}。
     * @param input 表示输入数据的 {@link I}。
     * @param session 表示流程实例运行标识信息的 {@link FlowSession}。
     * @return 表示输出数据的 {@link O}。
     */
    public static <I, O> O applyPattern(Pattern<I, O> pattern, I input, FlowSession session) {
        List<O> output = AiFlowSession.applyBatchPattern(Collections.singletonList(pattern), input, session);
        Validation.notEmpty(output, "The output cannot be empty.");
        return output.get(0);
    }

    /**
     * 在 {@link AiFlowSession} 上下文批量执行委托单元。
     *
     * @param patterns 表示委托单元的 {@link List}{@code <}{@link Pattern}{@code <}{@link I}{@code , }{@link O}{@code >>}。
     * @param input 表示输入数据的 {@link I}。
     * @param session 表示流程实例运行标识信息的 {@link FlowSession}。
     * @return 表示输出数据的 {@link O}。
     */
    public static <I, O> List<O> applyBatchPattern(List<Pattern<I, O>> patterns, I input, FlowSession session) {
        AiFlowSession.SESSION_THREAD_LOCAL.set(session);
        List<O> output = patterns.stream().map(pattern -> pattern.invoke(input)).collect(Collectors.toList());
        AiFlowSession.SESSION_THREAD_LOCAL.remove();
        return output;
    }
}
