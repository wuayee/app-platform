/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.support;

import com.huawei.databus.sdk.api.DataBusIoRequest;
import com.huawei.databus.sdk.memory.SharedMemoryKey;

/**
 * IORequest 的默认内存实现
 *
 * @author 王成 w00863339
 * @since 2024/3/22
 */
public class MemoryIoRequest implements DataBusIoRequest {
    private final SharedMemoryKey sharedMemoryKey;
    private final byte[] bytes;
    private final long memoryOffset;
    private final int dataLength;
    private final byte permissionType;

    private MemoryIoRequest(SharedMemoryKey sharedMemoryKey, byte[] bytes, long memoryOffset, int dataLength,
        byte permissionType) {
        this.sharedMemoryKey = sharedMemoryKey;
        this.bytes = bytes;
        this.memoryOffset = memoryOffset;
        this.dataLength = dataLength;
        this.permissionType = permissionType;
    }

    @Override
    public SharedMemoryKey sharedMemoryKey() {
        return this.sharedMemoryKey;
    }

    @Override
    public byte[] bytes() {
        return this.bytes;
    }

    @Override
    public long memoryOffset() {
        return this.memoryOffset;
    }

    @Override
    public int dataLength() {
        return this.dataLength;
    }

    @Override
    public byte permissionType() {
        return this.permissionType;
    }

    /**
     * {@link MemoryIoRequest} 的构造器
     */
    public static class Builder implements DataBusIoRequest.Builder {
        private SharedMemoryKey sharedMemoryKey;
        private byte[] bytes;
        private long memoryOffset;
        private int dataLength;
        private byte permissionType;

        @Override
        public Builder permissionType(byte permissionType) {
            this.permissionType = permissionType;
            return this;
        }

        @Override
        public Builder bytes(byte[] bytes) {
            this.bytes = bytes;
            return this;
        }

        @Override
        public Builder dataLength(int dataLength) {
            this.dataLength = dataLength;
            return this;
        }

        @Override
        public Builder memoryOffset(long memoryOffset) {
            this.memoryOffset = memoryOffset;
            return this;
        }

        @Override
        public Builder sharedMemoryKey(SharedMemoryKey sharedMemoryKey) {
            this.sharedMemoryKey = sharedMemoryKey;
            return this;
        }

        @Override
        public MemoryIoRequest build() {
            if (this.bytes == null) {
                this.bytes = new byte[0];
            }
            return new MemoryIoRequest(this.sharedMemoryKey, this.bytes,
                    this.memoryOffset, this.dataLength, this.permissionType);
        }
    }

    @Override
    public String toString() {
        return "MemoryIORequest{sharedMemoryKey=" + sharedMemoryKey.toString()
                + ", memoryOffset=" + memoryOffset + ", dataLength=" + dataLength
                + ", permissionType=" + permissionType + '}';
    }

    /**
     * 获取 {@link MemoryIoRequest} 的构建器。
     *
     * @return 表示 {@link MemoryIoRequest} 的构建器的 {@link Builder}。
     */
    public static Builder custom() {
        return new MemoryIoRequest.Builder();
    }
}
