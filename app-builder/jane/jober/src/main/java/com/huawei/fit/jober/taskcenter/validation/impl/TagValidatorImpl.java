/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.validation.TagValidator;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.util.StringUtils;

/**
 * 为 {@link TagValidator} 提供实现。
 *
 * @author 梁济时
 * @since 2023-08-16
 */
@Component
public class TagValidatorImpl implements TagValidator {
    private final int tagLengthMaximum;

    private final int descriptionLengthMaximum;

    private final int objectTypeLengthMaximum;

    public TagValidatorImpl(
            @Value("${validation.tag.value.length.maximum:64}") int tagLengthMaximum,
            @Value("${validation.tag.description.length.maximum:512}") int descriptionLengthMaximum,
            @Value("${validation.tag.objectType.length.maximum:16}") int objectTypeLengthMaximum) {
        this.tagLengthMaximum = tagLengthMaximum;
        this.descriptionLengthMaximum = descriptionLengthMaximum;
        this.objectTypeLengthMaximum = objectTypeLengthMaximum;
    }

    @Override
    public String tag(String tag) {
        if (StringUtils.isEmpty(tag)) {
            throw new BadRequestException(ErrorCodes.TAG_REQUIRED);
        } else if (tag.length() > this.tagLengthMaximum) {
            throw new BadRequestException(ErrorCodes.TAG_LENGTH_OUT_OF_BOUNDS);
        } else {
            return tag;
        }
    }

    @Override
    public String description(String description) {
        if (StringUtils.isEmpty(description)) {
            return "";
        } else if (description.length() > this.descriptionLengthMaximum) {
            throw new BadRequestException(ErrorCodes.TAG_DESCRIPTION_LENGTH_OUT_OF_BOUNDS);
        } else {
            return description;
        }
    }

    @Override
    public String objectType(String objectType) {
        if (StringUtils.isEmpty(objectType)) {
            throw new BadRequestException(ErrorCodes.TAG_OBJECT_TYPE_REQUIRED);
        } else if (objectType.length() > this.objectTypeLengthMaximum) {
            throw new BadRequestException(ErrorCodes.TAG_OBJECT_TYPE_OUT_OF_BOUNDS);
        } else {
            return objectType;
        }
    }

    @Override
    public String objectId(String objectId) {
        if (StringUtils.isEmpty(objectId)) {
            throw new BadRequestException(ErrorCodes.TAG_OBJECT_ID_REQUIRED);
        } else {
            return Entities.validateId(objectId,
                    () -> new BadRequestException(ErrorCodes.TAG_OBJECT_ID_FORMAT_INCORRECT));
        }
    }
}
