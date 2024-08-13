/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.util.ParamUtils;
import com.huawei.fit.jober.taskcenter.validation.TreeValidator;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.util.StringUtils;

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
