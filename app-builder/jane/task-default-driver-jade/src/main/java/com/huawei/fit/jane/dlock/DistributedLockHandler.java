/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.dlock;

import java.util.concurrent.locks.Lock;

/**
 * 表示分布式锁的处理接口
 *
 * @author 李哲峰
 * @since 2023-11-30
 */
public interface DistributedLockHandler {
    /**
     * 通过锁key获取分布式锁
     *
     * @param key 分布式锁的key值
     * @return {@link Lock} 分布式锁锁对象
     */
    Lock getLock(String key);
}
