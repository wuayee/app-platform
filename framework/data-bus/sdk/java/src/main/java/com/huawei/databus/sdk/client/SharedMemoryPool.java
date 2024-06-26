/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.client;

import com.huawei.databus.sdk.memory.SharedMemory;
import com.huawei.databus.sdk.memory.SharedMemoryInternal;
import com.huawei.databus.sdk.memory.SharedMemoryKey;
import com.huawei.databus.sdk.message.ApplyMemoryMessage;
import com.huawei.databus.sdk.message.ApplyMemoryMessageResponse;
import com.huawei.databus.sdk.message.ApplyPermissionMessage;
import com.huawei.databus.sdk.message.ApplyPermissionMessageResponse;
import com.huawei.databus.sdk.message.ErrorType;
import com.huawei.databus.sdk.message.GetMetaDataMessage;
import com.huawei.databus.sdk.message.GetMetaDataMessageResponse;
import com.huawei.databus.sdk.message.MessageType;
import com.huawei.databus.sdk.message.PermissionType;
import com.huawei.databus.sdk.message.ReleaseMemoryMessage;
import com.huawei.databus.sdk.message.ReleasePermissionMessage;
import com.huawei.databus.sdk.support.GetMetaDataRequest;
import com.huawei.databus.sdk.support.GetMetaDataResult;
import com.huawei.databus.sdk.support.MemoryIoRequest;
import com.huawei.databus.sdk.support.MemoryPermissionResult;
import com.huawei.databus.sdk.support.ReleaseMemoryRequest;
import com.huawei.databus.sdk.support.SharedMemoryRequest;
import com.huawei.databus.sdk.support.SharedMemoryResult;
import com.huawei.databus.sdk.tools.Constant;
import com.huawei.databus.sdk.tools.DataBusUtils;
import com.huawei.databus.sdk.tools.SeqGenerator;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.StringUtils;

import com.google.flatbuffers.FlatBufferBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 内存池，管理当前所有内存块的生命周期，包括内存申请、许可管理和释放。
 *
 * @author 王成 w00863339
 * @since 2024/3/25
 */
class SharedMemoryPool {
    private static final Logger logger = LogManager.getLogger(SharedMemoryPool.class);

    private final Map<String, SharedMemoryInternal> memoryPool;
    private final Map<Long, BlockingQueue<ByteBuffer>> replyQueues;
    private final SocketChannel socketChannel;
    private final SeqGenerator seqGenerator;

    public SharedMemoryPool(Map<Long, BlockingQueue<ByteBuffer>> replyQueues, SocketChannel socketChannel) {
        this.memoryPool = new HashMap<>();
        this.replyQueues = replyQueues;
        this.socketChannel = socketChannel;
        this.seqGenerator = SeqGenerator.getInstance();
    }

    /**
     * 根据传入的 {@link MemoryIoRequest} 申请新的共享内存。
     *
     * @param request 传入的 {@link SharedMemoryRequest}。
     * @return 表示共享内存结果的 {@link SharedMemoryResult}。
     */
    public SharedMemoryResult applySharedMemory(@Nonnull SharedMemoryRequest request) {
        Validation.notNull(request, "The apply memory request cannot be null.");
        // 先建造消息体
        FlatBufferBuilder bodyBuilder = new FlatBufferBuilder();
        int objectKeyOffset = bodyBuilder.createString(request.userKey());
        ApplyMemoryMessage.startApplyMemoryMessage(bodyBuilder);
        ApplyMemoryMessage.addObjectKey(bodyBuilder, objectKeyOffset);
        ApplyMemoryMessage.addMemorySize(bodyBuilder, request.size());
        int messageOffset = ApplyMemoryMessage.endApplyMemoryMessage(bodyBuilder);
        bodyBuilder.finish(messageOffset);
        ByteBuffer messageBodyBuffer = bodyBuilder.dataBuffer();

        // 建造消息头
        long seq = seqGenerator.getNextNumber();
        ByteBuffer messageHeaderBuffer = DataBusUtils.buildMessageHeader(MessageType.ApplyMemory,
                messageBodyBuffer.remaining(), seq);

        try {
            ByteBuffer resBuf = getReply(seq, messageHeaderBuffer, messageBodyBuffer);
            if (resBuf == null) {
                logger.error("[applySharedMemory] Apply memory timeout. [seq={}]", seq);
                return SharedMemoryResult.failure(ErrorType.Timeout);
            }
            ApplyMemoryMessageResponse response =
                    ApplyMemoryMessageResponse.getRootAsApplyMemoryMessageResponse(resBuf);
            if (response.errorType() == ErrorType.None) {
                SharedMemory sharedMemory = this.addNewMemory(response.memoryKey(), request.userKey(),
                        PermissionType.None, response.memorySize());
                return SharedMemoryResult.success(sharedMemory);
            }
            return SharedMemoryResult.failure(response.errorType());
        } catch (IOException | InterruptedException e) {
            logger.error("[applySharedMemory] unexpected exception. [e={}, seq={}]", e.toString(), seq);
            return SharedMemoryResult.failure(ErrorType.UnknownError, e);
        }
    }

