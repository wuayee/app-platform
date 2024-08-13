/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.service;

import com.huawei.fitframework.broker.Target;

import java.util.List;

/**
 * 表示注册中心的定位器。
 *
 * @author 季聿阶
 * @since 2022-09-12
 */
public interface RegistryLocator {
    /**
     * 获取注册中心的地址列表。
     *
     * @return 表示注册中心的地址列表的 {@link List}{@code <}{@link Target}{@code >}。
     */
    List<Target> targets();
}
