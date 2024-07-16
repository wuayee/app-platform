/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.domain.PropertyScope;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.util.ParamUtils;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fit.jober.taskcenter.validation.AbstractValidator;
import com.huawei.fit.jober.taskcenter.validation.PropertyValidator;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.util.StringUtils;

/**
 * {@link PropertyValidator}的默认实现。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-18
 */
@Component
public class PropertyValidatorImpl extends AbstractValidator implements PropertyValidator {
    private final int nameLengthMinimum;

    private final int nameLengthMaximum;

    private final int descriptionLengthMaximum;

    private final int dataTypeLengthMaximum;

    private final int scopeLengthMaximum;

    public PropertyValidatorImpl(@Value("${validation.property.name.length.minimum:1}") int nameLengthMinimum,
            @Value("${validation.property.name.length.maximum:64}") int nameLengthMaximum,
            @Value("${validation.property.description.length.maximum:512}") int descriptionLengthMaximum,
            @Value("${validation.property.dataType.length.maximum:16}") int dataTypeLengthMaximum,
            @Value("${validation.property.scope.length.maximum:16}") int scopeLengthMaximum) {
        this.nameLengthMinimum = nameLengthMinimum;
        this.nameLengthMaximum = nameLengthMaximum;
        this.descriptionLengthMaximum = descriptionLengthMaximum;
        this.dataTypeLengthMaximum = dataTypeLengthMaximum;
        this.scopeLengthMaximum = scopeLengthMaximum;
    }

    @Override
    public String validatePropertyId(String propertyId, OperationContext context) {
        if (propertyId == null) {
            throw new BadRequestException(ErrorCodes.PROPERTY_REQUIRED, ParamUtils.convertOperationContext(context));
        } else {
            return Entities.validateId(propertyId, () -> new BadRequestException(ErrorCodes.PROPERTY_INVALID,
                    ParamUtils.convertOperationContext(context)));
        }
    }

    @Override
    public String validateTaskId(String taskId, OperationContext context) {
        return super.validateTaskId(taskId, context);
    }

    @Override
    public String validateName(String name, OperationContext context) {
        if (StringUtils.isEmpty(name)) {
            throw new BadRequestException(ErrorCodes.PROPERTY_NAME_REQUIRED,
                    ParamUtils.convertOperationContext(context));
        } else if (name.length() > this.nameLengthMaximum) {
            throw new BadRequestException(ErrorCodes.PROPERTY_NAME_LENGTH_OUT_OF_BOUNDS,
                    ParamUtils.convertOperationContext(context));
        } else if (name.length() < this.nameLengthMinimum) {
            throw new BadRequestException(ErrorCodes.PROPERTY_NAME_LENGTH_LESS_THAN_BOUNDS,
                    ParamUtils.convertOperationContext(context));
        } else {
            return name;
        }
    }

    @Override
    public String validateDescription(String description, OperationContext context) {
        if (StringUtils.isEmpty(description)) {
            return "";
        } else if (description.length() > this.descriptionLengthMaximum) {
            throw new BadRequestException(ErrorCodes.PROPERTY_DESCRIPTION_LENGTH_OUT_OF_BOUNDS,
                    ParamUtils.convertOperationContext(context));
        } else {
            return description;
        }
    }

    @Override
    public PropertyDataType validateDataType(String dataType, OperationContext context) {
        if (StringUtils.isEmpty(dataType)) {
            return PropertyDataType.TEXT;
        } else if (dataType.length() > this.dataTypeLengthMaximum) {
            throw new BadRequestException(ErrorCodes.PROPERTY_DATATYPE_LENGTH_OUT_OF_BOUNDS,
                    ParamUtils.convertOperationContext(context));
        } else {
            return Enums.parse(PropertyDataType.class, dataType);
        }
    }

    @Override
    public Boolean validateIdentifiable(Boolean identifiable) {
        return nullIf(identifiable, false);
    }

    @Override
    public Boolean validateRequired(Boolean required) {
        if (required == null) {
            return false;
        } else {
            return required;
        }
    }

    @Override
    public PropertyScope validateScope(String scope, OperationContext context) {
        if (StringUtils.isEmpty(scope)) {
            return PropertyScope.PUBLIC;
        } else if (scope.length() > this.scopeLengthMaximum) {
            throw new BadRequestException(ErrorCodes.PROPERTY_SCOPE_LENGTH_OUT_OF_BOUNDS,
                    ParamUtils.convertOperationContext(context));
        } else {
            return Enums.parse(PropertyScope.class, scope);
        }
    }

    @Override
    public String validateAppearance(String appearance) {
        if (StringUtils.isEmpty(appearance)) {
            return "{}";
        } else {
            return appearance;
        }
    }
}
