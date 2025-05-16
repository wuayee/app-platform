/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.common.util.ParamUtils;
import modelengine.fit.jober.taskcenter.validation.AbstractValidator;
import modelengine.fit.jober.taskcenter.validation.TenantValidator;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.util.StringUtils;

/**
 * {@link TenantValidator}的默认实现。
 *
 * @author 陈镕希
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
