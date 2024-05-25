/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 解码工具
 *
 * @since 2024/5/25
 */
public class DecodeUtil {
    /**
     * 解码str
     *
     * @param encodeStr 编码str
     * @return 解码后的str
     */
    public static String decodeStr(String encodeStr) {
        try {
            return URLDecoder.decode(encodeStr, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return encodeStr;
        }
    }
}
