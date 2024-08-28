/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.common.utils;

import java.util.UUID;

/**
 * Uuid的Utils类。
 *
 * @author 孙怡菲
 * @since 2023-06-15
 */
public class UUIDUtil {
    /**
     * 随机生成uuid。
     *
     * @return 随机生成的uuid的 {@link String}。
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