    /**
     * 通过共享内存句柄获取内存信息。如果共享内存不存在，则根据 key 生成新的共享内存。
     *
     * @param key 表示内存句柄的 {@link SharedMemoryKey}。
     * @return 表示内存的 {@link SharedMemoryInternal}。
     */
    public SharedMemoryInternal getOrAddMemory(String key) {
        SharedMemoryInternal internal = new SharedMemoryInternal(new SharedMemoryKey(key), PermissionType.None, 0);
        SharedMemoryInternal memory = this.memoryPool.putIfAbsent(key, internal);
        return memory == null ? internal : memory;
    }

    /**
     * 通过用户指定 key 获取内存信息。
     *
     * @param key 表示内存句柄的 {@link SharedMemoryKey}。
     * @return 表示内存的 {@link SharedMemoryInternal}。
     */
    public Optional<SharedMemoryInternal> getMemory(String key) {
        return Optional.ofNullable(this.memoryPool.get(key));
    }

    /**
     * 将客户端申请到的内存加入内存池，并返回其只读视图。
     *
     * @param memoryId 表示内存系统级句柄的 {@code int}。
     * @param userKey 表示用户自定义 key 的 {@code Optional<String>}。
     * @param permission 表示内存权限的 {@code byte}。
     * @param size 表示内存容量的 {@code long}。
     * @return 表示内存的 {@link SharedMemoryInternal}。
     */
    private SharedMemoryInternal addNewMemory(int memoryId, String userKey, byte permission, long size) {
        SharedMemoryInternal internal = new SharedMemoryInternal(
                new SharedMemoryKey(memoryId, userKey), permission, size);
        this.memoryPool.put(userKey, internal);
        return internal;
    }

    /**
     * 根据传入的 {@link MemoryIoRequest} 申请读写权限。
     *
     * @param request 客户端传入 {@link MemoryIoRequest}。
     * @param memory 需要申请权限的内存 {@link SharedMemoryInternal}。
     * @return 表示权限申请结果的 {@link MemoryPermissionResult}。
     */
    public MemoryPermissionResult applyPermission(@Nonnull MemoryIoRequest request,
                                                  @Nonnull SharedMemoryInternal memory) {
        Validation.notNull(request, "The apply permission request cannot be null.");
        Validation.notNull(memory, "The apply permission memory cannot be null.");
        FlatBufferBuilder bodyBuilder = new FlatBufferBuilder();
        int objectKeyOffset = bodyBuilder.createString(getUserKeyIfIdAbsent(memory.sharedMemoryKey()));
        byte[] userData = request.isOperatingUserData() && request.permissionType() == PermissionType.Write
                ? request.userData() : new byte[0];
        int userDataOffset = ApplyPermissionMessage.createUserDataVector(bodyBuilder, userData);

        ApplyPermissionMessage.startApplyPermissionMessage(bodyBuilder);
        ApplyPermissionMessage.addPermission(bodyBuilder, request.permissionType());
        ApplyPermissionMessage.addObjectKey(bodyBuilder, objectKeyOffset);
        ApplyPermissionMessage.addMemoryKey(bodyBuilder, memory.sharedMemoryKey().memoryId());
        ApplyPermissionMessage.addIsOperatingUserData(bodyBuilder, request.isOperatingUserData());
        ApplyPermissionMessage.addUserData(bodyBuilder, userDataOffset);
        int messageOffset = ApplyPermissionMessage.endApplyPermissionMessage(bodyBuilder);
        bodyBuilder.finish(messageOffset);
        ByteBuffer messageBodyBuffer = bodyBuilder.dataBuffer();

        // 建造消息头。
        long seq = seqGenerator.getNextNumber();
        ByteBuffer messageHeaderBuffer = DataBusUtils.buildMessageHeader(MessageType.ApplyPermission,
                messageBodyBuffer.remaining(), seq);
        try {
            ByteBuffer resBuf = getReply(seq, messageHeaderBuffer, messageBodyBuffer);
            if (resBuf == null) {
                logger.error("[applyPermission] Apply memory timeout. [seq={}]", seq);
                return MemoryPermissionResult.failure(ErrorType.Timeout);
            }

            ApplyPermissionMessageResponse response =
                    ApplyPermissionMessageResponse.getRootAsApplyPermissionMessageResponse(resBuf);
            // 修改本地内存信息。
            if (response.errorType() == ErrorType.None) {
                memory.setPermission(request.permissionType()).setSize(response.memorySize())
                        .setMemoryId(response.memoryKey());
                if (request.isOperatingUserData() && request.permissionType() == PermissionType.Read) {
                    byte[] resData = getUserData(response.userDataAsByteBuffer());
                    return MemoryPermissionResult.success(memory.getView(), resData);
                }
                return MemoryPermissionResult.success(memory.getView(), null);
            }
            return MemoryPermissionResult.failure(response.errorType());
        } catch (IOException | InterruptedException e) {
            logger.error("[applyPermission] unexpected exception. [e={}, seq={}]", e.toString(), seq);
            return MemoryPermissionResult.failure(ErrorType.UnknownError, e);
        }
    }

