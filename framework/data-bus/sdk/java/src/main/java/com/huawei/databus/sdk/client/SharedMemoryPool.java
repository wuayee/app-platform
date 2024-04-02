/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.client;

import static com.huawei.databus.sdk.support.SharedMemoryResult.failure;
import static com.huawei.databus.sdk.support.SharedMemoryResult.success;

import com.huawei.databus.sdk.memory.SharedMemory;
import com.huawei.databus.sdk.memory.SharedMemoryInternal;
import com.huawei.databus.sdk.memory.SharedMemoryKey;
import com.huawei.databus.sdk.message.ApplyPermissionMessage;
import com.huawei.databus.sdk.message.ApplyPermissionMessageResponse;
import com.huawei.databus.sdk.message.ErrorType;
import com.huawei.databus.sdk.message.MessageType;
import com.huawei.databus.sdk.support.MemoryIoRequest;
import com.huawei.databus.sdk.support.SharedMemoryResult;
import com.huawei.databus.sdk.tools.DataBusUtils;

import com.google.flatbuffers.FlatBufferBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * 内存池，管理当前所有内存块的生命周期，包括内存申请、许可管理和释放
 *
 * @author 王成 w00863339
 * @since 2024/3/25
 */
class SharedMemoryPool {
    private final Map<SharedMemoryKey, SharedMemoryInternal> memoryPool;
    private final Map<Byte, BlockingQueue<ByteBuffer>> replyQueues;
    private final SocketChannel socketChannel;

    public SharedMemoryPool(Map<Byte, BlockingQueue<ByteBuffer>> replyQueues, SocketChannel socketChannel) {
        this.memoryPool = new HashMap<>();
        this.replyQueues = replyQueues;
        this.socketChannel = socketChannel;
    }

    /**
     * 将申请到的内存加入内存池，并返回其只读视图
     *
     * @param key 表示内存句柄的 {@code int}
     * @param permission 表示内存权限的 {@code byte}
     * @param size 表示内存容量的 {@code long}
     * @return 表示内存只读视图的 {@link SharedMemory}
     */
    public SharedMemory addNewMemory(int key, byte permission, long size) {
        return this.addNewMemory(new SharedMemoryKey(key), permission, size);
    }

    /**
     * 将申请到的内存加入内存池，并返回只读视图
     *
     * @param key 表示内存句柄的 {@link SharedMemoryKey}
     * @param permission 表示内存权限的 {@code byte}
     * @param size 表示内存容量的 {@code long}
     * @return 表示内存只读视图的 {@link SharedMemory}
     */
    public SharedMemory addNewMemory(SharedMemoryKey key, byte permission, long size) {
        SharedMemoryInternal internal = new SharedMemoryInternal(key, permission, size);
        this.memoryPool.put(key, internal);
        return internal.getView();
    }

    /**
     * 根据传入的 {@link MemoryIoRequest} 申请读写权限
     *
     * @param request 传入的 {@link MemoryIoRequest}
     * @return 表示权限申请结果的 {@link SharedMemoryResult}
     */
    public SharedMemoryResult applyPermission(MemoryIoRequest request) {
        // 如果当前已获取更高的权限，则直接返回成功，无需申请
        SharedMemoryInternal internal = memoryPool.get(request.sharedMemoryKey());
        if (internal != null && DataBusUtils.comparePermission(internal.permission(), request.permissionType()) >= 0) {
            return SharedMemoryResult.success(internal.getView());
        }

        // 需要申请权限
        FlatBufferBuilder bodyBuilder = new FlatBufferBuilder();
        ApplyPermissionMessage.startApplyPermissionMessage(bodyBuilder);
        ApplyPermissionMessage.addPermission(bodyBuilder, request.permissionType());
        ApplyPermissionMessage.addMemoryKey(bodyBuilder, request.sharedMemoryKey().getMemoryId());
        int messageOffset = ApplyPermissionMessage.endApplyPermissionMessage(bodyBuilder);
        bodyBuilder.finish(messageOffset);
        ByteBuffer messageBodyBuffer = bodyBuilder.dataBuffer();

        // 建造消息头
        ByteBuffer messageHeaderBuffer = DataBusUtils.buildMessageHeader(MessageType.ApplyPermission,
                messageBodyBuffer.remaining());
        try {
            socketChannel.write(new ByteBuffer[]{messageHeaderBuffer, messageBodyBuffer});
            // 阻塞等待回复
            ApplyPermissionMessageResponse response =
                    ApplyPermissionMessageResponse.getRootAsApplyPermissionMessageResponse(
                            this.replyQueues.get(MessageType.ApplyPermission).take());
            if (response.errorType() == ErrorType.None) {
                SharedMemory sharedMemory =
                        this.addNewMemory(request.sharedMemoryKey(), request.permissionType(), response.memorySize());
                return success(sharedMemory);
            }
            return failure(response.errorType());
        } catch (IOException | InterruptedException e) {
            // 错误码
            return failure(ErrorType.PermissionDenied);
        }
    }
}
