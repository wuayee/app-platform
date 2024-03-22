/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.api;

import com.huawei.databus.sdk.client.DefaultDataBusClient;
import com.huawei.databus.sdk.support.SharedMemoryKey;
import com.huawei.databus.sdk.support.SharedMemoryRequest;
import com.huawei.databus.sdk.support.SharedMemoryResult;

import java.io.IOException;
import java.net.InetAddress;

/**
 * DataBus 客户端基础 API
 *
 * @author 王成 w00863339
 * @since 2024-03-17
 */
public interface DataBusClient {
    /**
     * 打开通向指定地址和端口的 DataBus 连接
     *
     * @param dataBusAddr 表示 DataBus 服务地址的 {@link InetAddress}
     * @param dataBusPort 表示 DataBus 服务端口的 {@code int}
     * @throws IOException 当连接异常时
     */
    void open(InetAddress dataBusAddr, int dataBusPort) throws IOException;

    /**
     * 向 DataBus 服务发起内存申请请求
     *
     * @param request 表示内存申请请求的 {@link SharedMemoryRequest}
     * @return SharedMemoryResult 表示内存申请结果的 {@link SharedMemoryResult}
     */
    SharedMemoryResult sharedMalloc(SharedMemoryRequest request);

    /**
     * 关闭通向指定地址和端口的 DataBus 连接
     *
     * @throws IOException 当连接异常时，或者连接不存在时
     */
    void close() throws IOException;

    /**
     * 向指定的内存块中写入数据。
     *
     * @param key 表示目标内存块的句柄 {@link SharedMemoryKey}
     * @param readOffset 表示从目标内存块读入地点的偏移量的 {@code long}。
     * @param readLength 表示待读入数据的数量的 {@code long}。
     * @param bytes 表示待写入数据所在数组的 {@code byte[]}。
     * @return 表示读取字节总数的 {@code long}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws IllegalArgumentException 当 {@code bytes} 为 {@code null} 或 {@code readOffset}
     * 或 {@code readLength} 为负数时
     * @throws IndexOutOfBoundsException 当 {@code readOffset} 或者 {@code readOffset + readLength} 超过了内存块长度时，
     * 或者 {@code readLength} 超过了 bytes 长度时
     */
    long readOnce(SharedMemoryKey key, long readOffset, long readLength, byte[] bytes) throws IOException;

    /**
     * 向指定的内存块中写入数据。
     *
     * @param key 表示目标内存块的句柄 {@link SharedMemoryKey}
     * @param writeOffset 表示从目标内存块写入地点的偏移量的 {@code long}。
     * @param writeLength 表示待写入数据的数量的 {@code long}。
     * @param bytes 表示待写入数据所在数组的 {@code byte[]}。
     * @return 表示写入字节总数的 {@code long}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws IllegalArgumentException 当 {@code bytes} 为 {@code null} 或 {@code writeOffset}
     * 或 {@code writeLength} 为负数时
     * @throws IndexOutOfBoundsException 当 {@code writeOffset} 或者 {@code writeOffset + writeLength} 超过了内存块长度时。
     */
    long writeOnce(SharedMemoryKey key, long writeOffset, long writeLength, byte[] bytes) throws IOException;

    /**
     * 获取 DataBus 客户端单例实例
     *
     * @return DataBusClient 客户端实例
     */
    static DataBusClient getClient() {
        return DefaultDataBusClient.getInstance();
    }
}
