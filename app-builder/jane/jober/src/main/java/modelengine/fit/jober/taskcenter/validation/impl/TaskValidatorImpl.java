/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.common.util.ParamUtils;
import modelengine.fit.jober.common.utils.VersionUtils;
import modelengine.fit.jober.taskcenter.validation.AbstractValidator;
import modelengine.fit.jober.taskcenter.validation.TaskValidator;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.util.StringUtils;

/**
 * 为{@link TaskValidator}提供实现
 *
 * @author 梁致强
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
        String realName = VersionUtils.getRealName(name);
        if (StringUtils.isEmpty(realName)) {
            throw new BadRequestException(ErrorCodes.TASK_NAME_REQUIRED, ParamUtils.convertOperationContext(context));
        } else if (realName.length() > this.nameLengthMaximum) {
            throw new BadRequestException(ErrorCodes.TASK_NAME_LENGTH_OUT_OF_BOUNDS,
                    ParamUtils.convertOperationContext(context));
        } else if (realName.length() < this.nameLengthMinimum) {
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
