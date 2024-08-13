/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

/**
 * 版本号的工具类
 *
 * @author 姚江
 * @since 2024-07-02
 */
public class VersionUtils {
    /**
     * 预览版本号中UUID的长度
     */
    public static final int PREVIEW_UUID_LEN = 6;

    /**
     * 预览版本号的版本号与uuid之间的连接符号
     */
    public static final String CONNECTION_SIGN = "-";

    /**
     * 校验一个版本号是否为正确的版本号格式
     *
     * @param version 版本号
     * @return 版本号是否合法
     */
    public static boolean isValidVersion(String version) {
        String[] parts = version.split("\\.");
        if (parts.length != 3) {
            return false;
        }
        for (String part : parts) {
            if (!isNumeric(part)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 创建一个预览版本号
     *
     * @param version 版本号
     * @return 预览版本号
     */
    public static String buildPreviewVersion(String version) {
        String uuid = UUIDUtil.uuid();
        String subUuid = (uuid.length() > PREVIEW_UUID_LEN) ? uuid.substring(0, PREVIEW_UUID_LEN) : uuid;
        return version + CONNECTION_SIGN + subUuid;
    }
}
