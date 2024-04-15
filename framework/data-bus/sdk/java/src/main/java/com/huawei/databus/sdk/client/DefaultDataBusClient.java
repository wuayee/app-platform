/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.client;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.databus.sdk.api.DataBusClient;
import com.huawei.databus.sdk.api.DataBusIoResult;
import com.huawei.databus.sdk.client.jni.SharedMemoryReaderWriter;
import com.huawei.databus.sdk.memory.SharedMemory;
import com.huawei.databus.sdk.message.ApplyMemoryMessage;
import com.huawei.databus.sdk.message.ApplyMemoryMessageResponse;
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
import com.huawei.fitframework.util.StringUtils;

import com.google.flatbuffers.FlatBufferBuilder;

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
        if (!isConnected) {
            return SharedMemoryResult.failure(ErrorType.NotConnectedToDataBus, null);
        }
        DataBusUtils.verifyMemoryRequest(request);
        // 应该全部抽取到 sharedMemoryPool
        // 先建造消息体
        FlatBufferBuilder bodyBuilder = new FlatBufferBuilder();
        ApplyMemoryMessage.startApplyMemoryMessage(bodyBuilder);
        ApplyMemoryMessage.addMemorySize(bodyBuilder, request.size());
        int messageOffset = ApplyMemoryMessage.endApplyMemoryMessage(bodyBuilder);
        bodyBuilder.finish(messageOffset);
        ByteBuffer messageBodyBuffer = bodyBuilder.dataBuffer();

        // 建造消息头
        ByteBuffer messageHeaderBuffer = DataBusUtils.buildMessageHeader(MessageType.ApplyMemory,
                messageBodyBuffer.remaining());

        try {
            socketChannel.write(new ByteBuffer[]{messageHeaderBuffer, messageBodyBuffer});
            // 阻塞等待回复
            ByteBuffer resBuffer = this.replyQueues.get(MessageType.ApplyMemory).take();
            ApplyMemoryMessageResponse response =
                    ApplyMemoryMessageResponse.getRootAsApplyMemoryMessageResponse(resBuffer);
            if (response.errorType() == ErrorType.None) {
                SharedMemory sharedMemory = this.sharedMemoryPool.addNewMemory(response.memoryKey(),
                        PermissionType.None, response.memorySize());
                return SharedMemoryResult.success(sharedMemory);
            }
            return SharedMemoryResult.failure(response.errorType());
        } catch (IOException | InterruptedException e) {
            // TODO: 错误码
            return SharedMemoryResult.failure(ErrorType.OutOfMemory, e);
        }
    }

    @Override
    public void close() throws IOException {
        socketChannel.close();
        this.responseDispatcher.stop();
    }

    @Override
    public MemoryIoResult readOnce(@Nonnull MemoryIoRequest request) {
        DataBusIoResult preResult = ioPreprocess(request, PermissionType.Read);
        if (!preResult.isSuccess()) {
            return cast(preResult);
        }

        try {
            byte[] readBytes = sharedMemoryReaderWriter.read(request.sharedMemoryKey().getMemoryId(),
                    request.memoryOffset(), request.dataLength());
            return MemoryIoResult.success(preResult.sharedMemory(), readBytes, request.permissionType());
        } catch (IOException e) {
            // 日志打印真实错误信息
            return MemoryIoResult.failure(ErrorType.Timeout);
        }
    }

    @Override
    public MemoryIoResult writeOnce(@Nonnull MemoryIoRequest request) {
        DataBusIoResult preResult = ioPreprocess(request, PermissionType.Write);
        if (!preResult.isSuccess()) {
            return cast(preResult);
        }

        try {
            sharedMemoryReaderWriter.write(request.sharedMemoryKey().getMemoryId(), request.memoryOffset(),
                    request.dataLength(), request.bytes());
            return MemoryIoResult.success(preResult.sharedMemory(), request.bytes(), request.permissionType());
        } catch (IOException e) {
            // 日志打印真实错误信息
            return MemoryIoResult.failure(ErrorType.Timeout);
        }
    }


    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * 获取 {@link DefaultDataBusClient} 的单例对象
     *
     * @return {@link DefaultDataBusClient}
     */
    public static DefaultDataBusClient getInstance() {
        return INSTANCE;
    }

    private DataBusIoResult ioPreprocess(MemoryIoRequest request, byte permissionType) {
        if (!isConnected) {
            return MemoryIoResult.failure(ErrorType.NotConnectedToDataBus);
        }
        DataBusUtils.verifyIoRequest(request, permissionType);
        SharedMemoryResult result = sharedMemoryPool.applyPermission(request);
        if (!result.isSuccess()) {
            return MemoryIoResult.failure(result.errorType());
        }
        // 检查写操作是否在内存边界内
        Validation.lessThanOrEquals(request.memoryOffset() + request.dataLength(), result.sharedMemory().size(),
                () -> new IllegalArgumentException(StringUtils.format(
                        "You have accessed beyond the legitimate memory range. offset={0}, length={1}, size={2}",
                        request.memoryOffset(), request.dataLength(), result.sharedMemory().size())));

        return result;
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