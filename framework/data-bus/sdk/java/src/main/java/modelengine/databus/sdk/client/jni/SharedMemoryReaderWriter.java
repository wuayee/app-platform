/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.databus.sdk.client.jni;

import modelengine.databus.sdk.tools.DataBusUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * 共享内存 JNI 接口定义
 *
 * @author 李哲峰
 * @since 2024-03-19
 */
public class SharedMemoryReaderWriter {
    private static final Logger logger = LogManager.getLogger(SharedMemoryReaderWriter.class);

    /**
     *  x86 动态链接库名称
     */
    private static final String DATABUS_JNI_NATIVE_LIBRARY_NAME_X86 = "libdatabus_x86";

    /**
     *  aarch64 动态链接库名称
     */
    private static final String DATABUS_JNI_NATIVE_LIBRARY_NAME_AARCH64 = "libdatabus_aarch64";

    static {
        if (DataBusUtils.isSupportedPlatform()) {
            try {
                if (DataBusUtils.isX86Supported()) {
                    NativeLibraryLoader.loadLibrary(DATABUS_JNI_NATIVE_LIBRARY_NAME_X86);
                } else {
                    NativeLibraryLoader.loadLibrary(DATABUS_JNI_NATIVE_LIBRARY_NAME_AARCH64);
                }
            } catch (IOException e) {
                logger.error("[init] Loading native library failed.", e);
            }
        }
    }

    /**
     * JNI read 接口
     *
     * @param sharedMemoryId 表示目标内存块的句柄
     * @param readOffset 表示从目标内存块读入地点的偏移量的 {@code long}。
     * @param readLength 表示待读入数据的数量的 {@code long}。
     * @return 从目标内存块读出的字节数据 {@code byte[]}
     * @throws IOException 当发生 I/O 异常时。
     */
    public native byte[] read(int sharedMemoryId, long readOffset, long readLength) throws IOException;

    /**
     * JNI write 接口
     *
     * @param sharedMemoryId 表示目标内存块的句柄
     * @param writeOffset 表示从目标内存块写入地点的偏移量的 {@code long}。
     * @param writeLength 表示待写入数据的数量的 {@code long}。
     * @param bytes 表示待写入数据所在数组的 {@code byte[]}。
     * @return 表示写入字节总数的 {@code long}。
     * @throws IOException 当发生 I/O 异常时。
     */
    public native long write(int sharedMemoryId, long writeOffset, long writeLength, byte[] bytes) throws IOException;
}