    /**
     * 根据传入的 {@link MemoryIoRequest} 释放读写许可。
     *
     * @param request 传入的 {@link MemoryIoRequest}。
     * @param memory 需要申请权限的内存 {@link SharedMemoryInternal}。
     */
    public void releasePermission(@Nonnull MemoryIoRequest request, @Nonnull SharedMemoryInternal memory) {
        Validation.notNull(request, "The release permission request cannot be null.");
        Validation.notNull(memory, "The release permission memory cannot be null.");
        FlatBufferBuilder bodyBuilder = new FlatBufferBuilder();
        int objectKeyOffset = bodyBuilder.createString(getUserKeyIfIdAbsent(memory.sharedMemoryKey()));
        ReleasePermissionMessage.startReleasePermissionMessage(bodyBuilder);
        ReleasePermissionMessage.addPermission(bodyBuilder, request.permissionType());
        ReleasePermissionMessage.addObjectKey(bodyBuilder, objectKeyOffset);
        ReleasePermissionMessage.addMemoryKey(bodyBuilder, memory.sharedMemoryKey().memoryId());
        int messageOffset = ReleasePermissionMessage.endReleasePermissionMessage(bodyBuilder);
        bodyBuilder.finish(messageOffset);
        ByteBuffer messageBodyBuffer = bodyBuilder.dataBuffer();

        // 建造消息头。
        long seq = seqGenerator.getNextNumber();
        ByteBuffer messageHeaderBuffer = DataBusUtils.buildMessageHeader(MessageType.ReleasePermission,
                messageBodyBuffer.remaining(), seq);
        try {
            // 发出信息后即刻返回，释放许可总是被 DataBus 主服务批准而且没有回复信息。
            this.socketChannel.write(new ByteBuffer[]{messageHeaderBuffer, messageBodyBuffer});
        } catch (IOException e) {
            logger.error("[releasePermission] socket channel exception. [e={}, seq={}]", e.toString(), seq);
        } finally {
            // 客户端不再持有此内存块的读写许可。
            memory.setPermission(PermissionType.None);
        }
    }

