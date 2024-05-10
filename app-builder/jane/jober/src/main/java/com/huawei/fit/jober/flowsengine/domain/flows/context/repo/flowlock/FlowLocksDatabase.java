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
 * 流程锁，数据库版本的实现
 *
 * @author l00862071
 * @since 2023/11/30
 */
@Component
@Alias("flowLocksDatabase")
public class FlowLocksDatabase implements FlowLocks {
    private final DistributedLockProvider distributedLockProvider;

    public FlowLocksDatabase(
            @Fit(alias = "databaseDistributedLockProvider") DistributedLockProvider distributedLockProvider) {
        this.distributedLockProvider = distributedLockProvider;
    }

    /**
     * 获取分布式锁
     *
     * @param key 获取分布式锁的key值，一般是prefix-streamID-eventId
     * 比如key值为：flow-event-streamId-eventId; flow-node-streamId-eventId
     * @return {@link Lock} 锁对象
     */
    @Override
    public Lock getDistributedLock(String key) {
        return distributedLockProvider.get(key);
    }
}
