/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.enums;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;

import lombok.Getter;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;

/**
 * App的状态.
 *
 * @author 张越
 * @since 2024-05-24
 */
@Getter
public enum AppState {
    PUBLISHED("active"),

    INACTIVE("inactive"),

    IMPORTING("importing");

    private final String name;

    AppState(String name) {
        this.name = name;
    }

    /**
     * 获取状态
     *
     * @param name 名称
     * @return 发布状态
     */
    public static AppState getAppState(String name) {
        return Arrays.stream(values())
                .filter(value -> StringUtils.equalsIgnoreCase(name, value.getName()))
                .findFirst()
                .orElseThrow(() -> new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID, name));
    }
}
