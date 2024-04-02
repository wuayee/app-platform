/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.tools;

import com.huawei.databus.sdk.api.DataBusIoRequest;
import com.huawei.databus.sdk.message.ApplyMemoryMessage;
import com.huawei.databus.sdk.message.MessageHeader;
import com.huawei.databus.sdk.message.PermissionType;
import com.huawei.databus.sdk.support.SharedMemoryRequest;
import com.huawei.fitframework.inspection.Validation;

import com.google.flatbuffers.FlatBufferBuilder;

import java.nio.ByteBuffer;

/**
 * DataBus 工具类
 *
 * @author 王成 w00863339
 * @since 2024/3/25
 */
public class DataBusUtils {
    /**
     * 根据消息体类型和长度生成消息头
     *
     * @param type 代表消息类型的 {@code byte}
     * @param size 代表消息体长度的 {@code size}
     * @return 内含消息体的 {@link ByteBuffer}
     */
    public static ByteBuffer buildMessageHeader(byte type, int size) {
        FlatBufferBuilder headerBuilder = new FlatBufferBuilder(Constant.DATABUS_SERVICE_HEADER_SIZE);
        MessageHeader.startMessageHeader(headerBuilder);
        MessageHeader.addType(headerBuilder, type);
        MessageHeader.addSize(headerBuilder, size);
        int messageOffset = ApplyMemoryMessage.endApplyMemoryMessage(headerBuilder);
        headerBuilder.finish(messageOffset);
        ByteBuffer messageHeaderBuffer = headerBuilder.dataBuffer();
        Validation.equals(messageHeaderBuffer.remaining(),
                Constant.DATABUS_SERVICE_HEADER_SIZE,
                () -> new IllegalStateException("Message header size MUST match the preconfigured value."));
        return messageHeaderBuffer;
    }

    /**
     * 比较两个权限的大小。0：None，1：Read，2：write。目前可以通过简单的相减来判断权限大小
     * 引入新的权限是，必须修改此函数
     *
     * @param perm1 表示权限1的 {@code byte}
     * @param perm2 表示权限2的 {@code byte}
     * @return 表示比较结果的 {code int}
     */
    public static int comparePermission(byte perm1, byte perm2) {
        return perm1 - perm2;
    }

    /**
     * 验证 IO 请求的参数是否合法
     *
     * @param request 表示 IO 请求的 {@link DataBusIoRequest}
     * @param permissionType 表示 IO 请求的权限 {@code byte}
     */
    public static void verifyIoRequest(DataBusIoRequest request, byte permissionType) {
        Validation.notNull(request, () -> new IllegalArgumentException("Request cannot be null."));
        Validation.notNull(request.sharedMemoryKey(),
                () -> new IllegalArgumentException("Shared memory key cannot be null."));
        Validation.equals(request.permissionType(), permissionType,
                () -> new IllegalArgumentException("Permission type mismatches with API."));
        if (permissionType == PermissionType.Write) {
            Validation.notNull(request.bytes(), () -> new IllegalArgumentException("Byte array cannot be null."));
            Validation.greaterThan(request.bytes().length, 0,
                    () -> new IllegalArgumentException("Byte array length cannot be 0."));
            Validation.greaterThanOrEquals(request.bytes().length, request.dataLength(),
                    () -> new IllegalArgumentException("The buffer length should not be less than data length."));
        }
        Validation.greaterThan(request.dataLength(), 0,
                () -> new IllegalArgumentException("data length must be positive."));
        Validation.greaterThanOrEquals(request.memoryOffset(), 0,
                () -> new IllegalArgumentException("Memory offset must be 0 or positive."));
        Validation.between((int) request.permissionType(), 0, PermissionType.names.length - 1,
                () -> new IllegalArgumentException("Illegal permission type."));
    }


    /**
     * 验证内存请求的参数是否合法
     *
     * @param request 表示 IO 请求的 {@link SharedMemoryRequest}
     */
    public static void verifyMemoryRequest(SharedMemoryRequest request) {
        Validation.notNull(request, () -> new IllegalArgumentException("Request cannot be null."));
        Validation.greaterThan(request.size(), 0,
                () -> new IllegalArgumentException("memory length must be positive."));
    }
}
