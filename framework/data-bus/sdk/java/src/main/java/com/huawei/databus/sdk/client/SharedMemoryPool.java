/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.client;

import static com.huawei.databus.sdk.support.SharedMemoryResult.failure;
import static com.huawei.databus.sdk.support.SharedMemoryResult.success;

import com.huawei.databus.sdk.memory.SharedMemory;
import com.huawei.databus.sdk.memory.SharedMemoryInternal;
import com.huawei.databus.sdk.memory.SharedMemoryKey;
import com.huawei.databus.sdk.message.ApplyMemoryMessage;
import com.huawei.databus.sdk.message.ApplyMemoryMessageResponse;
import com.huawei.databus.sdk.message.ApplyPermissionMessage;
import com.huawei.databus.sdk.message.ApplyPermissionMessageResponse;
import com.huawei.databus.sdk.message.ErrorType;
import com.huawei.databus.sdk.message.MessageType;
import com.huawei.databus.sdk.message.PermissionType;
import com.huawei.databus.sdk.message.ReleaseMemoryMessage;
import com.huawei.databus.sdk.message.ReleasePermissionMessage;
import com.huawei.databus.sdk.support.MemoryIoRequest;
import com.huawei.databus.sdk.support.ReleaseMemoryRequest;
import com.huawei.databus.sdk.support.SharedMemoryRequest;
import com.huawei.databus.sdk.support.SharedMemoryResult;
import com.huawei.databus.sdk.tools.DataBusUtils;
import com.huawei.fitframework.util.StringUtils;

import com.google.flatbuffers.FlatBufferBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * 内存池，管理当前所有内存块的生命周期，包括内存申请、许可管理和释放
 *
 * @author 王成 w00863339
 * @since 2024/3/25
 */
class SharedMemoryPool {
    private final Map<String, SharedMemoryInternal> memoryPool;
    private final Set<String> selfAppliedMemory;
    private final Map<Byte, BlockingQueue<ByteBuffer>> replyQueues;
    private final SocketChannel socketChannel;

    public SharedMemoryPool(Map<Byte, BlockingQueue<ByteBuffer>> replyQueues, SocketChannel socketChannel) {
        this.memoryPool = new HashMap<>();
        this.selfAppliedMemory = new HashSet<>();
        this.replyQueues = replyQueues;
        this.socketChannel = socketChannel;
    }

    /**
     * 根据传入的 {@link MemoryIoRequest} 申请新的共享内存
     *
     * @param request 传入的 {@link SharedMemoryRequest}
     * @return 表示共享内存结果的 {@link SharedMemoryResult}
     */
    public SharedMemoryResult applySharedMemory(SharedMemoryRequest request) {
        // 先建造消息体
        FlatBufferBuilder bodyBuilder = new FlatBufferBuilder();
        int objectKeyOffset = bodyBuilder.createString(request.getUserKey());
        ApplyMemoryMessage.startApplyMemoryMessage(bodyBuilder);
        ApplyMemoryMessage.addObjectKey(bodyBuilder, objectKeyOffset);
        ApplyMemoryMessage.addMemorySize(bodyBuilder, request.size());
        int messageOffset = ApplyMemoryMessage.endApplyMemoryMessage(bodyBuilder);
        bodyBuilder.finish(messageOffset);
        ByteBuffer messageBodyBuffer = bodyBuilder.dataBuffer();

        // 建造消息头
        ByteBuffer messageHeaderBuffer = DataBusUtils.buildMessageHeader(MessageType.ApplyMemory,
                messageBodyBuffer.remaining());

        try {
            this.socketChannel.write(new ByteBuffer[]{messageHeaderBuffer, messageBodyBuffer});
            // 阻塞等待回复
            ByteBuffer resBuffer = this.replyQueues.get(MessageType.ApplyMemory).take();
            ApplyMemoryMessageResponse response =
                    ApplyMemoryMessageResponse.getRootAsApplyMemoryMessageResponse(resBuffer);
            if (response.errorType() == ErrorType.None) {
                SharedMemory sharedMemory = this.addNewMemory(response.memoryKey(), request.getUserKey(),
                        PermissionType.None, response.memorySize());
                return SharedMemoryResult.success(sharedMemory);
            }
            return SharedMemoryResult.failure(response.errorType());
        } catch (IOException | InterruptedException e) {
            return SharedMemoryResult.failure(ErrorType.UnknownError, e);
        }
    }

    /**
     * 通过共享内存句柄获取内存信息。如果共享内存不存在，则根据 key 生成新的共享内存
     *
     * @param key 表示内存句柄的 {@link SharedMemoryKey}
     * @return 表示内存的 {@link SharedMemoryInternal}
     */
    public SharedMemoryInternal getOrAddMemory(String key) {
        SharedMemoryInternal internal = new SharedMemoryInternal(new SharedMemoryKey(key), PermissionType.None, 0);
        SharedMemoryInternal memory = this.memoryPool.putIfAbsent(key, internal);
        return memory == null ? internal : memory;
    }

    /**
     * 通过用户指定 key 获取内存信息
     *
     * @param key 表示内存句柄的 {@link SharedMemoryKey}
     * @return 表示内存的 {@link SharedMemoryInternal}
     */
    public Optional<SharedMemoryInternal> getMemory(String key) {
        return Optional.ofNullable(this.memoryPool.get(key));
    }

