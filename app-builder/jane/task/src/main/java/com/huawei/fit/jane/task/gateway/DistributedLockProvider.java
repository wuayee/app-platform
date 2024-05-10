/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import java.util.concurrent.locks.Lock;

/**
 * 为应用程序提供分布式锁。
 *
 * @author 梁济时 l00815032
 * @since 2023-11-16
 */
public interface DistributedLockProvider {
    /**
     * 获取指定键的分布式锁。
     *
     * @param key 表示分布式锁对应的键的 {@link String}。
     * @return 表示分布式锁的 {@link Lock}。
     */
    Lock get(String key);
}
