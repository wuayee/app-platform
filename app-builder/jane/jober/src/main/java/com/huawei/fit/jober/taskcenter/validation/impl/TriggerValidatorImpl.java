/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.util.ParamUtils;
import com.huawei.fit.jober.taskcenter.validation.AbstractValidator;
import com.huawei.fit.jober.taskcenter.validation.TriggerValidator;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;

/**
 * {@link TriggerValidator} 的默认实现。
 *
 * @author 陈镕希
 * @since 2023-08-18
 */
@Component
public class TriggerValidatorImpl extends AbstractValidator implements TriggerValidator {
    private final int nameLengthMaximum;

    public TriggerValidatorImpl(@Value("${validation.trigger.name.length.maximum:64}") int nameLengthMaximum) {
        this.nameLengthMaximum = nameLengthMaximum;
    }

    @Override
    public String validateTriggerId(String triggerId, OperationContext context) {
        if (triggerId == null) {
            throw new BadRequestException(ErrorCodes.TRIGGER_REQUIRED, ParamUtils.convertOperationContext(context));
        } else {
            return Entities.validateId(triggerId, () -> new BadRequestException(ErrorCodes.TRIGGER_INVALID,
                    ParamUtils.convertOperationContext(context)));
        }
    }

    @Override
    public String validateTaskId(String taskId, OperationContext context) {
        return super.validateTaskId(taskId, context);
    }

    @Override
    public String validatePropertyName(String propertyName, OperationContext context) {
        if (propertyName == null) {
            throw new BadRequestException(ErrorCodes.PROPERTY_NAME_REQUIRED,
                    ParamUtils.convertOperationContext(context));
        } else if (propertyName.length() > nameLengthMaximum) {
            throw new BadRequestException(ErrorCodes.PROPERTY_NAME_LENGTH_OUT_OF_BOUNDS,
                    ParamUtils.convertOperationContext(context));
        } else {
            return propertyName;
        }
    }

    @Override
    public String validateFitableId(String fitableId, OperationContext context) {
        if (fitableId == null) {
            throw new BadRequestException(ErrorCodes.FITABLE_ID_REQUIRED, ParamUtils.convertOperationContext(context));
        } else {
            return Entities.validateId(fitableId,
                    () -> new BadRequestException(ErrorCodes.FITABLE_ID_INVALID,
                            ParamUtils.convertOperationContext(context)));
        }
    }
}