    /**
     * 根据传入的 {@link ReleaseMemoryRequest} 释放内存。
     *
     * @param request 传入的 {@link ReleaseMemoryRequest}。
     */
    public void releaseSharedMemory(@Nonnull ReleaseMemoryRequest request) {
        Validation.notNull(request, "The release memory request cannot be null.");
        // 先建造消息体。
        FlatBufferBuilder bodyBuilder = new FlatBufferBuilder();

        Optional<SharedMemoryInternal> memory = this.getMemory(request.userKey());
        SharedMemoryKey key = memory.map(SharedMemoryInternal::sharedMemoryKey)
                .orElse(new SharedMemoryKey(request.userKey()));
        int objectKeyOffset = bodyBuilder.createString(getUserKeyIfIdAbsent(key));
        ReleaseMemoryMessage.startReleaseMemoryMessage(bodyBuilder);
        ReleaseMemoryMessage.addMemoryKey(bodyBuilder, key.memoryId());
        ReleaseMemoryMessage.addObjectKey(bodyBuilder, objectKeyOffset);
        int messageOffset = ReleaseMemoryMessage.endReleaseMemoryMessage(bodyBuilder);
        bodyBuilder.finish(messageOffset);
        ByteBuffer messageBodyBuffer = bodyBuilder.dataBuffer();

        // 建造消息头。
        long seq = seqGenerator.getNextNumber();
        ByteBuffer messageHeaderBuffer = DataBusUtils.buildMessageHeader(MessageType.ReleaseMemory,
                messageBodyBuffer.remaining(), seq);
        try {
            // 发出信息后即刻返回，释放内存没有回复信息。
            this.socketChannel.write(new ByteBuffer[]{messageHeaderBuffer, messageBodyBuffer});
        } catch (IOException e) {
            logger.error("[releaseSharedMemory] socket channel exception. [e={}, seq={}]", e.toString(), seq);
        } finally {
            // 客户端不再持有此内存块。
            this.memoryPool.remove(request.userKey());
        }
    }

    private String getUserKeyIfIdAbsent(SharedMemoryKey sharedMemoryKey) {
        return sharedMemoryKey.memoryId() == -1 ? sharedMemoryKey.userKey() : StringUtils.EMPTY;
    }

    private byte[] getUserData(ByteBuffer buffer) {
        if (buffer != null) {
            byte[] resData = new byte[buffer.remaining()];
            buffer.get(resData);
            return resData;
        }
        return new byte[0];
    }

    /**
     * 根据传入的 {@link GetMetaDataRequest} 查询内存元数据。
     *
     * @param request 传入的 {@link GetMetaDataRequest}。
     * @return 表示元数据读取结果的 {@link GetMetaDataResult}。
     */
    public GetMetaDataResult getMemoryMetaData(@Nonnull GetMetaDataRequest request) {
        Validation.notNull(request, "The get memory metadata request cannot be null.");
        // 先建造消息体。
        FlatBufferBuilder bodyBuilder = new FlatBufferBuilder();
        int objectKeyOffset = bodyBuilder.createString(request.userKey());
        GetMetaDataMessage.startGetMetaDataMessage(bodyBuilder);
        GetMetaDataMessage.addObjectKey(bodyBuilder, objectKeyOffset);
        int messageOffset = GetMetaDataMessage.endGetMetaDataMessage(bodyBuilder);
        bodyBuilder.finish(messageOffset);
        ByteBuffer messageBodyBuffer = bodyBuilder.dataBuffer();

        // 建造消息头。
        long seq = seqGenerator.getNextNumber();
        ByteBuffer messageHeaderBuffer = DataBusUtils.buildMessageHeader(MessageType.GetMetaData,
                messageBodyBuffer.remaining(), seq);

        try {
            ByteBuffer resBuf = getReply(seq, messageHeaderBuffer, messageBodyBuffer);
            if (resBuf == null) {
                logger.error("[applyPermission] Apply memory timeout. [seq={}]", seq);
                return GetMetaDataResult.failure(ErrorType.Timeout);
            }

            GetMetaDataMessageResponse response =
                    GetMetaDataMessageResponse.getRootAsGetMetaDataMessageResponse(resBuf);
            if (response.errorType() == ErrorType.None) {
                byte[] resData = this.getUserData(response.userDataAsByteBuffer());
                return GetMetaDataResult.success(resData, response.memorySize());
            }
            return GetMetaDataResult.failure(response.errorType());
        } catch (IOException | InterruptedException e) {
            logger.error("[getMemoryMetaData] unexpected exception.", e);
            return GetMetaDataResult.failure(ErrorType.UnknownError, e);
        }
    }

    private ByteBuffer getReply(long seq, ByteBuffer messageHeaderBuffer, ByteBuffer messageBodyBuffer)
            throws IOException, InterruptedException {
        this.replyQueues.put(seq, new ArrayBlockingQueue<>(1));
        this.socketChannel.write(new ByteBuffer[]{messageHeaderBuffer, messageBodyBuffer});
        // 阻塞等待回复并清理
        ByteBuffer resBuf = this.replyQueues.get(seq).poll(Constant.DEFAULT_WAITING_TIME_SECOND, TimeUnit.SECONDS);
        this.replyQueues.remove(seq);

        return resBuf;
    }
}
