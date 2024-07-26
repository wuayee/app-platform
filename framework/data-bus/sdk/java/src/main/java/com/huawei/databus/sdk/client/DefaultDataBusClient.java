/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.client;

import com.huawei.databus.sdk.api.DataBusClient;
import com.huawei.databus.sdk.client.jni.SharedMemoryReaderWriter;
import com.huawei.databus.sdk.memory.SharedMemoryInternal;
import com.huawei.databus.sdk.message.ErrorType;
import com.huawei.databus.sdk.message.MessageHeader;
import com.huawei.databus.sdk.message.MessageType;
import com.huawei.databus.sdk.message.PermissionType;
import com.huawei.databus.sdk.support.GetMetaDataRequest;
import com.huawei.databus.sdk.support.GetMetaDataResult;
import com.huawei.databus.sdk.support.MemoryIoRequest;
import com.huawei.databus.sdk.support.MemoryIoResult;
import com.huawei.databus.sdk.support.MemoryPermissionResult;
import com.huawei.databus.sdk.support.OpenConnectionResult;
import com.huawei.databus.sdk.support.ReleaseMemoryRequest;
import com.huawei.databus.sdk.support.SharedMemoryRequest;
import com.huawei.databus.sdk.support.SharedMemoryResult;
import com.huawei.databus.sdk.tools.Constant;
import com.huawei.databus.sdk.tools.DataBusUtils;
import com.huawei.databus.sdk.tools.SeqGenerator;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.inspection.Validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DataBus 客户端默认实现。
 * 使用单例模式。
 *
 * @author 王成 w00863339
 * @since 2024-03-17
 */
public class DefaultDataBusClient implements DataBusClient {
    private static final Logger logger = LogManager.getLogger(DefaultDataBusClient.class);

    /**
     * 单例变量。
     */
    private static final DefaultDataBusClient INSTANCE = new DefaultDataBusClient();

    private SocketChannel socketChannel;
    private ResponseDispatcher responseDispatcher;
    private final Map<Long, BlockingQueue<ByteBuffer>> replyQueues;
    private SharedMemoryPool sharedMemoryPool;
    private final SharedMemoryReaderWriter sharedMemoryReaderWriter;


