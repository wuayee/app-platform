/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用操作工具
 *
 * @author 陈潇文
 * @since 2024-08-26
 */
public class AppUtils {
    /**
     * 将值为*xxx*的excludeNames替换为{xxx}。
     *
     * @param excludeNames 表示查询接口排除的应用名称的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示替换处理后的询接口排除的应用名称的 {@link List}{@code <}{@link String}{@code >}。
     */
    public static List<String> replaceAsterisks(List<String> excludeNames) {
        return excludeNames
                .stream()
                .map(s -> s.replaceAll("\\*(\\w+)\\*", "{$1}"))
                .collect(Collectors.toList());
    }
}
