/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.runtime;

import com.huawei.fitframework.event.Event;

import java.time.Duration;

/**
 * 当 FIT 运行时环境已准备就绪时引发的事件。
 *
 * @author 梁济时
 * @since 2022-11-30
 */
public interface FitRuntimePreparedEvent extends Event {
    /**
     * 获取已准备就绪的运行时。
     *
     * @return 表示运行时的 {@link FitRuntime}。
     */
    FitRuntime runtime();

    /**
     * 获取运行时准备就绪所花费的时间。
     *
     * @return 表示所花费时间的 {@link Duration}。
     */
    Duration duration();

    @Override
    default Object publisher() {
        return this.runtime();
    }
}
