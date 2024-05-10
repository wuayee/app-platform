/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jane.task.domain.RelationType;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fit.jober.taskcenter.validation.TaskRelationValidator;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

/**
 * 功能描述
 *
 * @author 罗书强 lwx1291633
 * @since 2024-01-02
 */
@Component
public class TaskRelationValidatorImpl implements TaskRelationValidator {
    private static final Logger log = Logger.get(AuthorizationValidatorImpl.class);

    private final int objectId1LengthMaximum;

    private final int objectType1LengthMaximum;

    private final int objectId2LengthMaximum;

    private final int objectType2LengthMaximum;

    private final int relationTypeLengthMaximum;

    public TaskRelationValidatorImpl(
            @Value("${validation.relation.objectId1.length.maximum:32}") int objectId1LengthMaximum,
            @Value("${validation.relation.objectType1.length.maximum:16}") int objectType1LengthMaximum,
            @Value("${validation.relation.objectId2.length.maximum:32}") int objectId2LengthMaximum,
            @Value("${validation.relation.objectType2.length.maximum:16}") int objectType2LengthMaximum,
            @Value("${validation.relation.relationType.length.maximum:16}") int relationTypeLengthMaximum) {
        this.objectId1LengthMaximum = objectId1LengthMaximum;
        this.objectType1LengthMaximum = objectType1LengthMaximum;
        this.objectId2LengthMaximum = objectId2LengthMaximum;
        this.objectType2LengthMaximum = objectType2LengthMaximum;
        this.relationTypeLengthMaximum = relationTypeLengthMaximum;
    }

    /**
     * 关联关系的唯一标识。
     *
     * @param id 表示任务关联关系的唯一标识的 {@link String}。
     * @return 表示符合要求的任务关联关系的唯一标识的 {@link String}。
     */
    @Override
    public String id(String id) {
        return Entities.validateId(StringUtils.trim(id), () -> {
            log.error("The id of task relation is invalid. [id={}]", id);
            return new BadRequestException(ErrorCodes.TASK_RELATION_RELATION_ID_INVALID);
        });
    }

    @Override
    public String objectId1(String objectId1) {
        String actual = StringUtils.trim(objectId1);
        if (StringUtils.isEmpty(actual)) {
            log.error("The objectId1 of task relation cannot be a blank string.");
            throw new BadRequestException(ErrorCodes.TASK_RELATION_OBJECT_ID1_REQUIRED);
        }
        if (actual.length() > this.objectId1LengthMaximum) {
            log.error("The objectId1 of task relation system is out of bounds. [objectId1={}, length={}, maximum={}]",
                    actual, actual.length(), this.objectId1LengthMaximum);
            throw new BadRequestException(ErrorCodes.TASK_RELATION_OBJECT_ID1_TOO_LONG);
        }
        return actual;
    }

    @Override
    public String objectType1(String objectType1) {
        String actual = StringUtils.trim(objectType1);
        if (StringUtils.isEmpty(actual)) {
            log.error("The objectType1 of task relation cannot be a blank string.");
            throw new BadRequestException(ErrorCodes.TASK_RELATION_OBJECT_TYPE1_REQUIRED);
        }
        if (actual.length() > this.objectType1LengthMaximum) {
            log.error(
                    "The objectType1 of task relation system is out of bounds. [objectType1={}, length={}, maximum={}]",
                    actual, actual.length(), this.objectType1LengthMaximum);
            throw new BadRequestException(ErrorCodes.TASK_RELATION_OBJECT_TYPE1_TOO_LONG);
        }
        return actual;
    }

    @Override
    public String objectId2(String objectId2) {
        String actual = StringUtils.trim(objectId2);
        if (StringUtils.isEmpty(actual)) {
            log.error("The objectId2 of task relation cannot be a blank string.");
            throw new BadRequestException(ErrorCodes.TASK_RELATION_OBJECT_ID2_REQUIRED);
        }
        if (actual.length() > this.objectId2LengthMaximum) {
            log.error("The objectId2 of task relation system is out of bounds. [objectId2={}, length={}, maximum={}]",
                    actual, actual.length(), this.objectId2LengthMaximum);
            throw new BadRequestException(ErrorCodes.TASK_RELATION_OBJECT_ID2_TOO_LONG);
        }
        return actual;
    }

    @Override
    public String objectType2(String objectType2) {
        String actual = StringUtils.trim(objectType2);
        if (StringUtils.isEmpty(actual)) {
            log.error("The objectType2 of task relation cannot be a blank string.");
            throw new BadRequestException(ErrorCodes.TASK_RELATION_OBJECT_TYPE2_REQUIRED);
        }
        if (actual.length() > this.objectType2LengthMaximum) {
            log.error(
                    "The objectType2 of task relation system is out of bounds. [objectType2={}, length={}, maximum={}]",
                    actual, actual.length(), this.objectType2LengthMaximum);
            throw new BadRequestException(ErrorCodes.TASK_RELATION_OBJECT_TYPE2_TOO_LONG);
        }
        return actual;
    }

    @Override
    public RelationType relationType(String relationType) {
        String actual = StringUtils.trim(relationType);
        if (StringUtils.isEmpty(actual)) {
            log.error("The relationType of task relation cannot be a blank string.");
            throw new BadRequestException(ErrorCodes.TASK_RELATION_RELATION_TYPE_REQUIRED);
        }
        if (actual.length() > this.relationTypeLengthMaximum) {
            log.error(
                    "The relationType of task relation system is out of bounds. [relationType={}, length={}, maximum={}]",
                    actual, actual.length(), this.relationTypeLengthMaximum);
            throw new BadRequestException(ErrorCodes.TASK_RELATION_RELATION_TYPE_TOO_LONG);
        }
        return Enums.parse(RelationType.class, actual);
    }
}
