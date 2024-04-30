/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.validators.rules;

import static com.huawei.fit.waterflow.common.ErrorCodes.INPUT_PARAM_IS_INVALID;

import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 校验规则公共接口
 *
 * @author g00564732
 * @since 1.0
 */
public interface Rules {
    /**
     * exception
     *
     * @param paramName paramName
     * @return Supplier<JobberParamException>
     */
    default Supplier<WaterflowParamException> exception(String paramName) {
        return () -> new WaterflowParamException(INPUT_PARAM_IS_INVALID, paramName);
    }

    /**
     * validateEmpty
     *
     * @param list list
     * @param paramName paramName
     */
    default <T> void validateEmpty(List<T> list, String paramName) {
        if (CollectionUtils.isNotEmpty(list)) {
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, paramName);
        }
    }

    /**
     * validateBlank
     *
     * @param string string
     * @param paramName paramName
     */
    default void validateBlank(String string, String paramName) {
        if (StringUtils.isNotBlank(string)) {
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, paramName);
        }
    }

    /**
     * validateNull
     *
     * @param object object
     * @param paramName paramName
     */
    default <T> void validateNull(T object, String paramName) {
        if (Objects.nonNull(object)) {
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, paramName);
        }
    }
}
