/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2023. All rights reserved.
 */

package com.huawei.fit.service;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.fitframework.broker.LocalExecutor;

import java.util.List;

/**
 * 表示注册服务实现的服务。
 *
 * @author 李鑫
 * @since 2021-11-29
 */
@Genericable
public interface RegisterFitableService {
    /**
     * 将指定服务实现在注册中心进行注册。
     *
     * @param fitableMetadata 表示待注册的服务列表的 {@link List}{@code <}{@link LocalExecutor}{@code >}。
     */
    void register(List<LocalExecutor> fitableMetadata);

    /**
     * 将指定服务实现在注册中心进行注销。
     *
     * @param fitableMetadata 表示待注销的服务列表的 {@link List}{@code <}{@link LocalExecutor}{@code >}。
     */
    void unregister(List<LocalExecutor> fitableMetadata);
}
