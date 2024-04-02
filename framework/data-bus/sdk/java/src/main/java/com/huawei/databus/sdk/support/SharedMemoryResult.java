/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.support;

import com.huawei.databus.sdk.api.DataBusResult;
import com.huawei.databus.sdk.memory.SharedMemory;
import com.huawei.databus.sdk.message.ErrorType;
import com.huawei.fitframework.inspection.Validation;

/**
 * 为内存申请提供结果。
 *
 * @author 王成 w00863339
 * @since 2024-03-17
 */
public interface SharedMemoryResult extends DataBusResult {
    /**
     * 生成一个表示成功的结果。
     *
     * @param sharedMemory 表示申请内存得到的配置的实例的 {@link SharedMemory}。
     * @return 表示申请内存成功的结果的 {@link SharedMemoryResult}。
     * @throws IllegalArgumentException {@code config} 为 {@code null}。
     */
    static SharedMemoryResult success(SharedMemory sharedMemory) {
        return new SuccessResult(sharedMemory);
    }

    /**
     * 获取表示申请内存失败的结果。
     *
     * @param errorType 表示申请内存得到的错误码 {@code byte}。
     * @return 表示申请内存失败的结果的 {@link SharedMemoryResult}。
     */
    static SharedMemoryResult failure(byte errorType) {
        return new FailureResult(errorType);
    }

    /**
     * 为 {@link SharedMemoryResult} 提供表示申请内存成功的实现。
     *
     * @author 王成 w00863339
     * @since 2024-03-17
     */
    static final class SuccessResult implements SharedMemoryResult {
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
     * @author 王成 w00863339
     * @since 2024-03-17
     */
    static final class FailureResult implements SharedMemoryResult {
        private final byte errorType;

        private FailureResult(byte errorType) {
            this.errorType = errorType;
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
        public SharedMemory sharedMemory() {
            return null;
        }

        @Override
        public String toString() {
            return ErrorType.name(errorType);
        }
    }
}
