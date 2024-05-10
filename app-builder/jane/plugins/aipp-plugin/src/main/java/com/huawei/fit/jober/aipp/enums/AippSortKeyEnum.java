package com.huawei.fit.jober.aipp.enums;

import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AippSortKeyEnum {
    CREATE_AT("created_at"),
    UPDATE_AT("updated_at");

    private final String key;

    AippSortKeyEnum(String key) {
        this.key = key;
    }

    public static AippSortKeyEnum getSortKey(String key) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(() -> new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID, key));
    }
}
