/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.enums;

import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * 应用类型枚举类
 *
 * @author l00498867
 * @since 2024/4/23
 */
@Getter
public enum AppCategory {
    APP("应用", "app", "APP", "APP", "huawei", "AIPP"),
    WATER_FLOW("工具流", "waterFlow", "TOOL", "WATERFLOW", "huawei", "AIPP"),
    FIT("工具", "fit", "TOOL", "FIT", "huawei", "AIPP");

    private final String description;
    private final String type;
    private final String category;
    private final String tag;
    private final String source;
    private final String xiaohaiType;

    AppCategory(String description, String type, String category, String tag, String source, String xiaohaiType) {
        this.description = description;
        this.type = type;
        this.category = category;
        this.tag = tag;
        this.source = source;
        this.xiaohaiType = xiaohaiType;
    }

    /**
     * 根据 {@code type} 找到枚举值
     *
     * @param type 表示类型。
     * @return {@link Optional} 包的枚举值。
     */
    public static Optional<AppCategory> findByType(String type) {
        for (AppCategory appCategory : AppCategory.values()) {
            if (StringUtils.equalsIgnoreCase(appCategory.getType(), type)) {
                return Optional.of(appCategory);
            }
        }
        return Optional.empty();
    }
}
