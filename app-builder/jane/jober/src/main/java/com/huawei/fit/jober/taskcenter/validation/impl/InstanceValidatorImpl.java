/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.validation.AbstractValidator;
import com.huawei.fit.jober.taskcenter.validation.InstanceValidator;

import modelengine.fitframework.annotation.Component;

import java.util.Map;

/**
 * 为 {@link InstanceValidator} 提供实现。
 *
 * @author 梁济时
 * @since 2023-08-15
 */
@Component
public class InstanceValidatorImpl extends AbstractValidator implements InstanceValidator {
    @Override
    public String typeId(String typeId) {
        if (typeId == null) {
            throw new BadRequestException(ErrorCodes.INSTANCE_SOURCE_REQUIRED);
        } else {
            return Entities.validateId(typeId, () -> new BadRequestException(ErrorCodes.INSTANCE_TYPE_INVALID));
        }
    }

    @Override
    public String sourceId(String sourceId) {
        if (sourceId == null) {
            throw new BadRequestException(ErrorCodes.INSTANCE_SOURCE_REQUIRED);
        } else {
            return Entities.validateId(sourceId, () -> new BadRequestException(ErrorCodes.INSTANCE_SOURCE_INVALID));
        }
    }

    @Override
    public Map<String, Object> info(Map<String, Object> info) {
        if (info == null) {
            throw new BadRequestException(ErrorCodes.INSTANCE_INFO_REQUIRED);
        } else {
            return info;
        }
    }

    @Override
    public String validateTaskId(String taskId) {
        return super.validateTaskId(taskId);
    }
}
