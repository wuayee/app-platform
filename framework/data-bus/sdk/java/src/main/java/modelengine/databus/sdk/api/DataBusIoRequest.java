/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.databus.sdk.api;

import java.time.Duration;
import java.util.Optional;

/**
 * 为 DataBus 读写请求共有接口。
 *
 * @author 王成
 * @since 2024-03-17
 */
public interface DataBusIoRequest extends DataBusRequest {
    /**
     * 返回与本次 IO 相关的字节数组。
     *
     * @return 表示与本次 IO 请求相关的 {@code byte[]}。
     */
    byte[] bytes();

    /**
     * 返回本次 IO 请求的起始内存地址。
     *
     * @return 表示起始内存地址的 {@code long}。
     */
    long memoryOffset();

    /**
     * 返回本次 IO 请求相关的最大字节数。如果客户端未设置此值或者设置为 0 时，则默认为应尽可能读写最多字节数。
     * <ol>
     *   <li>对于读请求，默认读取整块内存的所有内存。<li/>
     *   <li>对于写请求，默认写入整个字节数组的所有内容。<li/>
     * </ol>
     *
     * @return 表示最大字节数的 {@code int}。
     */
    int dataLength();

    /**
     * 返回本次 IO 请求相关的许可类型。
     *
     * @return 表示许可类型的 {@code byte}。
     */
    byte permissionType();

    /**
     * 如果本次 IO 操作为写操作，且用户设定了同时操作用户数据，此方法返回用户设置过的元数据。用户元数据保存在 DataBus
     * 主服务内部，而非共享内存块中。元数据与特定内存块绑定，长度应小于等于 1024 字节。
     * <ol>
     *   <li>推荐用户使用元数据存储内存块中数据的描述、格式等信息。但不强制使用。</li>
     *   <li>用户元数据的写入必须与共享内存数据一起写入。</li>
     *   <li>用户元数据的读取可以与共享内存数据一起进行，亦可以通过 API 调用单独读取。</li>
     * </ol>
     *
     * @return 表示被设置的用户数据的 {@code byte[]}。
     */
    byte[] userData();

    /**
     * 返回本次 IO 请求是否同时操作字节数组保存的的用户元数据。
     *
     * @return 表示操作用户数据与否的 {@code boolean}。
     */
    boolean isOperatingUserData();

    /**
     * 返回本次 IO 请求相关的超时时间。如果没有设置超时时限，则返回 {@code Optional.empty()}。
     *
     * @return 表示超时时限的 {@code Optional<Duration>}。
     */
    Optional<Duration> timeoutDuration();

    /**
     * IORequest 的构造器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置许可类型。
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
         * 向当前构建器中设置起始内存地址。
         *
         * @param memoryOffset 表示被设置的起始内存地址 {@code long}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder memoryOffset(long memoryOffset);

        /**
         * 向当前构建器中设置用户自定义键。
         *
         * @param userKey 表示被设置的内存句柄 {@code String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder userKey(String userKey);

        /**
         * 向当前构建器中设置是否附带用户元数据操作。
         *
         * @param isOperatingUserData 表示被设置的内存句柄 {@link boolean}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder isOperatingUserData(boolean isOperatingUserData);

        /**
         * 向当前构建器中设置用户元数据字节数组。仅在写请求里有效。
         *
         * @param data 表示被设置的用户元数据字节数组 {@link byte[]}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder userData(byte[] data);

        /**
         * 向当前构建器中设置超时时限。
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
