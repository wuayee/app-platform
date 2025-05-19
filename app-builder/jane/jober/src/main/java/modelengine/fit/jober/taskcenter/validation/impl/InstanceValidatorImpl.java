/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.taskcenter.validation.AbstractValidator;
import modelengine.fit.jober.taskcenter.validation.InstanceValidator;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
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
