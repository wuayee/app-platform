/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.server.netty.support;

import com.huawei.fit.http.server.netty.NettyReadableMessageBody;
import com.huawei.fitframework.inspection.Nonnull;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;

/**
 * {@link com.huawei.fit.http.protocol.ReadableMessageBody} 的 {@link CompositeByteBuf} 的实现。
 *
 * @author 王成 w00863339
 * @since 2024-02-17
 */
public class CompositeByteBufReadableMessageBody extends NettyReadableMessageBody {
    private final CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();

    @Override
    public int read0() throws IOException {
        if (this.compositeByteBuf.readableBytes() == 0) {
            return -1;
        }
        return this.compositeByteBuf.readByte() & 0xFF;
    }

    @Override
    public int read0(@Nonnull byte[] bytes, int off, int len) {
        if (this.compositeByteBuf.readableBytes() == 0 || len == 0) {
            return 0;
        }
        int toRead = Math.min(len, compositeByteBuf.readableBytes());
        this.compositeByteBuf.readBytes(bytes, off, toRead);
        return toRead;
    }

    @Override
    public int available() {
        if (this.compositeByteBuf.refCnt() > 0) {
            return this.compositeByteBuf.readableBytes();
        }
        return 0;
    }

    @Override
    protected int write0(@Nonnull ByteBuf data, boolean isLast) {
        this.compositeByteBuf.addComponent(true, data.retain());
        return data.readableBytes();
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (this.compositeByteBuf.refCnt() > 0) {
            this.compositeByteBuf.release();
        }
    }
}