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
 * @since 2024-12-30
 */
@Getter
public enum AppStatus {
    PUBLISHED("published"),
    DRAFT("draft"),
    IMPORTING("importing");

    private final String name;

    AppStatus(String name) {
        this.name = name;
    }

    /**
     * 将状态转换为 {@link AppStatus} 对象.
     *
     * @param name 名称
     * @return 发布状态
     */
    public static AppStatus toStatus(String name) {
        return Arrays.stream(values())
                .filter(value -> StringUtils.equalsIgnoreCase(name, value.getName()))
                .findFirst()
                .orElseThrow(() -> new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID, name));
    }
}
