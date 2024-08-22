/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.tools;

import com.huawei.databus.sdk.api.DataBusIoRequest;
import com.huawei.databus.sdk.message.ApplyMemoryMessage;
import com.huawei.databus.sdk.message.MessageHeader;
import com.huawei.databus.sdk.message.PermissionType;
import com.huawei.databus.sdk.support.SharedMemoryRequest;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Validation;

import com.google.flatbuffers.FlatBufferBuilder;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * DataBus 工具类
 *
 * @author 王成
 * @since 2024/3/25
 */
public class DataBusUtils {
    private static final Set<String> SUPPORTED_PLATFORM = new HashSet<>();

    static {
        // DataBus 只能在Linux下运行
        SUPPORTED_PLATFORM.add("linux");
    }

    /**
     * 根据消息体类型和长度生成消息头
     *
     * @param type 代表消息类型的 {@code byte}
     * @param size 代表消息体长度的 {@code int}
     * @param seq 代表消息体序列号的 {@code long}
     * @return 内含消息体的 {@link ByteBuffer}
     */
    public static ByteBuffer buildMessageHeader(byte type, int size, long seq) {
        FlatBufferBuilder headerBuilder = new FlatBufferBuilder(Constant.DATABUS_SERVICE_HEADER_SIZE);
        MessageHeader.startMessageHeader(headerBuilder);
        MessageHeader.addType(headerBuilder, type);
        MessageHeader.addSize(headerBuilder, size);
        MessageHeader.addSeq(headerBuilder, seq);
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
        Validation.notNull(request.userKey(),
                () -> new IllegalArgumentException("Shared memory key cannot be null."));
        Validation.equals(request.permissionType(), permissionType,
                () -> new IllegalArgumentException("Permission type mismatches with API."));
        if (permissionType == PermissionType.Write) {
            Validation.notNull(request.bytes(), () -> new IllegalArgumentException("Byte array cannot be null."));
            Validation.greaterThanOrEquals(request.bytes().length, request.dataLength(),
                    () -> new IllegalArgumentException("The buffer length should not be less than data length."));
            if (request.isOperatingUserData()) {
                Validation.notNull(request.userData(),
                        () -> new IllegalArgumentException("User data cannot be null when specifying user operation"
                                + " in a write request."));
                Validation.lessThanOrEquals(request.userData().length, 1024,
                        () -> new IllegalArgumentException("User data size cannot be larger than 1024 bytes"));
            }
        }
        Validation.greaterThanOrEquals(request.dataLength(), 0,
                () -> new IllegalArgumentException("data length must be non-negative."));
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
        Validation.greaterThanOrEquals(request.size(), 0,
                () -> new IllegalArgumentException("memory length must be non-negative."));
        Validation.notNull(request.userKey(),
                () -> new IllegalArgumentException("Shared memory key cannot be null."));
    }

    /**
     * 验证当前平台是否支持 X86 架构
     *
     * @return 表示平台是否支持的 {code boolean}
     */
    public static boolean isX86Supported() {
        String arch = System.getProperty("os.arch").toLowerCase();
        return arch.startsWith("x86") || arch.startsWith("amd");
    }

    /**
     * 验证当前平台是否支持
     *
     * @return 表示平台是否支持的 {code boolean}
     */
    public static boolean isSupportedPlatform() {
        return SUPPORTED_PLATFORM.stream().anyMatch(p -> System.getProperty("os.name").toLowerCase().contains(p));
    }

    /**
     * 创建一个新的 ByteBuffer ，其容量为要复制的字节数，从源 ByteBuffer 复制 n 个字节到新 ByteBuffer
     *
     * @param source 表示源数据的 {@link ByteBuffer}
     * @param n 表示拷贝字节数的 {@code int}
     * @return 表示目标数据的 {@link ByteBuffer}
     */
    public static ByteBuffer copyFromByteBuffer(@Nonnull ByteBuffer source, int n) {
        if (source == null) {
            throw new IllegalArgumentException("Source ByteBuffer cannot be null");
        }
        if (n < 0 || source.remaining() < n) {
            throw new IllegalArgumentException("Invalid number of bytes to copy");
        }

        ByteBuffer destination = ByteBuffer.allocate(n);
        for (int i = 0; i < n; i++) {
            destination.put(source.get());
        }
        destination.flip();

        return destination;
    }
}
