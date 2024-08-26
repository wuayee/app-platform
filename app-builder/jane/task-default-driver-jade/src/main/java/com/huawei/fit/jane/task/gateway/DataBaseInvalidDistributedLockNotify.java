/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import modelengine.fitframework.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

/**
 * 当分布式锁失效时，向外部通知
 *
 * @author 宋永坦
 * @since 2024/3/21
 */
@Component
public class DataBaseInvalidDistributedLockNotify implements InvalidDistributedLockNotify {
    private final List<Consumer<Lock>> consumers = new ArrayList<>();

    @Override
    public synchronized void subscribe(Consumer<Lock> consumer) {
        consumers.add(consumer);
    }

    @Override
    public synchronized void notify(Lock invalidLock) {
        consumers.forEach(consumer -> consumer.accept(invalidLock));
    }
}
