/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.memory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 共享内存抽象，在SDK内部使用
 *
 * @author 王成 w00863339
 * @since 2024/3/25
 */
public class SharedMemoryInternal implements SharedMemory {
    private final SharedMemoryKey key;
    private byte permission;
    private long size;
    private final ReentrantLock lock;

    public SharedMemoryInternal(SharedMemoryKey memoryKey, byte permission, long size) {
        this.key = memoryKey;
        this.permission = permission;
        this.size = size;
        this.lock = new ReentrantLock();
    }

    @Override
    public SharedMemoryKey key() {
        return key;
    }

    @Override
    public byte permission() {
        return permission;
    }

    @Override
    public long size() {
        return size;
    }

    /**
     * 获取当前内存锁
     *
     * @return 表示内存锁的 {@link ReentrantLock}
     */
    public Lock lock() {
        return lock;
    }

    /**
     * 私有内部类，提供共享内存的不可变视图
     */
    private static class SharedMemoryView implements SharedMemory {
        private final SharedMemory internal;

        private SharedMemoryView(SharedMemory internal) {
            this.internal = internal;
        }

        @Override
        public SharedMemoryKey key() {
            return this.internal.key();
        }

        @Override
        public byte permission() {
            return this.internal.permission();
        }

        @Override
        public long size() {
            return this.internal.size();
        }

        @Override
        public Lock lock() {
            return this.internal.lock();
        }

        @Override
        public String toString() {
            return "SharedMemoryView{key=" + key() + ",permission=" + permission() + ",size=" + size() + '}';
        }
    }

    /**
     * 返回此共享内存的不可变视图
     *
     * @return 表示内存视图的 {@link SharedMemory}
     */
    public SharedMemory getView() {
        return new SharedMemoryView(this);
    }

    /**
     * 修改当前内存块的许可
     *
     * @param permission 表示许可的 {@code byte}
     * @return 方便链式调用的 {@link SharedMemoryInternal}
     */
    public SharedMemoryInternal setPermission(byte permission) {
        this.permission = permission;
        return this;
    }

    /**
     * 修改当前内存块的大小
     *
     * @param size 表示许可的 {@code byte}
     * @return 方便链式调用的 {@link SharedMemoryInternal}
     */
    public SharedMemoryInternal setSize(long size) {
        this.size = size;
        return this;
    }
}
