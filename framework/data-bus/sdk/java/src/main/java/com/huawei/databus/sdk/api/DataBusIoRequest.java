/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.api;

import java.time.Duration;
import java.util.Optional;

/**
 * 为 DataBus 读写请求共有接口。
 *
 * @author 王成 w00863339
 * @since 2024-03-17
 */
public interface DataBusIoRequest {
    /**
     * 返回与本次 IO 相关的内存用户 key
     *
     * @return 表示与本次 IO 请求相关的内存 {@code String}
     */
    String userKey();

    /**
     * 返回与本次 IO 相关的字节数组
     *
     * @return 表示与本次 IO 请求相关的 {@code byte[]}
     */
    byte[] bytes();

    /**
     * 返回本次 IO 请求的起始内存地址
     *
     * @return 表示起始内存地址的 {@code long}
     */
    long memoryOffset();

    /**
     * 返回本次 IO 请求相关的最大字节数。如果客户端未设置此值或者设置为0时，则默认为应尽可能读写最多字节数
     * 1. 对于读请求，默认读取整块内存的所有内存
     * 2. 对于写请求，默认写入整个字节数组的所有内容
     *
     * @return 表示最大字节数的 {@code int}
     */
    int dataLength();

    /**
     * 返回本次 IO 请求相关的许可类型
     *
     * @return 表示许可类型的 {@code byte}
     */
    byte permissionType();

    /**
     * 返回本次 IO 请求相关的超时时间。如果没有设置超时时限，则返回 {@code Optional.empty()}
     *
     * @return 表示超时时限的 {@code Optional<Duration>}
     */
    Optional<Duration> timeoutDuration();

    /**
     * IORequest 的构造器
     */
    interface Builder {
        /**
         * 向当前构建器中设置许可类型
         *
         * @param permissionType 表示被设置的字节数组 {@code byte}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder permissionType(byte permissionType);

        /**
         * 向当前构建器中设置字节数组。
         *
         * @param bytes 表示被设置的字节数组 {@link byte[]}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder bytes(byte[] bytes);

        /**
         * 向当前构建器中设置最大字节数。
         *
         * @param dataLength 表示被设置的最大字节数 {@code int}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder dataLength(int dataLength);

        /**
         * 向当前构建器中设置起始内存地址
         *
         * @param memoryOffset 表示被设置的起始内存地址 {@code long}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder memoryOffset(long memoryOffset);

        /**
         * 向当前构建器中设置用户自定义 key。
         *
         * @param userKey 表示被设置的内存句柄 {@code String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder userKey(String userKey);

        /**
         * 向当前构建器中设置超时时限
         *
         * @param duration 表示被设置的超时时限 {@link Duration}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder timeoutDuration(Duration duration);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link DataBusIoRequest}。
         */
        DataBusIoRequest build();
    }
}
