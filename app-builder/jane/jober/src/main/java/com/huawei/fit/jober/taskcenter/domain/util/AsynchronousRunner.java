/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util;

import com.huawei.fit.jober.taskcenter.domain.util.support.DefaultAsynchronousRunner;

/**
 * 为任务实例的相关通知提供异步执行器。
 *
 * @author 梁济时 l00815032
 * @since 2023-09-18
 */
public interface AsynchronousRunner {
    /**
     * 提交待异步执行的逻辑。
     *
     * @param actions 表示待异步执行的逻辑的 {@link Runnable}{@code []}。
     */
    void run(Runnable... actions);

    /**
     * 返回异步执行器的唯一实例。
     *
     * @return 表示异步执行器的唯一实例的 {@link AsynchronousRunner}。
     */
    static AsynchronousRunner instance() {
        return DefaultAsynchronousRunner.INSTANCE;
    }
}

