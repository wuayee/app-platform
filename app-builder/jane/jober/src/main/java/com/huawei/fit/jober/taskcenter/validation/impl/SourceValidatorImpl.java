/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.domain.SourceType;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fit.jober.taskcenter.validation.AbstractValidator;
import com.huawei.fit.jober.taskcenter.validation.SourceValidator;
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
