/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.protocol.jar;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class JarBuilder implements Closeable {
    private final ZipOutputStream zip;
    private final Set<String> packages;

    private JarBuilder(ZipOutputStream zip) {
        this.zip = zip;
        this.packages = new HashSet<>();
    }

    private void ensurePackage(String packageName) throws IOException {
        if (packageName == null || this.packages.contains(packageName)) {
            return;
        }
        this.ensurePackage(parent(packageName));
        ZipEntry entry = new ZipEntry(packageName);
        this.zip.putNextEntry(entry);
        this.zip.closeEntry();
        this.packages.add(packageName);
    }

    private static String parent(String entryName) {
        int last = entryName.length() - 1;
        if (entryName.charAt(last) == JarEntryLocation.ENTRY_PATH_SEPARATOR) {
            last--;
        }
        last = entryName.lastIndexOf(JarEntryLocation.ENTRY_PATH_SEPARATOR, last);
        if (last < 0) {
            return null;
        } else {
            return entryName.substring(0, last + 1);
        }
    }

    long add(String entryName, byte[] bytes) throws IOException {
        this.ensurePackage(parent(entryName));
        ZipEntry entry = new ZipEntry(entryName);
        this.zip.putNextEntry(entry);
        this.zip.write(bytes);
        this.zip.closeEntry();
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return crc32.getValue();
    }

    public long store(String entryName, byte[] bytes) throws IOException {
        this.ensurePackage(parent(entryName));
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        ZipEntry entry = new ZipEntry(entryName);
        entry.setMethod(ZipEntry.STORED);
        entry.setCrc(crc32.getValue());
        entry.setCompressedSize(bytes.length);
        entry.setSize(bytes.length);
        this.zip.putNextEntry(entry);
        this.zip.write(bytes);
        this.zip.closeEntry();
        return crc32.getValue();
    }

    @Override
    public void close() throws IOException {
        this.zip.close();
    }

    public static JarBuilder of(File file) throws IOException {
        return new JarBuilder(new ZipOutputStream(Files.newOutputStream(file.toPath())));
    }

    static JarBuilder of(OutputStream out) {
        return new JarBuilder(new ZipOutputStream(out));
    }
}
