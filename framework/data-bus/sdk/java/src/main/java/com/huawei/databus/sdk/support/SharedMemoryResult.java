/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.support;

import com.huawei.databus.sdk.api.DataBusIoResult;
import com.huawei.databus.sdk.memory.SharedMemory;
import com.huawei.databus.sdk.message.ErrorType;
import com.huawei.fitframework.inspection.Validation;

import java.util.Optional;

/**
 * 为内存申请提供结果。
 *
 * @author 王成
 * @since 2024-03-17
 */
public interface SharedMemoryResult extends DataBusIoResult {
    /**
     * 生成一个表示成功的结果。
     *
     * @param sharedMemory 表示申请内存得到的配置的实例的 {@link SharedMemory}。
     * @return 表示申请内存成功的结果的 {@link SharedMemoryResult}。
     */
    static SharedMemoryResult success(SharedMemory sharedMemory) {
        return new SuccessResult(sharedMemory);
    }

    /**
     * 获取表示申请内存失败的结果。
     *
     * @param errorType 表示申请内存得到的错误码 {@code byte}。
     * @param throwable 表示 Java 原生异常的 {@link Throwable}
     * @return 表示内存 IO 失败的结果的 {@link SharedMemoryResult}。
     */
    static SharedMemoryResult failure(byte errorType, Throwable throwable) {
        return new SharedMemoryResult.FailureResult(errorType, throwable);
    }

    /**
     * 获取表示申请内存失败的结果。
     *
     * @param errorType 表示申请内存得到的错误码 {@code byte}。
     * @return 表示内存 IO 失败的结果的 {@link SharedMemoryResult}。
     */
    static SharedMemoryResult failure(byte errorType) {
        return new SharedMemoryResult.FailureResult(errorType, null);
    }

    /**
     * 为 {@link SharedMemoryResult} 提供表示申请内存成功的实现。
     *
     * @author 王成
     * @since 2024-03-17
     */
    final class SuccessResult implements SharedMemoryResult {
        private final SharedMemory sharedMemory;

        /**
         * 使用被成功申请内存的配置初始化 {@link SuccessResult} 类的新实例。
         *
         * @param key 表示被成功申请内存的句柄的 {@link SharedMemory}。
         * @throws IllegalArgumentException {@code key} 为 {@code null}。
         */
        private SuccessResult(SharedMemory key) {
            this.sharedMemory = Validation.notNull(key, "The shared memory cannot be null.");
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
    }

    /**
     * 为 {@link SharedMemoryResult} 提供表示申请内存失败的实现。
     *
     * @author 王成
     * @since 2024-03-17
     */
    final class FailureResult implements SharedMemoryResult {
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
    }
}
