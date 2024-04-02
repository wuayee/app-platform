/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.memory;

/**
 * 共享内存抽象，在SDK内部使用
 *
 * @author 王成 w00863339
 * @since 2024/3/25
 */
public class SharedMemoryInternal implements SharedMemory {
    private final SharedMemoryKey key;
    private final byte permission;
    private final long size;

    public SharedMemoryInternal(SharedMemoryKey memoryKey, byte permission, long size) {
        this.key = memoryKey;
        this.permission = permission;
        this.size = size;
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
}
