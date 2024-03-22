/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.client;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.databus.sdk.api.DataBusClient;
import com.huawei.databus.sdk.api.DataBusResult;
import com.huawei.databus.sdk.message.ApplyMemoryMessage;
import com.huawei.databus.sdk.message.ErrorType;
import com.huawei.databus.sdk.message.MessageHeader;
import com.huawei.databus.sdk.message.MessageType;
import com.huawei.databus.sdk.support.SharedMemoryKey;
import com.huawei.databus.sdk.support.SharedMemoryRequest;
import com.huawei.databus.sdk.support.SharedMemoryResult;
import com.huawei.databus.sdk.tools.Constant;
import com.huawei.fitframework.inspection.Validation;

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
    private final Map<Byte, BlockingQueue<DataBusResult>> replyQueues;

    private DefaultDataBusClient() {
        this.isConnected = false;

        Map<Byte, BlockingQueue<DataBusResult>> tmpQueues = new HashMap<>();
        tmpQueues.put(MessageType.HeartBeat, new LinkedBlockingQueue<>());
        tmpQueues.put(MessageType.ApplyMemory, new LinkedBlockingQueue<>());

        this.replyQueues = Collections.unmodifiableMap(tmpQueues);
    }

    @Override
    public void open(InetAddress dataBusAddr, int dataBusPort) throws IOException {
        this.socketChannel = SocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(dataBusAddr, dataBusPort);
        socketChannel.connect(address);
        this.responseDispatcher = new ResponseDispatcher(this.replyQueues, socketChannel);
        this.responseDispatcher.start();
        this.isConnected = true;
    }

    @Override
    public SharedMemoryResult sharedMalloc(SharedMemoryRequest request) {
        // 先建造消息体
        FlatBufferBuilder bodyBuilder = new FlatBufferBuilder();
        ApplyMemoryMessage.startApplyMemoryMessage(bodyBuilder);
        ApplyMemoryMessage.addMemorySize(bodyBuilder, request.size());
        int messageOffset = ApplyMemoryMessage.endApplyMemoryMessage(bodyBuilder);
        bodyBuilder.finish(messageOffset);
        ByteBuffer messageBodyBuffer = bodyBuilder.dataBuffer();

        // 建造消息头
        FlatBufferBuilder headerBuilder = new FlatBufferBuilder();
        MessageHeader.startMessageHeader(headerBuilder);
        MessageHeader.addType(headerBuilder, MessageType.ApplyMemory);
        MessageHeader.addSize(headerBuilder, messageBodyBuffer.remaining());
        messageOffset = ApplyMemoryMessage.endApplyMemoryMessage(headerBuilder);
        headerBuilder.finish(messageOffset);
        ByteBuffer messageHeaderBuffer = headerBuilder.dataBuffer();
        Validation.equals(messageHeaderBuffer.remaining(),
                Constant.DATABUS_SERVICE_HEADER_SIZE, "Message header size MUST match the preconfigured value.");

        try {
            socketChannel.write(new ByteBuffer[]{messageHeaderBuffer, messageBodyBuffer});
            // 阻塞等待回复
            return cast(this.replyQueues.get(MessageType.ApplyMemory).take());
        } catch (IOException | InterruptedException e) {
            // TODO: 错误码
            return SharedMemoryResult.failure(ErrorType.None);
        }
    }

    @Override
    public void close() throws IOException {
        socketChannel.close();
        this.responseDispatcher.stop();
    }

    @Override
    public long readOnce(SharedMemoryKey key, long readOffset, long readLength, byte[] bytes) {
        return 0;
    }

    @Override
    public long writeOnce(SharedMemoryKey key, long writeOffset, long writeLength, byte[] bytes) {
        return 0;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static DefaultDataBusClient getInstance() {
        return INSTANCE;
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