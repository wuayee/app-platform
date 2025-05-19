/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.common.util.ParamUtils;
import modelengine.fit.jober.taskcenter.validation.TreeValidator;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.util.StringUtils;

/**
 * 为 {@link TreeValidator} 提供实现。
 *
 * @author 梁济时
 * @since 2023-08-17
 */
@Component
public class TreeValidatorImpl implements TreeValidator {
    private final int nameLengthMaximum;

    public TreeValidatorImpl(
            @Value("${validation.tree.name.length.maximum:64}") int nameLengthMaximum) {
        this.nameLengthMaximum = nameLengthMaximum;
    }

    @Override
    public String name(String name, OperationContext context) {
        if (StringUtils.isEmpty(name)) {
            throw new BadRequestException(ErrorCodes.TREE_NAME_REQUIRED, ParamUtils.convertOperationContext(context));
        } else if (name.length() > this.nameLengthMaximum) {
            throw new BadRequestException(ErrorCodes.TREE_NAME_LENGTH_OUT_OF_BOUNDS,
                    ParamUtils.convertOperationContext(context));
        } else {
            return name;
        }
    }

    @Override
    public String taskId(String taskId, OperationContext context) {
        if (StringUtils.isEmpty(taskId)) {
            return Entities.emptyId();
        } else {
            return Entities.validateId(taskId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID,
                    ParamUtils.convertOperationContext(context)));
        }
    }
}
