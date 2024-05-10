/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.client;

import com.huawei.databus.sdk.api.DataBusClient;
import com.huawei.databus.sdk.client.jni.SharedMemoryReaderWriter;
import com.huawei.databus.sdk.memory.SharedMemory;
import com.huawei.databus.sdk.message.ErrorType;
import com.huawei.databus.sdk.message.MessageType;
import com.huawei.databus.sdk.message.PermissionType;
import com.huawei.databus.sdk.support.MemoryIoRequest;
import com.huawei.databus.sdk.support.MemoryIoResult;
import com.huawei.databus.sdk.support.OpenConnectionResult;
import com.huawei.databus.sdk.support.SharedMemoryRequest;
import com.huawei.databus.sdk.support.SharedMemoryResult;
import com.huawei.databus.sdk.tools.DataBusUtils;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.inspection.Validation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * DataBus 客户端默认实现
 * 使用单例模式
 *
 * @author 王成 w00863339
 * @since 2024-03-17
 */
public class DefaultDataBusClient implements DataBusClient {
    /**
     * 单例变量
     */
    private static final DefaultDataBusClient INSTANCE = new DefaultDataBusClient();

    private SocketChannel socketChannel;
    private ResponseDispatcher responseDispatcher;
    private boolean isConnected;
    private final Map<Byte, BlockingQueue<ByteBuffer>> replyQueues;
    private SharedMemoryPool sharedMemoryPool;
    private final SharedMemoryReaderWriter sharedMemoryReaderWriter;


    private DefaultDataBusClient() {
        this.isConnected = false;
        this.sharedMemoryReaderWriter = new SharedMemoryReaderWriter();

        Map<Byte, BlockingQueue<ByteBuffer>> tmpQueues = new HashMap<>();
        tmpQueues.put(MessageType.HeartBeat, new LinkedBlockingQueue<>());
        tmpQueues.put(MessageType.ApplyMemory, new LinkedBlockingQueue<>());
        tmpQueues.put(MessageType.ApplyPermission, new LinkedBlockingQueue<>());

        this.replyQueues = Collections.unmodifiableMap(tmpQueues);
    }

    @Override
    public OpenConnectionResult open(InetAddress dataBusAddr, int dataBusPort) {
        try {
            this.socketChannel = SocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(dataBusAddr, dataBusPort);
            socketChannel.connect(address);
        } catch (IOException e) {
            return OpenConnectionResult.failure(ErrorType.NotConnectedToDataBus, e);
        }

        this.responseDispatcher = new ResponseDispatcher(this.replyQueues, socketChannel);
        this.sharedMemoryPool = new SharedMemoryPool(this.replyQueues, socketChannel);
        this.responseDispatcher.start();
        this.isConnected = true;

        return OpenConnectionResult.success();
    }

    @Override
    public SharedMemoryResult sharedMalloc(@Nonnull SharedMemoryRequest request) {
        if (!isConnected()) {
            return SharedMemoryResult.failure(ErrorType.NotConnectedToDataBus);
        }
        DataBusUtils.verifyMemoryRequest(request);
        return this.sharedMemoryPool.applySharedMemory(request);
    }

    @Override
    public void close() throws IOException {
        socketChannel.close();
        this.responseDispatcher.stop();
    }

    @Override
    public MemoryIoResult readOnce(@Nonnull MemoryIoRequest request) {
        return this.doIoRequest(request, PermissionType.Read);
    }

    @Override
    public MemoryIoResult writeOnce(@Nonnull MemoryIoRequest request) {
        return this.doIoRequest(request, PermissionType.Write);
    }

    /**
     * 获取 {@link DefaultDataBusClient} 的单例对象
     *
     * @return {@link DefaultDataBusClient}
     */
    public static DefaultDataBusClient getInstance() {
        return INSTANCE;
    }

    private MemoryIoResult doIoRequest(MemoryIoRequest request, byte permissionType) {
        // 核验参数有效性以及是否连接到 DataBus 主程序
        if (!this.isConnected()) {
            return MemoryIoResult.failure(ErrorType.NotConnectedToDataBus);
        }
        DataBusUtils.verifyIoRequest(request, permissionType);
        Validation.isFalse(this.sharedMemoryPool.isMemoryIdMissing(request.sharedMemoryKey()),
                () -> new IllegalArgumentException("Self applied memory should set memory ID."));
        // 当前内存上锁
        SharedMemory memory = this.sharedMemoryPool.getOrAddMemory(request.sharedMemoryKey());
        memory.lock().lock();

        try {
            // 申请内存许可
            SharedMemoryResult result = this.sharedMemoryPool.applyPermission(request);
            if (!result.isSuccess()) {
                return MemoryIoResult.failure(result.errorType());
            }

            // 检查写操作是否在内存边界内
            if (request.memoryOffset() + request.dataLength() > result.sharedMemory().size()) {
                // TD: 使用更精确的错误码
                return MemoryIoResult.failure(ErrorType.PermissionDenied);
            }

            // 执行读写操作
            byte[] readBytes;
            if (permissionType == PermissionType.Read) {
                readBytes = this.sharedMemoryReaderWriter.read(request.sharedMemoryKey().memoryId(),
                        request.memoryOffset(), request.dataLength());
            } else {
                readBytes = request.bytes();
                this.sharedMemoryReaderWriter.write(request.sharedMemoryKey().memoryId(), request.memoryOffset(),
                        request.dataLength(), readBytes);
            }

            // 返回读写成功结果
            return MemoryIoResult.success(memory, readBytes, request.permissionType());
        } catch (IOException e) {
            // 日志打印真实错误信息
            return MemoryIoResult.failure(permissionType == PermissionType.Read ? ErrorType.MemoryReadError
                    : ErrorType.MemoryWriteError, e);
        } finally {
            // 释放内存许可
            this.sharedMemoryPool.releasePermission(request);
            memory.lock().unlock();
        }
    }

    /**
     * 返回客户端当前状态
     *
     * @return 表示客户端当前是否在连接的 {@code boolean}。
     */
    public boolean isConnected() {
        return isConnected;
    }
}