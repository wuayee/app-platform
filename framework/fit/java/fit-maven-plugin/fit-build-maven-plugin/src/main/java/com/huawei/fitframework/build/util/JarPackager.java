/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.build.util;

import com.huawei.fitframework.protocol.jar.Jar;
import com.huawei.fitframework.protocol.jar.JarEntryLocation;
import com.huawei.fitframework.util.IoUtils;
import com.huawei.fitframework.util.StringUtils;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 提供 JAR 格式的打包程序。
 *
 * @author 梁济时 l00815032
 * @since 2023-02-02
 */
public final class JarPackager {
    private static final FileTime NONE_TIME = FileTime.fromMillis(0L);

    private final ZipOutputStream out;
    private final Set<String> directories;

    public JarPackager(ZipOutputStream out) {
        this.out = out;
        this.directories = new HashSet<>();
    }

    public ZipOutputStream out() {
        return this.out;
    }

    public void ensureDirectory(String fileEntry) throws MojoExecutionException {
        String[] path = StringUtils.split(fileEntry, JarEntryLocation.ENTRY_PATH_SEPARATOR);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < path.length - 1; i++) {
            builder.append(path[i]).append(JarEntryLocation.ENTRY_PATH_SEPARATOR);
            String name = builder.toString();
            if (!this.directories.contains(name)) {
                this.directories.add(name);
                ZipEntry entry = new ZipEntry(name);
                try {
                    this.out.putNextEntry(entry);
                    this.out.closeEntry();
                } catch (IOException ex) {
                    throw new MojoExecutionException(StringUtils.format(
                            "Failed to create directory in JAR. " + "[entry={0}]", name), ex);
                }
            }
        }
    }

    public void packageJarEntry(Jar.Entry entry) throws MojoExecutionException {
        this.packageJarEntry(entry, entry.name());
    }

    public void packageJarEntry(Jar.Entry entry, String newEntryName) throws MojoExecutionException {
        if (entry.directory()) {
            return;
        }
        this.ensureDirectory(newEntryName);
        ZipEntry target = new ZipEntry(newEntryName);
        target.setSize(entry.sizeOfUncompressed());
        target.setCompressedSize(entry.sizeOfCompressed());
        target.setMethod(entry.methodOfCompression().id());
        target.setComment(entry.comment());
        target.setCrc(Integer.toUnsignedLong(entry.crc32()));
        target.setCreationTime(FileTime.fromMillis(0));
        target.setExtra(entry.extra());
        target.setLastAccessTime(FileTime.fromMillis(0));
        target.setLastModifiedTime(FileTime.fromMillis(0));
        try {
            this.out.putNextEntry(target);
            try (InputStream in = entry.read()) {
                IoUtils.copy(in, this.out);
            }
            this.out.closeEntry();
        } catch (IOException ex) {
            throw new MojoExecutionException(StringUtils.format("Failed to package entry in JAR. [entry={0}]",
                    entry.location()), ex);
        }
    }

    public void addEntry(String entryName, byte[] bytes) throws MojoExecutionException {
        this.ensureDirectory(entryName);
        ZipEntry entry = new ZipEntry(entryName);
        entry.setLastModifiedTime(NONE_TIME);
        entry.setLastAccessTime(NONE_TIME);
        entry.setCreationTime(NONE_TIME);
        entry.setTime(0L);
        try {
            this.out.putNextEntry(entry);
            this.out.write(bytes);
            this.out.closeEntry();
        } catch (IOException e) {
            throw new MojoExecutionException(StringUtils.format("Failed to add new entry to JAR. [entry={0}]",
                    entryName), e);
        }
    }

    public void store(File file, String directory) throws MojoExecutionException {
        ZipEntry entry = new ZipEntry(directory + file.getName());
        entry.setCrc(crc32(file));
        entry.setSize(file.length());
        entry.setCompressedSize(file.length());
        entry.setLastModifiedTime(NONE_TIME);
        entry.setLastAccessTime(NONE_TIME);
        entry.setCreationTime(NONE_TIME);
        entry.setMethod(ZipEntry.STORED);
        entry.setTime(0L);
        this.ensureDirectory(entry.getName());
        try {
            this.out.putNextEntry(entry);
            IoUtils.copy(file, this.out);
            this.out.closeEntry();
        } catch (IOException ex) {
            throw new MojoExecutionException(StringUtils.format("Failed to store file into JAR. [file={0}, entry={1}]",
                    file.getPath(),
                    entry.getName()), ex);
        }
    }

    private static long crc32(File file) throws MojoExecutionException {
        CRC32 crc32 = new CRC32();
        byte[] buffer = new byte[IoUtils.DEFAULT_BUFFER_SIZE];
        try (InputStream in = new FileInputStream(file)) {
            while (true) {
                int read = in.read(buffer);
                if (read < 0) {
                    break;
                } else {
                    crc32.update(buffer, 0, read);
                }
            }
        } catch (IOException ex) {
            throw new MojoExecutionException(StringUtils.format("Failed to compute CRC-32 of file. [file={0}]",
                    file.getPath()), ex);
        }
        return crc32.getValue();
    }

    public static JarPackager of(ZipOutputStream out) {
        return new JarPackager(out);
    }
}
