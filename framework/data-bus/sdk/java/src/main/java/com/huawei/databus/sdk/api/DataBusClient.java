/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.api;

import com.huawei.databus.sdk.client.DefaultDataBusClient;
import com.huawei.databus.sdk.support.MemoryIoRequest;
import com.huawei.databus.sdk.support.MemoryIoResult;
import com.huawei.databus.sdk.support.SharedMemoryRequest;
import com.huawei.databus.sdk.support.SharedMemoryResult;
import com.huawei.fitframework.inspection.Nonnull;

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
    SharedMemoryResult sharedMalloc(@Nonnull SharedMemoryRequest request);

    /**
     * 关闭通向指定地址和端口的 DataBus 连接
     *
     * @throws IOException 当连接异常时，或者连接不存在时
     */
    void close() throws IOException;

    /**
     * 向指定的内存块中读入数据。
     *
     * @param memoryIORequest 表示读取请求 {@link MemoryIoRequest}
     * @return 表示 IO 操作结果的 {@link MemoryIoRequest}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws IllegalArgumentException 当 offset 的值为非正数时
     * @throws IndexOutOfBoundsException 当读位置超过了内存块大小时
     */
    MemoryIoResult readOnce(@Nonnull MemoryIoRequest memoryIORequest) throws IOException;

    /**
     * 向指定的内存块中写入数据。
     *
     * @param memoryIORequest 表示写入请求 {@link MemoryIoRequest}
     * @return 表示 IO 操作结果的 {@link MemoryIoRequest}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws IllegalArgumentException 当 offset 的值为非正数时
     * @throws IndexOutOfBoundsException 当写位置超过了内存块大小时
     */
    MemoryIoResult writeOnce(@Nonnull MemoryIoRequest memoryIORequest) throws IOException;


    /**
     * 获取 DataBus 客户端单例实例
     *
     * @return DataBusClient 客户端实例
     */
    static DataBusClient getClient() {
        return DefaultDataBusClient.getInstance();
    }
}
