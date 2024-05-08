/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fitframework.io.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.io.RandomAccessor;
import com.huawei.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

/**
 * 为 {@link RandomAccessor} 提供基于文件的实现。
 *
 * @author 梁济时 l00815032
 * @since 2022-07-25
 */
public class FileRandomAccessor implements RandomAccessor {
    private final File file;
    private final RandomAccessFile access;

    /**
     * 使用待访问的文件初始化 {@link FileRandomAccessor} 类的新实例。
     *
     * @param file 表示待访问的文件的 {@link File}。
     * @throws IllegalArgumentException 当 {@code file} 为 {@code null} 时。
     * @throws IOException 当 {@code file} 不存在或不标准时。
     */
    public FileRandomAccessor(File file) throws IOException {
        this.file = notNull(file, "The file to access cannot be null.").getCanonicalFile();
        this.access = new RandomAccessFile(file, "r");
    }

    @Override
    public byte[] read(long offset, int length) throws IOException {
        if (offset < 0 || offset >= this.size()) {
            throw new IllegalArgumentException(StringUtils.format(
                    "The offset of data to access is out of bound. [offset={0}, total={1}]",
                    offset,
                    this.size()));
        } else if (length < 0 || length > this.size() - offset) {
            throw new IllegalArgumentException(StringUtils.format(
                    "The length of data to access is out of bounds. [length={0}, offset={1}, total={2}]",
                    length,
                    offset,
                    this.size()));
        } else {
            this.access.seek(offset);
            byte[] buffer = new byte[length];
            int read = 0;
            while (read < length) {
                read += this.access.read(buffer, read, length - read);
            }
            return buffer;
        }
    }

    @Override
    public long size() {
        return this.file.length();
    }

    @Override
    public void close() throws IOException {
        this.access.close();
    }

    @Override
    public int hashCode() {
        return this.file.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof FileRandomAccessor) {
            FileRandomAccessor another = (FileRandomAccessor) obj;
            return Objects.equals(this.file, another.file);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.file.getPath();
    }
}
