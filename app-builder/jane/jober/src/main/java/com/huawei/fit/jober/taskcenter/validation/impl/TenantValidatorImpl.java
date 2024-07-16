/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.util.ParamUtils;
import com.huawei.fit.jober.taskcenter.validation.AbstractValidator;
import com.huawei.fit.jober.taskcenter.validation.TenantValidator;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.util.StringUtils;

/**
 * {@link TenantValidator}的默认实现。
 *
 * @author 陈镕希 c00572808
 * @since 2023-10-08
 */
@Component
public class TenantValidatorImpl extends AbstractValidator implements TenantValidator {
    private final int nameLengthMaximum;

    public TenantValidatorImpl(@Value("${validation.tenant.name.length.maximum:64}") int nameLengthMaximum) {
        this.nameLengthMaximum = nameLengthMaximum;
    }

    @Override
    public String name(String name, OperationContext context) {
        if (StringUtils.isEmpty(name)) {
            throw new BadRequestException(ErrorCodes.TENANT_NAME_REQUIRED, ParamUtils.convertOperationContext(context));
        } else if (name.length() > this.nameLengthMaximum) {
            throw new BadRequestException(ErrorCodes.TENANT_NAME_LENGTH_OUT_OF_BOUNDS,
                    ParamUtils.convertOperationContext(context));
        } else {
            return name;
        }
    }
}
