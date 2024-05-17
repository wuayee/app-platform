/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.support;

import com.huawei.databus.sdk.api.DataBusResult;
import com.huawei.databus.sdk.message.ErrorType;

import java.util.Optional;

/**
 * 为 DataBus 服务连接提供结果。
 *
 * @author 王成 w00863339
 * @since 2024-03-17
 */
public interface OpenConnectionResult extends DataBusResult {
    /**
     * 生成一个表示连接成功的结果。
     *
     * @return 表示连接成功的结果的 {@link OpenConnectionResult}。
     */
    static OpenConnectionResult success() {
        return new SuccessResult();
    }

    /**
     * 获取表示连接失败的结果。
     *
     * @param errorType 表示连接得到的错误码 {@code byte}。
     * @param throwable 表示 Java 原生异常的 {@link Throwable}
     * @return 表示连接失败的结果的 {@link OpenConnectionResult}。
     */
    static OpenConnectionResult failure(byte errorType, Throwable throwable) {
        return new FailureResult(errorType, throwable);
    }

    /**
     * 获取表示连接失败的结果，无异常抛出。
     *
     * @param errorType 表示连接得到的错误码 {@code byte}。
     * @return 表示连接失败的结果的 {@link OpenConnectionResult}。
     */
    static OpenConnectionResult failure(byte errorType) {
        return new FailureResult(errorType);
    }

    /**
     * 为 {@link OpenConnectionResult} 提供表示连接成功的实现。
     *
     * @author 王成 w00863339
     * @since 2024-03-17
     */
    final class SuccessResult implements OpenConnectionResult {
        private SuccessResult() {}

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
    }

    /**
     * 为 {@link OpenConnectionResult} 提供表示连接失败的实现。
     *
     * @author 王成 w00863339
     * @since 2024-03-17
     */
    final class FailureResult implements OpenConnectionResult {
        private final byte errorType;
        private final Throwable throwable;

        private FailureResult(byte errorType, Throwable throwable) {
            this.errorType = errorType;
            this.throwable = throwable;
        }

        private FailureResult(byte errorType) {
            this(errorType, null);
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
        public String toString() {
            return "FailureResult{errorType=" + errorType + ", throwable=" + throwable + '}';
        }
    }
}
