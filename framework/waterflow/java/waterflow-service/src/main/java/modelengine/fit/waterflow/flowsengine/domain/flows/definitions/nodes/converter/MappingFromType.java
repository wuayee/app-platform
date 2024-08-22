/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import static com.huawei.fit.jober.common.ErrorCodes.ENUM_CONVERT_FAILED;

import com.huawei.fit.jober.common.exceptions.JobberParamException;

import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;

/**
 * 映射来源类型
 *
 * @author 宋永坦
 * @since 2024/4/18
 */
@Getter
public enum MappingFromType {
    INPUT("INPUT"),
    REFERENCE("REFERENCE"),
    EXPAND("EXPAND"),
    ;

    private String code;

    MappingFromType(String code) {
        this.code = code;
    }

    /**
     * 根据code获取枚举值
     *
     * @param code 枚举值对应的code
     * @return 返回对应的枚举值
     * @throws JobberParamException 当找不到对应的枚举值时，抛出此异常
     */
    public static MappingFromType get(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> new JobberParamException(ENUM_CONVERT_FAILED, "MappingFromType", code));
    }
}
