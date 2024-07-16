/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.validation;

/**
 * 校验工具标签拼接方式的工具类。
 *
 * @author 鲁为 l00839724
 * @since 2024-07-19
 */
public class ValidateTagMode {
    /**
     * 校验校验工具标签拼接方式。
     *
     * @param mode 表示标签拼接方式的 {@link String}。
     * @return 校验之后的标签拼接方式的 {@link String}。
     */
    public static String validateTagMode(String mode) {
        if (mode.equals("OR") || mode.equals("or")) {
            return "OR";
        }
        return "AND";
    }
}
