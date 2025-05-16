/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.taskcenter.domain.SourceType;
import modelengine.fit.jober.taskcenter.util.Enums;
import modelengine.fit.jober.taskcenter.validation.AbstractValidator;
import modelengine.fit.jober.taskcenter.validation.SourceValidator;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.util.StringUtils;

/**
 * 为 {@link SourceValidator} 提供实现。
 *
 * @author 陈镕希
 * @since 2023-08-15
 */
@Component
public class SourceValidatorImpl extends AbstractValidator implements SourceValidator {
    private final int sourceAppLengthMaximum;

    private final int sourceTypeLengthMaximum;

    private final int sourceNameLengthMaximum;

    public SourceValidatorImpl(@Value("${validation.source.app.length.maximum:64}") int sourceAppLengthMaximum,
            @Value("${validation.source.type.length.maximum:32}") int sourceTypeLengthMaximum,
            @Value("${validation.source.name.length.maximum:64}") int sourceNameLengthMaximum) {
        this.sourceAppLengthMaximum = sourceAppLengthMaximum;
        this.sourceTypeLengthMaximum = sourceTypeLengthMaximum;
        this.sourceNameLengthMaximum = sourceNameLengthMaximum;
    }

    @Override
    public String validateSourceId(String sourceId) {
        if (sourceId == null) {
            throw new BadRequestException(ErrorCodes.SOURCE_SOURCE_REQUIRED);
        } else {
            return Entities.validateId(sourceId, () -> new BadRequestException(ErrorCodes.SOURCE_SOURCE_INVALID));
        }
    }

    @Override
    public String validateSourceName(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new BadRequestException(ErrorCodes.SOURCE_NAME_REQUIRED);
        } else if (name.length() > this.sourceNameLengthMaximum) {
            throw new BadRequestException(ErrorCodes.SOURCE_NAME_LENGTH_OUT_OF_BOUNDS);
        } else {
            return name;
        }
    }

    @Override
    public String validateSourceApp(String app) {
        if (StringUtils.isEmpty(app)) {
            throw new BadRequestException(ErrorCodes.SOURCE_APP_REQUIRED);
        } else if (app.length() > this.sourceAppLengthMaximum) {
            throw new BadRequestException(ErrorCodes.SOURCE_APP_LENGTH_OUT_OF_BOUNDS);
        } else {
            return app;
        }
    }

    @Override
    public SourceType validateSourceType(String type) {
        if (StringUtils.isEmpty(type)) {
            throw new BadRequestException(ErrorCodes.SOURCE_TYPE_REQUIRED);
        } else if (type.length() > this.sourceTypeLengthMaximum) {
            throw new BadRequestException(ErrorCodes.SOURCE_TYPE_LENGTH_OUT_OF_BOUNDS);
        } else {
            return Enums.parse(SourceType.class, type);
        }
    }

    @Override
    public String validateTaskId(String taskId) {
        return super.validateTaskId(taskId);
    }

    @Override
    public String validateTypeId(String typeId) {
        if (typeId == null) {
            throw new BadRequestException(ErrorCodes.TYPE_ID_REQUIRED);
        } else {
            return Entities.validateId(typeId, () -> new BadRequestException(ErrorCodes.TYPE_ID_INVALID));
        }
    }
}
