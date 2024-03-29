/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.event;

import com.huawei.fitframework.broker.LocalExecutor;
import com.huawei.fitframework.broker.UniqueFitableId;

/**
 * 表示本地执行器注册完毕的观察者。
 *
 * @author 季聿阶 j00559309
 * @since 2023-03-25
 */
@FunctionalInterface
public interface LocalExecutorRegisteredObserver {
    /**
     * 当本地执行器注册完毕时触发。
     *
     * @param id 表示注册的本地执行器对应的服务实现的唯一标识的 {@link UniqueFitableId}。
     * @param executor 表示被注册的本地执行器的 {@link LocalExecutor}。
     */
    void onLocalExecutorRegistered(UniqueFitableId id, LocalExecutor executor);
}
