/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.support;

import com.huawei.databus.sdk.api.DataBusIoResult;
import com.huawei.databus.sdk.memory.SharedMemory;
import com.huawei.databus.sdk.message.ErrorType;
import modelengine.fitframework.inspection.Validation;

import java.util.Optional;

/**
 * 为内存权限申请提供结果。
 *
 * @author 王成
 * @since 2024-05-21
 */
public interface MemoryPermissionResult extends DataBusIoResult {
    /**
     * 如果本次为读取，且用户设置了同时操作返回用户数据，则返回用户元数据。如果此内存块未设置用户元数据，返回0长度字节数组。
     * 其他情况，返回 null。
     *
     * @return 表示与本次 IO 请求相关的用户元数据 {@code byte[]}
     */
    byte[] userData();

    /**
     * 生成一个权限申请成功的结果。
     *
     * @param sharedMemory 表示申请权限得到的配置的实例的 {@link SharedMemory}。
     * @param userData 表示此 IO 操作附带的用户元数据的 {@code byte[]}。
     * @return 表示申请权限成功的结果的 {@link MemoryPermissionResult}。
     */
    static MemoryPermissionResult success(SharedMemory sharedMemory, byte[] userData) {
        return new SuccessResult(sharedMemory, userData);
    }

    /**
     * 获取表示申请内存权限失败的结果。
     *
     * @param errorType 表示申请内存权限得到的错误码 {@code byte}。
     * @param throwable 表示 Java 原生异常的 {@link Throwable}
     * @return 表示内存 IO 失败的结果的 {@link MemoryPermissionResult}。
     */
    static MemoryPermissionResult failure(byte errorType, Throwable throwable) {
        return new MemoryPermissionResult.FailureResult(errorType, throwable);
    }

    /**
     * 获取表示申请内存权限失败的结果。
     *
     * @param errorType 表示申请内存得到的错误码 {@code byte}。
     * @return 表示内存 IO 失败的结果的 {@link MemoryPermissionResult}。
     */
    static MemoryPermissionResult failure(byte errorType) {
        return new MemoryPermissionResult.FailureResult(errorType, null);
    }

    /**
     * 为 {@link MemoryPermissionResult} 提供表示申请内存权限成功的实现。
     *
     * @author 王成
     * @since 2024-05-21
     */
    final class SuccessResult implements MemoryPermissionResult {
        private final SharedMemory sharedMemory;
        private final byte[] userData;

        /**
         * 使用被成功申请内存的配置初始化 {@link SuccessResult} 类的新实例。
         *
         * @param key 表示被成功申请内存的句柄的 {@link SharedMemory}。
         * @param userData 表示此 IO 操作附带的用户元数据的 {@code byte[]}。
         * @throws IllegalArgumentException {@code key} 为 {@code null}。
         */
        private SuccessResult(SharedMemory key, byte[] userData) {
            this.sharedMemory = Validation.notNull(key, "The shared memory cannot be null.");
            this.userData = userData;
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
        public String toString() {
            return this.sharedMemory.toString();
        }

        @Override
        public byte[] userData() {
            return this.userData;
        }
    }

    /**
     * 为 {@link MemoryPermissionResult} 提供表示申请内存权限失败的实现。
     *
     * @author 王成
     * @since 2024-03-17
     */
    final class FailureResult implements MemoryPermissionResult {
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
        public String toString() {
            return "FailureResult{errorType=" + errorType + ", throwable=" + throwable + '}';
        }

        @Override
        public byte[] userData() {
            return new byte[0];
        }
    }
}
