/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.util;

import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 提供哈希算法的工具类。
 *
 * @author 邱晓霞
 * @since 2025-01-14
 */
public class HashUtil {
    private static final Logger LOG = Logger.get(HashUtil.class);

    /**
     * 生成 SHA-256 哈希值。
     *
     * @param input 表示输入字符串的 {@link String}。
     * @return 表示 SHA-256 哈希值的十六进制表示的{@link String}。
     */
    public static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Failed to generate hashcode.");
            return StringUtils.EMPTY;
        }
    }
}
