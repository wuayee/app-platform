/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.support;

import com.huawei.databus.sdk.api.DataBusIoResult;
import com.huawei.databus.sdk.memory.SharedMemory;
import com.huawei.databus.sdk.memory.SharedMemoryKey;
import com.huawei.databus.sdk.message.ErrorType;
import com.huawei.databus.sdk.message.PermissionType;

import java.util.Optional;

/**
 * 为内存申请提供结果。
 *
 * @author 王成 w00863339
 * @since 2024-03-17
 */
public interface MemoryIoResult extends DataBusIoResult {
    /**
     * 返回与本次 IO 相关的字节数组
     *
     * @return 表示与本次 IO 请求相关的 {@code byte[]}
     */
    byte[] bytes();

    /**
     * 返回本次 IO 请求相关的许可类型
     *
     * @return 表示许可类型的 {@code byte}
     */
    byte permissionType();

    /**
     * 生成一个表示成功的结果。
     *
     * @param sharedMemory 表示此 IO 操作涉及到的内存实例的 {@link SharedMemoryKey}。
     * @param bytes 表示此 IO 操作涉及到字节缓冲区的 {@code byte[]}。
     * @param permissionType 表示此 IO 操作涉及到权限的 {@code byte}。
     * @return 表示内存 IO 操作成功的结果的 {@link MemoryIoResult}。
     */
    static MemoryIoResult success(SharedMemory sharedMemory, byte[] bytes, byte permissionType) {
        return new SuccessResult(sharedMemory, bytes, permissionType);
    }

    /**
     * 获取表示内存 IO 失败的结果。
     *
     * @param errorType 表示申请内存得到的错误码 {@code byte}。
     * @param throwable 表示 Java 原生异常的 {@link Throwable}
     * @return 表示内存 IO 失败的结果的 {@link MemoryIoResult}。
     */
    static MemoryIoResult failure(byte errorType, Throwable throwable) {
        return new FailureResult(errorType, throwable);
    }

    /**
     * 获取表示内存 IO 失败的结果。
     *
     * @param errorType 表示申请内存得到的错误码 {@code byte}。
     * @return 表示内存 IO 失败的结果的 {@link MemoryIoResult}。
     */
    static MemoryIoResult failure(byte errorType) {
        return new FailureResult(errorType, null);
    }

    /**
     * 为 {@link MemoryIoResult} 提供表示申请内存成功的实现。
     *
     * @author 王成 w00863339
     * @since 2024-03-17
     */
    final class SuccessResult implements MemoryIoResult {
        private final SharedMemory sharedMemory;
        private final byte[] bytes;
        private final byte permissionType;

        private SuccessResult(SharedMemory sharedMemory, byte[] bytes, byte permissionType) {
            this.sharedMemory = sharedMemory;
            this.bytes = bytes;
            this.permissionType = permissionType;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public byte errorType() {
            return ErrorType.None;
        }

        @Override
        public Optional<Throwable> cause() {
            return Optional.empty();
        }

        @Override
        public SharedMemory sharedMemory() {
            return this.sharedMemory;
        }

        @Override
        public byte[] bytes() {
            return this.bytes;
        }

        @Override
        public byte permissionType() {
            return this.permissionType;
        }

        @Override
        public String toString() {
            return "SuccessResult{sharedMemory=" + sharedMemory + ", permissionType=" + permissionType + '}';
        }
    }

    /**
     * 为 {@link MemoryIoResult} 提供表示内存 IO 失败的实现。
     *
     * @author 王成 w00863339
     * @since 2024-03-17
     */
    final class FailureResult implements MemoryIoResult {
        private final byte errorType;
        private final Throwable throwable;

        private FailureResult(byte errorType, Throwable throwable) {
            this.errorType = errorType;
            this.throwable = throwable;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public byte errorType() {
            return errorType;
        }

        @Override
        public Optional<Throwable> cause() {
            return Optional.ofNullable(throwable);
        }

        @Override
        public SharedMemory sharedMemory() {
            return null;
        }

        @Override
        public byte[] bytes() {
            return new byte[0];
        }

        @Override
        public byte permissionType() {
            return PermissionType.None;
        }

        @Override
        public String toString() {
            return "FailureResult{errorType=" + errorType + ", throwable=" + throwable + '}';
        }
    }
}
