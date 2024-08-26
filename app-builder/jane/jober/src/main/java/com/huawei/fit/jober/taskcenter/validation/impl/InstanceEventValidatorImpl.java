/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.domain.InstanceEventType;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fit.jober.taskcenter.validation.InstanceEventValidator;

import modelengine.fitframework.annotation.Component;

/**
 * 为 {@link InstanceEventValidator} 提供实现。
 *
 * @author 梁济时
 * @since 2023-09-04
 */
@Component
public class InstanceEventValidatorImpl implements InstanceEventValidator {
    @Override
    public String sourceId(String sourceId) {
        if (sourceId == null) {
            return null;
        }
        return Entities.validateId(sourceId, () -> new BadRequestException(ErrorCodes.SOURCE_SOURCE_INVALID));
    }

    @Override
    public String type(String type) {
        if (type == null) {
            return null;
        }
        return Enums.validate(InstanceEventType.class, type,
                () -> new BadRequestException(ErrorCodes.INSTANCE_EVENT_TYPE_INCORRECT));
    }

    @Override
    public String fitableId(String fitableId) {
        if (fitableId == null) {
            return null;
        }
        return Entities.validateId(fitableId, () -> new BadRequestException(ErrorCodes.FITABLE_ID_INVALID));
    }
}
