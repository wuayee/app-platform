/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.protocol;

import static com.huawei.fitframework.inspection.Validation.notNull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 表示服务端的 Http 响应。
 *
 * @author 季聿阶
 * @since 2022-07-05
 */
public interface ServerResponse
        extends Message<ConfigurableStatusLine, ConfigurableMessageHeaders, WritableMessageBody> {
    /**
     * 向 Http 消息起始行和消息头中写入数据。
     *
     * @throws IOException 当发生 I/O 异常时。
     */
    void writeStartLineAndHeaders() throws IOException;

    /**
     * 向 Http 消息体中写入数据。
     *
     * @param b 表示待写入数据的 {@code int}。
     * <p>实际上，这里的数据 {@code b} 一定是一个 {@code byte}。</p>
     * @throws IOException 当发生 I/O 异常时。
     */
    void writeBody(int b) throws IOException;

    /**
     * 向 Http 消息体中写入数据。
     *
     * @param bytes 表示待写入数据的 {@code byte[]}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws IllegalArgumentException 当 {@code bytes} 为 {@code null} 时。
     * @see #writeBody(byte[], int, int)
     */
    default void writeBody(byte[] bytes) throws IOException {
        this.writeBody(notNull(bytes, "The bytes to write cannot be null."), 0, bytes.length);
    }

    /**
     * 向 Http 消息体中写入数据。
     *
     * @param bytes 表示待写入数据所在数组的 {@code byte[]}。
     * @param off 表示待写入数据的偏移量的 {@code int}。
     * @param len 表示待写入数据的数量的 {@code int}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws IllegalArgumentException 当 {@code bytes} 为 {@code null} 时。
     * @throws IndexOutOfBoundsException 当 {@code off} 或 {@code len} 为负数时，或 {@code off + len}
     * 超过了 {@code bytes} 的长度时。
     */
    void writeBody(byte[] bytes, int off, int len) throws IOException;

    /**
     * 强制已经写入的数据执行写出，也就是说将之前写入到缓冲区的数据全部对外输出。
     *
     * @throws IOException 当发生 I/O 异常时。
     */
    void flush() throws IOException;

    /**
     * 获取消息体的输出流。
     *
     * @return 表示消息体的输出流的 {@link OutputStream}。
     */
    OutputStream getBodyOutputStream();
}
