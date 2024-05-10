/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.dlock;

/**
 * 表示支持生命周期管理的分布式锁处理接口
 *
 * @author l00862071
 * @since 2023-11-30
 */
public interface ExpirableDistributedLockHandler extends DistributedLockHandler {
    /**
     * 删除未被使用的于给定timeout时间前过期的锁
     *
     * @param timeout 从上次使用锁到现在所经历的时间
     */
    void deleteExpiredLocks(long timeout);
}
