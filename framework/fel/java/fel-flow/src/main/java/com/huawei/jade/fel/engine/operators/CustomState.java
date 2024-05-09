/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators;

import com.huawei.fit.waterflow.domain.context.StateContext;
import com.huawei.jade.fel.core.memory.Memory;

/**
 * 表示暴露给用户的状态参数。
 *
 * @param <T> 表示业务数据的类型。
 * @author 刘信宏
 * @since 2024-4-28
 */
public interface CustomState<T> extends StateContext {
    /**
     * 获取业务数据。
     *
     * @return 表示业务数据的 {@link T}。
     */
    T data();

    /**
     * 获取历史记录。
     *
     * @return 表示历史记录的 {@link Memory}。
     */
    Memory memory();
}
