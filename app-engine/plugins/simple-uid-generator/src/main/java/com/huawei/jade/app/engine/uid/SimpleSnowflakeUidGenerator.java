/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.uid;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 简单雪花算法 UID 生成实现。
 *
 * @author 何嘉斌
 * @since 2024-07-29
 */
@Component
public class SimpleSnowflakeUidGenerator implements UidGenerator {
    private static final long MAX_INT = 1L << 32;

    private static final int SEQUENCE_BIT = 32;

    private int workerId;

    private final AtomicLong id = new AtomicLong(0);

    private final WorkerGenerator workerGenerator;

    /**
     * 表示雪花算法 UID 生成器的构建器.
     *
     * @param workerGenerator 表示机器 ID 获取器持久层接口的 {@link WorkerGenerator}.
     */
    public SimpleSnowflakeUidGenerator(WorkerGenerator workerGenerator) {
        this.workerGenerator = workerGenerator;
        this.workerId = this.workerGenerator.getWorkerId();
    }

    @Override
    @Fitable("simple-snowflake")
    public long getUid() {
        long cid = this.id.getAndIncrement();
        if (cid >= MAX_INT) {
            cid = updateAndGetNext();
        }
        return (((long) this.workerId) << SEQUENCE_BIT) | cid;
    }

    private synchronized long updateAndGetNext() {
        long cid = this.id.getAndIncrement();
        if (cid < MAX_INT) {
            return cid;
        }
        this.workerId = this.workerGenerator.getWorkerId();
        this.id.set(0);
        return this.id.getAndIncrement();
    }
}