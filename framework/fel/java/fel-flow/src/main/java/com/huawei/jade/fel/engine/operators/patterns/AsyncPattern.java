/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.patterns;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.emitters.Emitter;

/**
 * 异步委托单元。
 *
 * @param <I> 表示输入数据类型。
 * @param <O> 表示输出数据类型。
 * @author 刘信宏
 * @since 2024-04-22
 */
public interface AsyncPattern<I, O> extends Emitter<O, FlowSession> {
    /**
     * 异步执行接口。
     *
     * @param data 表示待注入数据数组的 {@link I}。
     * @param session 表示流程会话实例信息的 {@link FlowSession}。
     */
    void offer(I data, FlowSession session);
}
