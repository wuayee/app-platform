/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

/**
 * 为应用程序提供基础信息。
 *
 * @author 梁济时 l00815032
 * @since 2023-11-17
 */
public interface AppInfo {
    /**
     * 获取应用程序的唯一标识。
     *
     * @return 表示应用程序的唯一标识的 {@link String}。
     */
    String id();

    /**
     * 获取应用程序的键。
     *
     * @return 表示应用程序的键的 {@link String}。
     */
    String key();

    /**
     * 获取应用程序的令牌。
     *
     * @return 表示应用程序的令牌的 {@link String}。
     */
    String token();
}
