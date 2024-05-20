/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.client.jni;

import com.huawei.databus.sdk.tools.DataBusUtils;

import java.io.IOException;

/**
 * 共享内存JNI接口定义
 *
 * @author l00862071
 * @since 2024-03-19
 */
public class SharedMemoryReaderWriter {
    /**
     *  动态链接库名称
     */
    public static final String NATIVE_LIBRARY_NAME = "native";

    static {
        if (DataBusUtils.isSupportedPlatform()) {
            System.loadLibrary(NATIVE_LIBRARY_NAME);
        }
    }

    /**
     * JNI read接口
     *
     * @param sharedMemoryId 表示目标内存块的句柄
     * @param readOffset 表示从目标内存块读入地点的偏移量的 {@code long}。
     * @param readLength 表示待读入数据的数量的 {@code long}。
     * @return 从目标内存块读出的字节数据 {@code byte[]}
     * @throws IOException 当发生 I/O 异常时。
     */
    public native byte[] read(int sharedMemoryId, long readOffset, long readLength) throws IOException;

    /**
     * JNI write接口
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

