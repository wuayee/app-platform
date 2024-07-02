/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.api;

import com.huawei.databus.sdk.client.DefaultDataBusClient;
import com.huawei.databus.sdk.support.GetMetaDataRequest;
import com.huawei.databus.sdk.support.GetMetaDataResult;
import com.huawei.databus.sdk.support.MemoryIoRequest;
import com.huawei.databus.sdk.support.MemoryIoResult;
import com.huawei.databus.sdk.support.OpenConnectionResult;
import com.huawei.databus.sdk.support.ReleaseMemoryRequest;
import com.huawei.databus.sdk.support.SharedMemoryRequest;
import com.huawei.databus.sdk.support.SharedMemoryResult;
import com.huawei.fitframework.inspection.Nonnull;

import java.net.InetAddress;

/**
 * DataBus 客户端基础 API。
 *
 * @author 王成 w00863339
 * @since 2024-03-17
 */
public interface DataBusClient {
    /**
     * 打开通向指定地址和端口的 DataBus 连接。
     *
     * @param dataBusAddr 表示 DataBus 服务地址的 {@link InetAddress}。
     * @param dataBusPort 表示 DataBus 服务端口的 {@code int}。
     * @return 表示连接结果的 {@link OpenConnectionResult}。
     */
    OpenConnectionResult open(InetAddress dataBusAddr, int dataBusPort);

    /**
     * 向 DataBus 服务发起内存申请请求。
     *
     * @param request 表示内存申请请求的 {@link SharedMemoryRequest}。
     * @return SharedMemoryResult 表示内存申请结果的 {@link SharedMemoryResult}。
     */
    SharedMemoryResult sharedMalloc(@Nonnull SharedMemoryRequest request);

    /**
     * 向 DataBus 服务发起内存释放请求。内存具体释放的时机由 DataBus 服务确定，因此本 API 不返回结果。
     *
     * @param request 表示内存释放请求的 {@link ReleaseMemoryRequest}。
     */
    void sharedFree(@Nonnull ReleaseMemoryRequest request);

    /**
     * 关闭通向指定地址和端口的 DataBus 连接。
     */
    void close();

    /**
     * 从指定的内存块中读入数据。同一内存块不可并发读写，不同内存块可以并发读写。
     *
     * @param memoryIORequest 表示读取请求 {@link MemoryIoRequest}。
     * @return 表示 IO 操作结果的 {@link MemoryIoRequest}。
     */
    MemoryIoResult readOnce(@Nonnull MemoryIoRequest memoryIORequest);

    /**
     * 读取指定的内存块的元数据。
     *
     * @param getMetaDataRequest 表示读取元数据请求 {@link GetMetaDataRequest}。
     * @return 表示获取元数据操作结果的 {@link GetMetaDataResult}。
     */
    GetMetaDataResult readMetaData(@Nonnull GetMetaDataRequest getMetaDataRequest);

    /**
     * 向指定的内存块中读入数据。同一内存块不可并发读写，不同内存块可以并发读写。
     *
     * @param memoryIORequest 表示写入请求 {@link MemoryIoRequest}。
     * @return 表示 IO 操作结果的 {@link MemoryIoResult}。
     */
    MemoryIoResult writeOnce(@Nonnull MemoryIoRequest memoryIORequest);

    /**
     * 返回客户端当前状态。
     *
     * @return 表示客户端当前是否在连接的 {@code boolean}。
     */
    boolean isConnected();

    /**
     * 获取 DataBus 客户端单例实例。
     *
     * @return DataBusClient 客户端实例。
     */
    static DataBusClient getClient() {
        return DefaultDataBusClient.getInstance();
    }
}
