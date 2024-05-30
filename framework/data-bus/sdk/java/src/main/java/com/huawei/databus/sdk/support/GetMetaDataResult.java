/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.support;

import com.huawei.databus.sdk.api.DataBusResult;
import com.huawei.databus.sdk.message.ErrorType;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.Optional;

/**
 * 为 DataBus 服务获取用户元数据提供结果。
 *
 * @author 王成 w00863339
 * @since 2024-05-27
 */
public interface GetMetaDataResult extends DataBusResult {
    /**
     * 返回用户元数据。如果此内存块未设置用户元数据，或者 key 对应的内存不存在，则返回 0 长度字节数组。
     *
     * @return 表示与本次 IO 请求相关的用户元数据 {@code byte[]}。
     */
    byte[] userData();

    /**
     * 获取此共享内存的长度。
     *
     * @return 表示共享内存长度的 {code long}。
     */
    long size();

    /**
     * 生成一个表示元数据获取成功的结果。未设置元数据也属于获取成功。
     *
     * @param data 表示获取到的用户元数据 {@code byte[]}。
     * @param size 表示获取到的内存长度 {@code long}。
     * @return 表示元数据获取成功的结果的 {@link GetMetaDataResult}。
     */
    static GetMetaDataResult success(byte[] data, long size) {
        return new SuccessResult(data, size);
    }

    /**
     * 获取表示元数据获取失败的结果。
     *
     * @param errorType 表示元数据获取得到的错误码 {@code byte}。
     * @param throwable 表示 Java 原生异常的 {@link Throwable}。
     * @return 表示元数据获取失败的结果的 {@link GetMetaDataResult}。
     */
    static GetMetaDataResult failure(byte errorType, Throwable throwable) {
        return new FailureResult(errorType, throwable);
    }

    /**
     * 获取表示连接失败的结果，无 Java 原生异常抛出。
     *
     * @param errorType 表示连接得到的错误码 {@code byte}。
     * @return 表示连接失败的结果的 {@link GetMetaDataResult}。
     */
    static GetMetaDataResult failure(byte errorType) {
        return new FailureResult(errorType);
    }

    /**
     * 为 {@link GetMetaDataResult} 提供表示获取元数据成功的实现。
     *
     * @author 王成 w00863339
     * @since 2024-05-27
     */
    final class SuccessResult implements GetMetaDataResult {
        private final byte[] data;
        private final long size;

        private SuccessResult(byte[] data, long size) {
            this.data = ObjectUtils.getIfNull(data, () -> new byte[0]);
            this.size = size;
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
        public byte[] userData() {
            return this.data;
        }

        @Override
        public long size() {
            return this.size;
        }
    }

    /**
     * 为 {@link GetMetaDataResult} 提供表示获取元数据失败的实现。
     *
     * @author 王成 w00863339
     * @since 2024-03-17
     */
    final class FailureResult implements GetMetaDataResult {
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

        @Override
        public byte[] userData() {
            return new byte[0];
        }

        @Override
        public long size() {
            return 0;
        }
    }
}
