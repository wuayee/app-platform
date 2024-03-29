/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.validation.validators;

import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.validation.ConstraintValidator;
import com.huawei.fitframework.validation.constraints.NotBlank;

/**
 * 表示 {@link NotBlank} 约束的校验器。
 *
 * @author 邬涨财 w00575064
 * @since 2023-03-08
 */
public class NotBlankValidator implements ConstraintValidator<NotBlank, String> {
    @Override
    public boolean isValid(String value) {
        return StringUtils.isNotBlank(value);
    }
}
