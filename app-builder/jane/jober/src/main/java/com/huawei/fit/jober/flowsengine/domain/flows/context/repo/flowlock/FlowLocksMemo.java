/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowlock;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.Lock;

/**
 * 流程锁，内存版本的实现
 *
 * @author g00564732
 * @since 2023/11/07
 */
@RequiredArgsConstructor
public class FlowLocksMemo implements FlowLocks {
    /**
     * 获取分布式锁
     * 获取分布式锁的key值，一般是prefix-streamID-nodeID-suffixes
     * 比如key值为：flow-event-streamId-eventId-192.168.0.1; flow-node-streamId-eventId-192.168.0.1
     *
     * @param key 版本ID
     * @return {@link Lock} 锁对象
     */
    @Override
    public Lock getDistributedLock(String key) {
        return getLocalLock(key);
    }
}
