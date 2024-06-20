/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.enums;

/**
 * App的状态.
 *
 * @author z00559346 张越
 * @since 2024-05-24
 */
public enum AppState {
    PUBLISHED("active"),

    INACTIVE("inactive");

    private final String name;

    AppState(String name) {
        this.name = name;
    }

    /**
     * 获取状态名称.
     *
     * @return 状态名称.
     */
    public String getName() {
        return name;
    }
}
