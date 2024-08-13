/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.memory;

import java.util.Objects;

/**
 * 存储已分配的共享内存的句柄。有以下两种获取方式：
 * <ol>
 *   <li>客户端申请内存并指定 ID</li>
 *   <li>客户端未申请内存，从外界获取制定 ID</li>
 * </ol>
 *
 * @author 王成
 * @since 2024-03-17
 */
public class SharedMemoryKey {
    /*
     * memoryId 由DataBus主服务提供，为4字节整数。
     */
    private int memoryId;

    /*
     * userKey 由客户端主动提供，可以为任何非空字符串，推荐使用UUID避免重复。 memoryId 和 userKey 都可以唯一确定一块内存，但以 memoryID
     * 为更优选择
     */
    private final String userKey;

    public SharedMemoryKey(String userKey) {
        this(-1, userKey);
    }

    public SharedMemoryKey(int memoryId, String userKey) {
        this.memoryId = memoryId;
        this.userKey = userKey;
    }

    /**
     * 获取内存的系统级 ID
     *
     * @return 表示内存系统级 ID 的 {@code int}
     */
    public int memoryId() {
        return memoryId;
    }

    /**
     * 设置内存的系统级 ID
     *
     * @param memoryId 表示系统级 ID 的 {@code int}
     */
    public void setMemoryId(int memoryId) {
        this.memoryId = memoryId;
    }

    /**
     * 获取内存的用户指定 ID
     *
     * @return 表示用户指定 ID 的 {@code String}
     */
    public String userKey() {
        return userKey;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + memoryId;
        result = prime * result + ((userKey == null) ? 0 : userKey.hashCode());
        return result;
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
        return memoryId == that.memoryId && Objects.equals(this.userKey, that.userKey);
    }

    @Override
    public String toString() {
        return "SharedMemoryKey{memoryId=" + memoryId + ", userKey='" + userKey + "'}";
    }
}
