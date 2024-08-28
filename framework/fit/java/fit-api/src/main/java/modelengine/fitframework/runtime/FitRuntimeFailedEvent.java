/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.runtime;

import modelengine.fitframework.event.Event;

/**
 * 当 FIT 运行时环境启动失败时引发的事件。
 *
 * @author 梁济时
 * @since 2022-11-30
 */
public interface FitRuntimeFailedEvent extends Event {
    /**
     * 获取启动失败的运行时。
     *
     * @return 表示启动失败的运行时的 {@link FitRuntime}。
     */
    FitRuntime runtime();

    /**
     * 获取启动失败的原因。
     *
     * @return 表示启动失败的原因的 {@link Throwable}。
     */
    Throwable cause();

    @Override
    default Object publisher() {
        return this.runtime();
    }
}
