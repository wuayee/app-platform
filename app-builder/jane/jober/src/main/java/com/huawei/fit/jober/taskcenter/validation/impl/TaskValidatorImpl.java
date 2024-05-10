/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.fitable.util.ParamUtils;
import com.huawei.fit.jober.taskcenter.validation.AbstractValidator;
import com.huawei.fit.jober.taskcenter.validation.TaskValidator;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.util.StringUtils;

/**
 * 为{@link TaskValidator}提供实现
 *
 * @author 梁致强 l50033199
 * @since 2023-08-17
 */
@Component
public class TaskValidatorImpl extends AbstractValidator implements TaskValidator {
    private final int nameLengthMinimum;

    private final int nameLengthMaximum;

    public TaskValidatorImpl(@Value("${validation.task.name.length.minimum:1}") int nameLengthMinimum,
            @Value("${validation.task.name.length.maximum:64}") int nameLengthMaximum) {
        this.nameLengthMinimum = nameLengthMinimum;
        this.nameLengthMaximum = nameLengthMaximum;
    }

    @Override
    public String validateName(String name, OperationContext context) {
        if (StringUtils.isEmpty(name)) {
            throw new BadRequestException(ErrorCodes.TASK_NAME_REQUIRED, ParamUtils.convertOperationContext(context));
        } else if (name.length() > this.nameLengthMaximum) {
            throw new BadRequestException(ErrorCodes.TASK_NAME_LENGTH_OUT_OF_BOUNDS,
                    ParamUtils.convertOperationContext(context));
        } else if (name.length() < this.nameLengthMinimum) {
            throw new BadRequestException(ErrorCodes.TASK_NAME_LENGTH_LESS_THAN_BOUNDS,
                    ParamUtils.convertOperationContext(context));
        } else {
            return name;
        }
    }

    @Override
    public String validateTaskId(String taskId, OperationContext context) {
        return super.validateTaskId(taskId, context);
    }
}
