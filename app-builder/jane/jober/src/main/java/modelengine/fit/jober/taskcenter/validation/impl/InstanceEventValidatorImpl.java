/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.taskcenter.domain.InstanceEventType;
import modelengine.fit.jober.taskcenter.util.Enums;
import modelengine.fit.jober.taskcenter.validation.InstanceEventValidator;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
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
