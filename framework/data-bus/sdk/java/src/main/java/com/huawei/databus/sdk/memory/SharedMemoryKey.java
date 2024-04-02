/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.memory;

/**
 * 存储已分配的共享内存的句柄
 *
 * @author 王成 w00863339
 * @since 2024-03-17
 */
public class SharedMemoryKey {
    private final int memoryId;

    public SharedMemoryKey(int memoryId) {
        this.memoryId = memoryId;
    }

    public int getMemoryId() {
        return memoryId;
    }

    @Override
    public int hashCode() {
        return memoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SharedMemoryKey)) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        SharedMemoryKey that = (SharedMemoryKey) o;
        return memoryId == that.memoryId;
    }

    @Override
    public String toString() {
        return "SharedMemoryKey{memoryId=" + memoryId + '}';
    }
}
