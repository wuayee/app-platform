/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 表示文件唯一性校验的工具类。
 *
 * @author 杭潇 h00675922
 * @since 2024-07-11
 */
public final class FileHashUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private FileHashUtils() {}

    /**
     * 计算文件的哈希值。
     *
     * @param filePath 文件路径的 {@link String}。
     * @param algorithm 哈希算法（例如：MD5, SHA-1, SHA-256）的 {@link String}。
     * @return 文件的哈希值（16进制字符串）的 {@link String}。
     * @throws NoSuchAlgorithmException 如果指定的哈希算法不可用。
     * @throws IOException 如果读取文件时发生 I/O 错误。
     */
    public static String calculateFileHash(String filePath, String algorithm)
            throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        try (InputStream fis = Files.newInputStream(Paths.get(filePath))) {
            byte[] byteArray = new byte[1024];
            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                messageDigest.update(byteArray, 0, bytesCount);
            }
        }
        byte[] digests = messageDigest.digest();

        StringBuilder sb = new StringBuilder();
        for (byte digest : digests) {
            sb.append(String.format("%02x", digest));
        }
        return sb.toString();
    }
}
