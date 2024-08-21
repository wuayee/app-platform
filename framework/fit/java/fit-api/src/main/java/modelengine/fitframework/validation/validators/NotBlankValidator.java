/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.validation.validators;

import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.validation.ConstraintValidator;
import modelengine.fitframework.validation.constraints.NotBlank;

/**
 * 表示 {@link NotBlank} 约束的校验器。
 *
 * @author 邬涨财
 * @since 2023-03-08
 */
public class NotBlankValidator implements ConstraintValidator<NotBlank, String> {
    @Override
    public boolean isValid(String value) {
        return StringUtils.isNotBlank(value);
    }
}
