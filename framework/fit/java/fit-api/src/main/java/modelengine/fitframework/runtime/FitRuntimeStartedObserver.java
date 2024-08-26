/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fitframework.runtime;

/**
 * 为 {@link FitRuntime} 启动完成提供观察者。
 *
 * @author 梁济时
 * @since 2022-08-15
 */
public interface FitRuntimeStartedObserver {
    /**
     * 当FIT运行环境启动完成时被通知。
     *
     * @param runtime 表示已启动完成的运行环境的 {@link FitRuntime}。
     */
    void onRuntimeStarted(FitRuntime runtime);
}
