/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server.netty.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.server.netty.NettyReadableMessageBody;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.util.FileUtils;

import io.netty.buffer.ByteBuf;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicLong;

/**
 * {@link com.huawei.fit.http.protocol.ReadableMessageBody} 的 {@link FileChannel} 的实现。
 *
 * @author 季聿阶
 * @since 2023-09-30
 */
public class FileChannelReadableMessageBody extends NettyReadableMessageBody {
    private final File file;
    private final RandomAccessFile randomAccessFile;
    private final FileChannel channel;

    private final AtomicLong writePosition = new AtomicLong(0);
    private final AtomicLong readPosition = new AtomicLong(0);

    public FileChannelReadableMessageBody() {
        try {
            this.file = Files.createTempFile("netty-channel-", ".tmp").toFile();
            this.randomAccessFile = new RandomAccessFile(this.file, "rw");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create tmp file by channel.", e);
        }
        this.channel = this.randomAccessFile.getChannel();
    }

    public FileChannelReadableMessageBody(FileChannel channel) {
        this.file = null;
        this.randomAccessFile = null;
        this.channel = notNull(channel, "The file channel cannot be null.");
    }

    @Override
    public int read0() throws IOException {
        if (this.readPosition.get() >= this.writePosition.get()) {
            return -1;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(1);
        int read = this.channel.read(byteBuffer, this.readPosition.get());
        if (read > 0) {
            this.readPosition.addAndGet(read);
            return byteBuffer.get(0) & 0xFF;
        } else {
            return -1;
        }
    }

    @Override
    protected int read0(@Nonnull byte[] bytes, int off, int len) throws IOException {
        if (this.readPosition.get() >= this.writePosition.get()) {
            return 0;
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes, off, len);
        int bytesRead = this.channel.read(byteBuffer, this.readPosition.get());
        if (bytesRead > 0) {
            this.readPosition.addAndGet(bytesRead);
        }
        return bytesRead;
    }

    @Override
    public int available() throws IOException {
        long availableBytes = this.writePosition.get() - this.readPosition.get();
        return availableBytes > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) availableBytes;
    }

    @Override
    protected int write0(@Nonnull ByteBuf data, boolean isLast) throws IOException {
        ByteBuffer byteBuffer = data.nioBuffer();
        int count = 0;
        while (byteBuffer.hasRemaining()) {
            int writeSize = this.channel.write(byteBuffer, this.writePosition.get());
            count += writeSize;
            this.writePosition.addAndGet(writeSize);
        }
        return count;
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.getLock().lock();
        try {
            this.channel.close();
            if (this.randomAccessFile != null) {
                this.randomAccessFile.close();
            }
            FileUtils.delete(this.file);
            this.readPosition.set(0);
            this.writePosition.set(0);
        } finally {
            this.getLock().unlock();
        }
    }
}
