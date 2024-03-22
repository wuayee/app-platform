/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.support;

import com.huawei.databus.sdk.api.DataBusResult;
import com.huawei.databus.sdk.message.ApplyMemoryMessageResponse;
import com.huawei.databus.sdk.message.ErrorType;
import com.huawei.fitframework.inspection.Validation;

/**
 * 为内存申请提供结果。
 *
 * @author 王成 w00863339
 * @since 2024-03-17
 */
public abstract class SharedMemoryResult implements DataBusResult {
    /**
     * 获取共享内存的句柄句柄。
     * <p>仅当 {@link #isSuccess()} 为 {@code true} 时有效。</p>
     *
     * @return 表示共享内存句柄的 {@link SharedMemoryKey}。
     */
    public abstract SharedMemoryKey sharedMemoryKey();

    /**
     * 生成一个表示成功的结果。
     *
     * @param sharedMemoryKey 表示申请内存得到的配置的实例的 {@link SharedMemoryKey}。
     * @return 表示申请内存成功的结果的 {@link SharedMemoryResult}。
     * @throws IllegalArgumentException {@code config} 为 {@code null}。
     */
    static SharedMemoryResult success(SharedMemoryKey sharedMemoryKey) {
        return new SuccessResult(sharedMemoryKey);
    }

    /**
     * 获取表示申请内存失败的结果。
     *
     * @param errorType 表示申请内存得到的错误码 {@code byte}。
     * @return 表示申请内存失败的结果的 {@link SharedMemoryResult}。
     */
    public static SharedMemoryResult failure(byte errorType) {
        return new FailureResult(errorType);
    }

    /**
     * 获取申请内存结果。
     *
     * @param response 表示申请内存得到的服务器回复 {@link ApplyMemoryMessageResponse}。
     * @return 表示申请内存结果的 {@link SharedMemoryResult}。
     */
    public static SharedMemoryResult getResult(ApplyMemoryMessageResponse response) {
        if (response.errorType() == ErrorType.None) {
            return success(new SharedMemoryKey(response.memoryKey()));
        }
        return failure(response.errorType());
    }


    /**
     * 为 {@link SharedMemoryResult} 提供表示申请内存成功的实现。
     *
     * @author 王成 w00863339
     * @since 2024-03-17
     */
    private static final class SuccessResult extends SharedMemoryResult {
        private final SharedMemoryKey sharedMemoryKey;

        /**
         * 使用被成功申请内存的配置初始化 {@link SuccessResult} 类的新实例。
         *
         * @param key 表示被成功申请内存的句柄的 {@link SharedMemoryKey}。
         * @throws IllegalArgumentException {@code key} 为 {@code null}。
         */
        private SuccessResult(SharedMemoryKey key) {
            this.sharedMemoryKey = Validation.notNull(key, "The loaded config cannot be null.");
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
        public SharedMemoryKey sharedMemoryKey() {
            return this.sharedMemoryKey;
        }

        @Override
        public String toString() {
            return this.sharedMemoryKey.toString();
        }
    }

    /**
     * 为 {@link SharedMemoryResult} 提供表示申请内存失败的实现。
     *
     * @author 王成 w00863339
     * @since 2024-03-17
     */
    private static final class FailureResult extends SharedMemoryResult {
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
        public SharedMemoryKey sharedMemoryKey() {
            return null;
        }

        @Override
        public String toString() {
            return ErrorType.name(errorType);
        }
    }
}