    /**
     * 将客户端申请到的内存加入内存池，并返回其只读视图
     *
     * @param memoryId 表示内存系统级句柄的 {@code int}
     * @param userKey 表示用户自定义 key 的 {@code Optional<String>}
     * @param permission 表示内存权限的 {@code byte}
     * @param size 表示内存容量的 {@code long}
     * @return 表示内存的 {@link SharedMemoryInternal}
     */
    private SharedMemoryInternal addNewMemory(int memoryId, String userKey, byte permission, long size) {
        SharedMemoryInternal internal = new SharedMemoryInternal(
                new SharedMemoryKey(memoryId, userKey), permission, size);
        this.memoryPool.put(userKey, internal);
        return internal;
    }

    /**
     * 根据传入的 {@link MemoryIoRequest} 申请读写权限
     *
     * @param request 客户端传入 {@link MemoryIoRequest}
     * @param memory 需要申请权限的内存 {@link SharedMemoryInternal}
     * @return 表示权限申请结果的 {@link SharedMemoryResult}
     */
    public SharedMemoryResult applyPermission(MemoryIoRequest request, SharedMemoryInternal memory) {
        FlatBufferBuilder bodyBuilder = new FlatBufferBuilder();
        int objectKeyOffset = bodyBuilder.createString(getUserKeyIfIdAbsent(memory.sharedMemoryKey()));
        ApplyPermissionMessage.startApplyPermissionMessage(bodyBuilder);
        ApplyPermissionMessage.addPermission(bodyBuilder, request.permissionType());
        ApplyPermissionMessage.addObjectKey(bodyBuilder, objectKeyOffset);
        ApplyPermissionMessage.addMemoryKey(bodyBuilder, memory.sharedMemoryKey().memoryId());
        int messageOffset = ApplyPermissionMessage.endApplyPermissionMessage(bodyBuilder);
        bodyBuilder.finish(messageOffset);
        ByteBuffer messageBodyBuffer = bodyBuilder.dataBuffer();

        // 建造消息头
        ByteBuffer messageHeaderBuffer = DataBusUtils.buildMessageHeader(MessageType.ApplyPermission,
                messageBodyBuffer.remaining());
        try {
            this.socketChannel.write(new ByteBuffer[]{messageHeaderBuffer, messageBodyBuffer});
            // 阻塞等待回复
            ApplyPermissionMessageResponse response =
                    ApplyPermissionMessageResponse.getRootAsApplyPermissionMessageResponse(
                            this.replyQueues.get(MessageType.ApplyPermission).take());

            // 修改本地内存信息
            if (response.errorType() == ErrorType.None) {
                memory.setPermission(request.permissionType()).setSize(response.memorySize())
                        .setMemoryId(response.memoryKey());
                return success(memory.getView());
            }
            return failure(response.errorType());
        } catch (IOException | InterruptedException e) {
            return SharedMemoryResult.failure(ErrorType.UnknownError, e);
        }
    }

    /**
     * 根据传入的 {@link MemoryIoRequest} 释放读写许可
     *
     * @param request 传入的 {@link MemoryIoRequest}
     * @param memory 需要申请权限的内存 {@link SharedMemoryInternal}
     */
    public void releasePermission(MemoryIoRequest request, SharedMemoryInternal memory) {
        FlatBufferBuilder bodyBuilder = new FlatBufferBuilder();
        int objectKeyOffset = bodyBuilder.createString(getUserKeyIfIdAbsent(memory.sharedMemoryKey()));
        ReleasePermissionMessage.startReleasePermissionMessage(bodyBuilder);
        ReleasePermissionMessage.addPermission(bodyBuilder, request.permissionType());
        ReleasePermissionMessage.addObjectKey(bodyBuilder, objectKeyOffset);
        ReleasePermissionMessage.addMemoryKey(bodyBuilder, memory.sharedMemoryKey().memoryId());
        int messageOffset = ReleasePermissionMessage.endReleasePermissionMessage(bodyBuilder);
        bodyBuilder.finish(messageOffset);
        ByteBuffer messageBodyBuffer = bodyBuilder.dataBuffer();

        // 建造消息头
        ByteBuffer messageHeaderBuffer = DataBusUtils.buildMessageHeader(MessageType.ReleasePermission,
                messageBodyBuffer.remaining());
        try {
            // 发出信息后即刻返回，释放许可总是被 DataBus 主服务批准而且没有回复信息
            this.socketChannel.write(new ByteBuffer[]{messageHeaderBuffer, messageBodyBuffer});
        } catch (IOException ignored) {
            // 需要打日志但是无需返回错误
        } finally {
            // 客户端不再持有此内存块的读写许可
            memory.setPermission(PermissionType.None);
        }
    }

    /**
     * 根据传入的 {@link ReleaseMemoryRequest} 释放内存
     *
     * @param request 传入的 {@link ReleaseMemoryRequest}
     */
    public void releaseSharedMemory(ReleaseMemoryRequest request) {
        // 先建造消息体
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

        // 建造消息头
        ByteBuffer messageHeaderBuffer = DataBusUtils.buildMessageHeader(MessageType.ReleaseMemory,
                messageBodyBuffer.remaining());
        try {
            // 发出信息后即刻返回，释放内存没有回复信息
            this.socketChannel.write(new ByteBuffer[]{messageHeaderBuffer, messageBodyBuffer});
        } catch (IOException ignored) {
            // 需要打日志但是无需返回错误
        } finally {
            // 客户端不再持有此内存块
            this.memoryPool.remove(request.userKey());
        }
    }

    private String getUserKeyIfIdAbsent(SharedMemoryKey sharedMemoryKey) {
        return sharedMemoryKey.memoryId() == -1 ? sharedMemoryKey.userKey() : StringUtils.EMPTY;
    }
}
