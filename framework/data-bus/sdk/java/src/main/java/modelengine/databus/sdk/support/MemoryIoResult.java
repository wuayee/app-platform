/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.databus.sdk.support;

import modelengine.databus.sdk.api.DataBusIoResult;
import modelengine.databus.sdk.memory.SharedMemory;
import modelengine.databus.sdk.memory.SharedMemoryKey;
import modelengine.databus.sdk.message.ErrorType;
import modelengine.databus.sdk.message.PermissionType;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Optional;

/**
 * 为内存申请提供结果。
 *
 * @author 王成
 * @since 2024-03-17
 */
public interface MemoryIoResult extends DataBusIoResult {
    /**
     * 返回与本次 IO 相关的字节数组
     *
     * @return 表示与本次 IO 请求相关的 {@code byte[]}
     */
    byte[] bytes();

    /**
     * 如果本次为读取，且用户设置了同时操作返回用户数据，则返回用户元数据。如果此内存块未设置用户元数据，返回0长度字节数组。
     * 其他情况，返回 null。
     *
     * @return 表示与本次 IO 请求相关的用户元数据 {@code byte[]}
     */
    byte[] userData();

    /**
     * 返回本次 IO 请求相关的许可类型
     *
     * @return 表示许可类型的 {@code byte}
     */
    byte permissionType();

    /**
     * 生成一个表示成功的结果。
     *
     * @param sharedMemory 表示此 IO 操作涉及到的内存实例的 {@link SharedMemoryKey}。
     * @param bytes 表示此 IO 操作涉及到字节缓冲区的 {@code byte[]}。
     * @param userData 表示此 IO 操作附带的用户元数据的 {@code byte[]}。
     * @param permissionType 表示此 IO 操作涉及到权限的 {@code byte}。
     * @return 表示内存 IO 操作成功的结果的 {@link MemoryIoResult}。
     */
    static MemoryIoResult success(SharedMemory sharedMemory, byte[] bytes, byte[] userData, byte permissionType) {
        return new SuccessResult(sharedMemory, bytes, userData, permissionType);
    }

    /**
     * 获取表示内存 IO 失败的结果。
     *
     * @param errorType 表示申请内存得到的错误码 {@code byte}。
     * @param throwable 表示 Java 原生异常的 {@link Throwable}
     * @return 表示内存 IO 失败的结果的 {@link MemoryIoResult}。
     */
    static MemoryIoResult failure(byte errorType, Throwable throwable) {
        return new FailureResult(errorType, throwable);
    }

    /**
     * 获取表示内存 IO 失败的结果。
     *
     * @param errorType 表示申请内存得到的错误码 {@code byte}。
     * @return 表示内存 IO 失败的结果的 {@link MemoryIoResult}。
     */
    static MemoryIoResult failure(byte errorType) {
        return new FailureResult(errorType, null);
    }

    /**
     * 为 {@link MemoryIoResult} 提供表示申请内存成功的实现。
     *
     * @author 王成
     * @since 2024-03-17
     */
    final class SuccessResult implements MemoryIoResult {
        private final SharedMemory sharedMemory;
        private final byte[] bytes;
        private final byte[] userData;
        private final byte permissionType;

        private SuccessResult(SharedMemory sharedMemory, byte[] bytes, byte[] userData, byte permissionType) {
            this.sharedMemory = sharedMemory;
            this.bytes = ObjectUtils.getIfNull(bytes, () -> new byte[0]);
            this.userData = userData;
            this.permissionType = permissionType;
        }

        @Override
        public byte errorType() {
            return ErrorType.None;
        }

        @Override
        public SharedMemory sharedMemory() {
            return this.sharedMemory;
        }

        @Override
        public byte[] userData() {
            return this.userData;
        }

        @Override
        public byte[] bytes() {
            return this.bytes;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public byte permissionType() {
            return this.permissionType;
        }

        @Override
        public Optional<Throwable> cause() {
            return Optional.empty();
        }

        @Override
        public String toString() {
            return "SuccessResult{sharedMemory=" + sharedMemory + ", permissionType=" + permissionType + '}';
        }
    }

    /**
     * 为 {@link MemoryIoResult} 提供表示内存 IO 失败的实现。
     *
     * @author 王成
     * @since 2024-03-17
     */
    final class FailureResult implements MemoryIoResult {
        private final byte errorType;
        private final Throwable throwable;

        private FailureResult(byte errorType, Throwable throwable) {
            this.errorType = errorType;
            this.throwable = throwable;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public SharedMemory sharedMemory() {
            return null;
        }

        @Override
        public byte[] userData() {
            return new byte[0];
        }

        @Override
        public Optional<Throwable> cause() {
            return Optional.ofNullable(throwable);
        }

        @Override
        public byte[] bytes() {
            return new byte[0];
        }

        @Override
        public byte permissionType() {
            return PermissionType.None;
        }

        @Override
        public byte errorType() {
            return errorType;
        }

        @Override
        public String toString() {
            return "FailureResult{errorType=" + errorType + ", throwable=" + throwable + '}';
        }
    }
}
