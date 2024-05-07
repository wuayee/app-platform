/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.memory;

import com.huawei.fitframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * 存储已分配的共享内存的句柄。有以下三种获取方式：
 *   1. 客户端申请内存，但没有指定 ID
 *   2. 客户端申请内存，且指定 ID
 *   3. 客户端未申请内存，从外界获取制定 ID
 *
 * @author 王成 w00863339
 * @since 2024-03-17
 */
public class SharedMemoryKey {
    /*
     * memoryId 由DataBus主服务提供，为4字节整数。
     */
    private final int memoryId;

    /*
     * userKey 由客户端主动提供，可以为任何非空字符串，推荐使用UUID避免重复。 memoryId 和 userKey 都可以唯一确定一块内存，但以 memoryID
     * 为更优选择
     */
    private final String userKey;

    public SharedMemoryKey(String userKey) {
        this.memoryId = -1;
        this.userKey = userKey;
    }

    public SharedMemoryKey(int memoryId, Optional<String> userKey) {
        this.memoryId = memoryId;
        this.userKey = userKey.orElse(StringUtils.EMPTY);
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
