/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import com.huawei.fit.jane.dlock.jdbc.DistributedLockClient;
import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.Component;

import lombok.AllArgsConstructor;

import java.util.concurrent.locks.Lock;

/**
 * 为 {@link DistributedLockProvider} 提供基于SQL数据库的实现。
 *
 * @author 李哲峰
 * @since 2023-12-07
 */
@Component
@AllArgsConstructor
@Alias("databaseDistributedLockProvider")
public class DatabaseDistributedLockProvider implements DistributedLockProvider {
    /**
     * 分布式锁客户端
     */
    private final DistributedLockClient distributedLockClient;

    @Override
    public Lock get(String key) {
        return this.distributedLockClient.getLock(key);
    }
}
