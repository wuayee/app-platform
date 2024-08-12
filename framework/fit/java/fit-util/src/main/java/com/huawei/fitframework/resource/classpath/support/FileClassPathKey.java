/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fitframework.resource.classpath.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.io.virtualization.VirtualDirectory;
import com.huawei.fitframework.resource.classpath.ClassPath;
import com.huawei.fitframework.resource.classpath.ClassPathKey;
import com.huawei.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Objects;

/**
 * {@link DefaultClassPath} 提供 {@code file} {@link URI#getScheme() scheme} 的实现。
 *
 * @author 梁济时
 * @since 2022-07-27
 */
public final class FileClassPathKey implements ClassPathKey {
    private final File file;

    /**
     * 使用文件初始化 {@link FileClassPathKey} 类的新实例。
     *
     * @param file 表示文件的 {@link File}。
     * @throws IllegalArgumentException {@code file} 为 {@code null}，或无法被标准化。
     */
    public FileClassPathKey(File file) {
        try {
            this.file = notNull(file, "The file of classpath key cannot be null.").getCanonicalFile();
        } catch (IOException ex) {
            throw new IllegalArgumentException(StringUtils.format(
                    "The file of classpath key is not canonical. [file={0}]",
                    file.getPath()));
        }
    }

    /**
     * 获取类路径的文件。
     *
     * @return 表示文件的 {@link File}。
     */
    public File file() {
        return this.file;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {FileClassPathKey.class, this.file});
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof FileClassPathKey) {
            FileClassPathKey another = (FileClassPathKey) obj;
            return Objects.equals(this.file, another.file);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.file.getPath();
    }

    @Override
    public ClassPath create() throws IOException {
        VirtualDirectory directory;
        if (file.isDirectory()) {
            directory = VirtualDirectory.of(file);
        } else {
            throw new UnsupportedOperationException("Does not support zip directory");
        }
        return new DefaultClassPath(this, directory);
    }
}
