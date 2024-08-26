/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.biz.service;

import com.huawei.fit.jane.task.gateway.InvalidDistributedLockNotify;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * 提供trace的归属服务
 *
 * @author 夏斐
 * @since 2024/2/29
 */
@Component
public class DefaultTraceOwnerService implements TraceOwnerService {
    private static final Logger log = Logger.get(DefaultTraceOwnerService.class);

    private final FlowLocks locks;

    private final Map<String, OwnInfo> traceMap = new ConcurrentHashMap<>();

    /**
     * 构造TraceOwnerService
     *
     * @param locks 锁
     * @param invalidDistributedLockNotify 锁的失效通知服务
     */
    public DefaultTraceOwnerService(FlowLocks locks, InvalidDistributedLockNotify invalidDistributedLockNotify) {
        this.locks = locks;
        invalidDistributedLockNotify.subscribe(this::removeInvalidTrace);
    }

    @Override
    public void own(String traceId, String transId) {
        Lock lock = this.locks.getDistributedLock(this.locks.traceLockKey(traceId));
        OwnLockHelper.lock(lock);
        this.traceMap.put(traceId, new OwnInfo(traceId, transId, lock));
    }

    @Override
    public boolean tryOwn(String traceId, String transId) {
        Lock lock = this.locks.getDistributedLock(this.locks.traceLockKey(traceId));
        boolean tryLock = OwnLockHelper.tryLock(lock);
        if (tryLock) {
            this.traceMap.put(traceId, new OwnInfo(traceId, transId, lock));
        }
        return tryLock;
    }

    @Override
    public void release(String traceId) {
        Lock lock = this.locks.getDistributedLock(this.locks.traceLockKey(traceId));
        OwnLockHelper.unlock(lock);
        this.traceMap.remove(traceId);
    }

    @Override
    public boolean isOwn(String traceId) {
        return this.traceMap.containsKey(traceId);
    }

    @Override
    public boolean isAnyOwn(Set<String> traceIds) {
        return traceIds.stream().anyMatch(this::isOwn);
    }

    @Override
    public List<String> getTraces() {
        return new ArrayList<>(this.traceMap.keySet());
    }

    @Override
    public List<String> getTraces(String targetTransId) {
        return Optional.ofNullable(targetTransId)
                .map(target -> this.traceMap.values().stream().map(OwnInfo::getTransId)
                        .filter(transId -> transId == null || target.equals(transId)).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public void removeInvalidTrace(Lock invalidLock) {
        log.warn("There is a lock is invalid.");
        Iterator<Map.Entry<String, OwnInfo>> iterator = this.traceMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, OwnInfo> entry = iterator.next();
            if (entry.getValue().getLock() == invalidLock) {
                log.warn("[TraceOwner] The trace is not belong to this node, traceId={}.", entry.getKey());
                OwnLockHelper.unlock(entry.getValue().getLock());
                iterator.remove();
                break;
            }
        }
    }

    @Data
    @AllArgsConstructor
    private static class OwnInfo {
        private String traceId;

        private String transId;

        private final Lock lock;
    }

    private static class OwnLockHelper {
        private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor(
                new CustomThreadFactory("own-lock"));

        /**
         * lock
         *
         * @param lock lock
         */
        public static void lock(Lock lock) {
            Future<?> submit = EXECUTOR_SERVICE.submit(lock::lock);
            try {
                submit.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new ServerInternalException(e.getMessage(), e);
            }
        }

        /**
         * unlock
         *
         * @param lock lock
         */
        public static void unlock(Lock lock) {
            Future<?> submit = EXECUTOR_SERVICE.submit(lock::unlock);
            try {
                submit.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new ServerInternalException(e.getMessage(), e);
            }
        }

        /**
         * tryLock
         *
         * @param lock lock
         * @return boolean
         */
        public static boolean tryLock(Lock lock) {
            Future<?> submit = EXECUTOR_SERVICE.submit(() -> lock.tryLock(100, TimeUnit.MILLISECONDS));
            boolean isGetLock;
            try {
                isGetLock = ObjectUtils.cast(submit.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new ServerInternalException(e.getMessage(), e);
            }
            return isGetLock;
        }
    }
}
