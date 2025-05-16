/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.databus.sdk.memory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 共享内存抽象，在SDK内部使用
 *
 * @author 王成
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

    /**
     * 获取当前内存的句柄
     *
     * @return 表示内存句柄的 {@link SharedMemoryKey}
     */
    public SharedMemoryKey sharedMemoryKey() {
        return key;
    }

    @Override
    public String userKey() {
        return this.key.userKey();
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
    @Override
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
        public String userKey() {
            return internal.userKey();
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
            return "SharedMemoryView{key=" + userKey() + ",permission=" + permission() + ",size=" + size() + '}';
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

    /**
     * 修改当前内存块的系统级句柄
     *
     * @param memoryId 表示许可的 {@code int}
     * @return 方便链式调用的 {@link SharedMemoryInternal}
     */
    public SharedMemoryInternal setMemoryId(int memoryId) {
        this.key.setMemoryId(memoryId);
        return this;
    }
}
