/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.util;

import com.huawei.fitframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 解压 zip 文件的工具类。
 *
 * @author 杭潇 h00675922
 * @since 2024-07-11
 */
public final class UnzipUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private UnzipUtils() {}

    /**
     * 解压缩指定的 zip 文件到目标目录。
     *
     * @param zipFile 表示需要解压缩的 zip 文件的 {@link File} 对象。
     * @param destDir 表示解压缩文件的目标目录的 {@link File} 对象。
     * @throws IOException 如果在读取 zip 文件或写入目标目录时发生 I/O 错误，则抛出此异常。
     * @throws IllegalArgumentException 如果 zip 文件格式错误，则抛出此异常。
     */
    public static void unzipFile(File zipFile, File destDir) throws IOException {
        // 待添加文件校验，比如 zip 炸弹
        try (ZipInputStream zipIn = new ZipInputStream(
                Files.newInputStream(zipFile.toPath(), StandardOpenOption.READ))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                try {
                    entry = processZipEntry(destDir, entry, zipIn);
                } catch (IOException e) {
                    throw new IllegalStateException(
                            StringUtils.format("Failed to process zip entry. [entry={0}]", entry.getName()), e);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(StringUtils.format("Malformed zip file. [file={0}]", zipFile.getPath()),
                    e);
        }
    }

    private static ZipEntry processZipEntry(File destDir, ZipEntry entry, ZipInputStream zipIn) throws IOException {
        File newFile = createNewFile(destDir, entry);
        if (entry.isDirectory()) {
            if (!newFile.isDirectory() && !newFile.mkdirs()) {
                throw new IllegalStateException(StringUtils.format("Failed to create directory [file={0}]", newFile));
            }
        } else {
            File parent = newFile.getParentFile();
            if (!parent.isDirectory() && !parent.mkdirs()) {
                throw new IllegalStateException(StringUtils.format("Failed to create directory [file={0}]", parent));
            }
            writeToFile(zipIn, newFile);
        }
        zipIn.closeEntry();
        return zipIn.getNextEntry();
    }

    private static void writeToFile(InputStream zipIn, File newFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(newFile)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = zipIn.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
    }

    private static File createNewFile(File destDir, ZipEntry entry) throws IOException {
        File newFile = new File(destDir, entry.getName());
        String destDirPath = destDir.getCanonicalPath();
        String newFilePath = newFile.getCanonicalPath();

        if (!newFilePath.startsWith(destDirPath + File.separator)) {
            throw new IllegalStateException(
                    StringUtils.format("Entry is outside of the target dir: [file={0}]", newFile));
        }
        return newFile;
    }
}