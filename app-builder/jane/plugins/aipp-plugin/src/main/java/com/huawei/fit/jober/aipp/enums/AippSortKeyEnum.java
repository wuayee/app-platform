/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.enums;

import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;

import lombok.Getter;

import java.util.Arrays;

/**
 * 枚举类，用于表示排序的key
 *
 * @author: 00664640
 * @since: 2024-05-10
 */
@Getter
public enum AippSortKeyEnum {
    CREATE_AT("created_at"),
    UPDATE_AT("updated_at");

    private final String key;

    AippSortKeyEnum(String key) {
        this.key = key;
    }

    /**
     * 根据输入的key获取对应的枚举值
     *
     * @param key 需要查找的key
     * @return 返回对应的枚举值
     * @throws AippParamException 当输入的key不在枚举值中时，抛出参数异常
     */
    public static AippSortKeyEnum getSortKey(String key) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(() -> new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID, key));
    }
}
