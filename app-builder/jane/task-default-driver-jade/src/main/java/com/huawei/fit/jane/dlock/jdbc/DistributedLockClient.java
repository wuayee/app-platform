/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.jane.dlock.jdbc;

import com.huawei.fit.jane.dlock.ExpirableDistributedLockHandler;
import com.huawei.fit.jane.task.gateway.InvalidDistributedLockNotify;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JDBC分布式锁客户端
 *
 * @author 李哲峰
 * @since 2023/11/30
 */
@Component
public final class DistributedLockClient implements ExpirableDistributedLockHandler {
    /**
     * 线程默认休眠时间
     */
    public static final int DEFAULT_IDLE_MS = 100;

    /**
     * 分布式锁池默认容量
     */
    public static final int DEFAULT_LOCKS_CAPACITY = 100_000;

    private final DistributedLockRepo repo;

    private final long ttl;

    private final long renewScheduleRate;

    private final Map<String, DistributedLock> locks = new LinkedHashMap<String, DistributedLock>(16, 0.75F, true) {
        private static final long serialVersionUID = 1825413387325768292L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, DistributedLock> eldest) {
            return size() > DistributedLockClient.this.cacheCapacity;
        }
    };

    private int idleTime = DEFAULT_IDLE_MS;

    private int cacheCapacity = DEFAULT_LOCKS_CAPACITY;

    private final InvalidDistributedLockNotify invalidDistributedLockNotify;

    public DistributedLockClient(@Value("${databasedistributedlock.ttl}") long ttl,
            @Value("${databasedistributedlock.renewScheduleRate}") long renewScheduleRate, DistributedLockRepo repo,
            InvalidDistributedLockNotify invalidDistributedLockNotify) {
        this.ttl = ttl;
        this.renewScheduleRate = renewScheduleRate;
        this.repo = repo;
        this.invalidDistributedLockNotify = invalidDistributedLockNotify;
    }

    /**
     * 设置休眠时间
     *
     * @param idleTime 休眠时间
     */
    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    /**
     * 设置锁池容量
     *
     * @param cacheCapacity 锁池容量
     */
    public void setCacheCapacity(int cacheCapacity) {
        this.cacheCapacity = cacheCapacity;
    }

    /**
     * 获取分布式锁，如果key值存在于锁池中，直接从锁池中获取，否则重新创建。
     *
     * @param key 分布式锁的key值
     * @return 分布式锁
     */
    @Override
    public DistributedLock getLock(String key) {
        String lockKey = DistributedLock.getLockKey(key);
        synchronized (this.locks) {
            return this.locks.computeIfAbsent(lockKey,
                    k -> new DistributedLock(this.ttl, this.renewScheduleRate, this.repo, this.idleTime, key,
                            invalidDistributedLockNotify));
        }
    }

    /**
     * 从锁池中删除未被使用的于给定timeout时间前过期的锁
     *
     * @param timeout 从上次使用锁到现在所经历的时间
     */
    @Override
    public void deleteExpiredLocks(long timeout) {
        long expiredTime = repo.now() - timeout;
        synchronized (this.locks) {
            for (Iterator<DistributedLock> iterator = this.locks.values().iterator(); iterator.hasNext(); ) {
                DistributedLock dLock = iterator.next();
                if (dLock.getLastUsed() < expiredTime && !dLock.isInProcess()) {
                    dLock.deleteExpired();
                    iterator.remove();
                }
            }
        }
    }
}
