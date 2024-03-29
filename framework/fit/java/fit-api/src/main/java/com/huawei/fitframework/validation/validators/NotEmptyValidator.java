/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.validation.validators;

import com.huawei.fitframework.util.ArrayUtils;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.validation.ConstraintValidator;
import com.huawei.fitframework.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

/**
 * 表示 {@link NotEmpty} 约束的校验器。
 *
 * @author 邬涨财 w00575064
 * @since 2023-03-08
 */
public class NotEmptyValidator implements ConstraintValidator<NotEmpty, Object> {
    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return false;
        } else if (value instanceof List) {
            return CollectionUtils.isNotEmpty(ObjectUtils.cast(value));
        } else if (value instanceof Map) {
            return MapUtils.isNotEmpty(ObjectUtils.cast(value));
        } else if (value instanceof String) {
            return StringUtils.isNotEmpty(ObjectUtils.cast(value));
        } else if (value instanceof Object[]) {
            return ArrayUtils.isNotEmpty(ObjectUtils.cast(value));
        } else {
            throw new UnsupportedOperationException("Failed to validate value: invalid value.");
        }
    }
}
