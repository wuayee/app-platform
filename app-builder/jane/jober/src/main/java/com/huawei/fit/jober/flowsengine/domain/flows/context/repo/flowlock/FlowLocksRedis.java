/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowlock;

import com.huawei.fit.jane.task.gateway.DistributedLockProvider;
import com.huawei.fitframework.annotation.Alias;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;

import java.util.concurrent.locks.Lock;

/**
 * 流程锁，redis版本的实现
 *
 * @author g00564732
 * @since 2023/11/07
 */
// @Component
@Alias("flowLocksRedis")
public class FlowLocksRedis implements FlowLocks {
    private final DistributedLockProvider distributedLockProvider;

    public FlowLocksRedis(
            @Fit(alias = "redisDistributedLockProvider") DistributedLockProvider distributedLockProvider) {
        this.distributedLockProvider = distributedLockProvider;
    }

    /**
     * 获取分布式锁
     *
     * @param key 获取分布式锁的key值，一般是prefix-streamID-nodeID-suffixes
     * 比如key值为：flow-event-streamId-eventId-192.168.0.1; flow-node-streamId-eventId-192.168.0.1
     * @return {@link Lock} 锁对象
     */
    @Override
    public Lock getDistributedLock(String key) {
        return distributedLockProvider.get(key);
    }
}
