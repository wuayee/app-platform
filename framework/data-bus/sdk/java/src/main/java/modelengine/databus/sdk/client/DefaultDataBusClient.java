/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.databus.sdk.client;

import modelengine.databus.sdk.api.DataBusClient;
import modelengine.databus.sdk.client.jni.SharedMemoryReaderWriter;
import modelengine.databus.sdk.memory.SharedMemoryInternal;
import modelengine.databus.sdk.message.ErrorType;
import modelengine.databus.sdk.message.MessageHeader;
import modelengine.databus.sdk.message.MessageType;
import modelengine.databus.sdk.message.PermissionType;
import modelengine.databus.sdk.support.GetMetaDataRequest;
import modelengine.databus.sdk.support.GetMetaDataResult;
import modelengine.databus.sdk.support.MemoryIoRequest;
import modelengine.databus.sdk.support.MemoryIoResult;
import modelengine.databus.sdk.support.MemoryPermissionResult;
import modelengine.databus.sdk.support.OpenConnectionResult;
import modelengine.databus.sdk.support.ReleaseMemoryRequest;
import modelengine.databus.sdk.support.SharedMemoryRequest;
import modelengine.databus.sdk.support.SharedMemoryResult;
import modelengine.databus.sdk.tools.Constant;
import modelengine.databus.sdk.tools.DataBusUtils;
import modelengine.databus.sdk.tools.SeqGenerator;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Validation;

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
 * @author 王成
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