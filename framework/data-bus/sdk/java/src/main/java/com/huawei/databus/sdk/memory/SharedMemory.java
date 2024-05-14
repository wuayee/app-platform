/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.memory;

import java.util.concurrent.locks.Lock;

/**
 * 共享内存抽象
 *
 * @author 王成 w00863339
 * @since 2024/3/25
 */
public interface SharedMemory {
    /**
     * 获取此内存块对应的句柄
     *
     * @return 表示对应的句柄 {@code String}
     */
    String userKey();

    /**
     * 获取共享内存的当前持有权限
     *
     * @return 表示权限的 {@code byte}
     */
    byte permission();

    /**
     * 获取此共享内存的长度
     *
     * @return 表示共享内存长度的 {code long}
     */
    long size();

    /**
     * 获取此共享内存的内存锁
     *
     * @return 表示内存锁的 {@link Lock}
     */
    Lock lock();
}
