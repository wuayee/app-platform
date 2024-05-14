/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.support;

import com.huawei.databus.sdk.api.DataBusIoRequest;
import com.huawei.fitframework.inspection.Validation;

import java.time.Duration;
import java.util.Optional;

/**
 * IORequest 的默认内存实现
 *
 * @author 王成 w00863339
 * @since 2024/3/22
 */
public class MemoryIoRequest implements DataBusIoRequest {
    private final String userKey;
    private final byte[] bytes;
    private final long memoryOffset;
    private final int dataLength;
    private final byte permissionType;
    private final Duration timeoutDuration;

    private MemoryIoRequest(Builder builder) {
        this.userKey = Validation.notBlank(builder.userKey, "User key could not be empty.");
        this.bytes = builder.bytes;
        this.memoryOffset = builder.memoryOffset;
        this.dataLength = builder.dataLength;
        this.permissionType = builder.permissionType;
        this.timeoutDuration = builder.timeoutDuration;
    }

    @Override
    public String userKey() {
        return this.userKey;
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

    @Override
    public Optional<Duration> timeoutDuration() {
        return Optional.ofNullable(this.timeoutDuration);
    }

    /**
     * {@link MemoryIoRequest} 的构造器
     */
    public static class Builder implements DataBusIoRequest.Builder {
        private byte[] bytes;
        private long memoryOffset;
        private int dataLength;
        private byte permissionType;
        private Duration timeoutDuration;
        private String userKey;

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
        public Builder userKey(String userKey) {
            this.userKey = userKey;
            return this;
        }

        @Override
        public Builder timeoutDuration(Duration timeoutDuration) {
            this.timeoutDuration = timeoutDuration;
            return this;
        }

        @Override
        public MemoryIoRequest build() {
            if (this.bytes == null) {
                this.bytes = new byte[0];
            }
            return new MemoryIoRequest(this);
        }
    }

    @Override
    public String toString() {
        return "MemoryIORequest{userKey=" + userKey + ", memoryOffset=" + memoryOffset + ", dataLength=" + dataLength
                + ", permissionType=" + permissionType + ", timeoutDuration=" + timeoutDuration + '}';
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