    private DefaultDataBusClient() {
        this.sharedMemoryReaderWriter = new SharedMemoryReaderWriter();
        this.replyQueues = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized OpenConnectionResult open(InetAddress dataBusAddr, int dataBusPort) {
        if (this.responseDispatcher != null && this.responseDispatcher.isRunning()) {
            return OpenConnectionResult.success();
        }
        if (!DataBusUtils.isSupportedPlatform()) {
            return OpenConnectionResult.failure(ErrorType.PlatformNotSupported);
        }
        try {
            this.socketChannel = SocketChannel.open();
            // 设置TCP_NODELAY，禁用Nagle算法，以防止粘包问题。
            this.socketChannel.socket().setTcpNoDelay(true);
            InetSocketAddress address = new InetSocketAddress(dataBusAddr, dataBusPort);
            this.socketChannel.socket().connect(address, Constant.DEFAULT_WAITING_TIME_CONNECT_MILLIS);
            if (!this.sayHello()) {
                return this.cleanUpConnection(null);
            }
        } catch (IOException e) {
            return this.cleanUpConnection(e);
        }
        logger.info("[open] Connection established and hello phase passed.");
        this.responseDispatcher = new ResponseDispatcher(this.replyQueues, this.socketChannel);
        this.sharedMemoryPool = new SharedMemoryPool(this.replyQueues, this.socketChannel);
        this.responseDispatcher.start();

        return OpenConnectionResult.success();
    }

    @Override
    public synchronized SharedMemoryResult sharedMalloc(@Nonnull SharedMemoryRequest request) {
        if (!isConnected()) {
            return SharedMemoryResult.failure(ErrorType.NotConnectedToDataBus);
        }
        DataBusUtils.verifyMemoryRequest(request);
        return this.sharedMemoryPool.applySharedMemory(request);
    }

    @Override
    public void sharedFree(@Nonnull ReleaseMemoryRequest request) {
        if (!isConnected()) {
            return;
        }
        Validation.notNull(request, "The release memory request cannot be null.");
        this.sharedMemoryPool.releaseSharedMemory(request);
    }

    @Override
    public void close() {
        if (this.responseDispatcher != null) {
            this.responseDispatcher.shutdownGracefully();
        }
    }

    @Override
    public MemoryIoResult readOnce(@Nonnull MemoryIoRequest request) {
        return this.doIoRequest(request, PermissionType.Read);
    }

    @Override
    public GetMetaDataResult readMetaData(@Nonnull GetMetaDataRequest request) {
        if (!this.isConnected()) {
            return GetMetaDataResult.failure(ErrorType.NotConnectedToDataBus);
        }
        Validation.notNull(request, "The release memory request cannot be null.");
        return this.sharedMemoryPool.getMemoryMetaData(request);
    }

    @Override
    public MemoryIoResult writeOnce(@Nonnull MemoryIoRequest request) {
        return this.doIoRequest(request, PermissionType.Write);
    }

    /**
     * 获取 {@link DefaultDataBusClient} 的单例对象。
     *
     * @return {@link DefaultDataBusClient}。
     */
    public static DefaultDataBusClient getInstance() {
        return INSTANCE;
    }

    private MemoryIoResult doIoRequest(MemoryIoRequest request, byte permissionType) {
        // 核验参数有效性以及是否连接到 DataBus 主程序。
        if (!this.isConnected()) {
            return MemoryIoResult.failure(ErrorType.NotConnectedToDataBus);
        }
        DataBusUtils.verifyIoRequest(request, permissionType);

        // 当前内存上锁。
        SharedMemoryInternal memory = this.sharedMemoryPool.getOrAddMemory(request.userKey());
        memory.lock().lock();

        try {
            // 申请内存许可。
            MemoryPermissionResult result = this.sharedMemoryPool.applyPermission(request, memory);
            if (!result.isSuccess()) {
                return MemoryIoResult.failure(result.errorType());
            }

            // 检查读写操作是否在内存边界内。
            if (request.memoryOffset() + request.dataLength() > result.sharedMemory().size()) {
                return MemoryIoResult.failure(ErrorType.IOOutOfBounds);
            }

            // 检查是否为0长度数组。
            if (result.sharedMemory().size() == 0) {
                return MemoryIoResult.success(memory, new byte[0], result.userData(), request.permissionType());
            }

            // 检查 memoryId 是否被正确设置。
            if (memory.sharedMemoryKey().memoryId() == -1) {
                return MemoryIoResult.failure(ErrorType.KeyNotFound);
            }

            // 执行读写操作。
            byte[] ioBytes;
            if (permissionType == PermissionType.Read) {
                long readLength = request.dataLength() == 0 ? memory.size() : request.dataLength();
                ioBytes = this.sharedMemoryReaderWriter.read(memory.sharedMemoryKey().memoryId(),
                        request.memoryOffset(), readLength);
            } else {
                ioBytes = request.bytes();
                long writeLength = request.dataLength() == 0 ? ioBytes.length : request.dataLength();
                this.sharedMemoryReaderWriter.write(memory.sharedMemoryKey().memoryId(), request.memoryOffset(),
                        writeLength, ioBytes);
            }

            // 返回读写成功结果。
            return MemoryIoResult.success(memory, ioBytes, result.userData(), request.permissionType());
        } catch (IOException e) {
            logger.error("[doIoRequest] IO request failed.", e);
            return MemoryIoResult.failure(permissionType == PermissionType.Read ? ErrorType.MemoryReadError
                    : ErrorType.MemoryWriteError, e);
        } finally {
            // 释放内存许可
            this.sharedMemoryPool.releasePermission(request, memory);
            memory.lock().unlock();
        }
    }

    /**
     * 返回客户端当前状态。
     *
     * @return 表示客户端当前是否在连接的 {@code boolean}。
     */
    @Override
    public boolean isConnected() {
        return this.responseDispatcher != null && this.responseDispatcher.isRunning();
    }

    private OpenConnectionResult cleanUpConnection(IOException e) {
        if (this.socketChannel != null) {
            try {
                this.socketChannel.close();
            } catch (IOException ex) {
                logger.error("[cleanUp] close connection failed with exception.", ex);
            }
        }

        if (e != null) {
            logger.error("[cleanUp] Open connection failed with exception.", e);
            return OpenConnectionResult.failure(ErrorType.NotConnectedToDataBus, e);
        }
        return OpenConnectionResult.failure(ErrorType.NotConnectedToDataBus);
    }

    private boolean sayHello() throws IOException {
        // 建造消息头。 Hello 消息只有消息头，为了保持消息头长度恒定，需要将其消息体长度域设置为 -1 定值。
        long seq = SeqGenerator.getInstance().getNextNumber();
        ByteBuffer messageHeaderBuffer = DataBusUtils.buildMessageHeader(MessageType.Hello, -1, seq);
        this.socketChannel.write(messageHeaderBuffer);
        ByteBuffer buffer = ByteBuffer.allocate(Constant.DATABUS_SERVICE_HEADER_SIZE);
        int bytesRead = this.socketChannel.read(buffer);
        if (bytesRead != Constant.DATABUS_SERVICE_HEADER_SIZE) {
            return false;
        }
        buffer.flip();
        return MessageHeader.getRootAsMessageHeader(buffer).type() == MessageType.Hello;
    }
}