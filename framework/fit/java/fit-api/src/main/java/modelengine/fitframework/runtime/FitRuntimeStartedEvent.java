/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.runtime;

import modelengine.fitframework.event.Event;

import java.time.Duration;

/**
 * 当 FIT 运行时环境已启动时引发的事件。
 *
 * @author 梁济时
 * @since 2022-11-30
 */
public interface FitRuntimeStartedEvent extends Event {
    /**
     * 获取已启动的运行时。
     *
     * @return 表示已启动的运行时的 {@link FitRuntime}。
     */
    FitRuntime runtime();

    /**
     * 获取启动运行时所花费的时间。
     *
     * @return 表示启动运行时所花费的时间的 {@link Duration}。
     */
    Duration duration();

    @Override
    default Object publisher() {
        return this.runtime();
    }
}
