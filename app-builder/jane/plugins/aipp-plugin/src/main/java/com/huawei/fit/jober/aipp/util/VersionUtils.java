/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

/**
 * @author 姚江 yWX1299574
 * @since 2024-07-02
 */
public class VersionUtils {
    public static final int PREVIEW_UUID_LEN = 6;
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

    public static String buildPreviewVersion(String version) {
        String uuid = UUIDUtil.uuid();
        String subUuid = (uuid.length() > PREVIEW_UUID_LEN) ? uuid.substring(0, PREVIEW_UUID_LEN) : uuid;
        return version + "-" + subUuid;
    }
}
