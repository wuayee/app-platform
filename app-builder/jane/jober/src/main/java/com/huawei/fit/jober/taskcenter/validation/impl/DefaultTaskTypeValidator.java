/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.utils.VersionUtils;
import com.huawei.fit.jober.taskcenter.validation.TaskTypeValidator;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.util.StringUtils;

/**
 * 为 {@link TaskTypeValidator} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @since 2023-09-13
 */
@Component
public class DefaultTaskTypeValidator implements TaskTypeValidator {
    private final int nameLengthMaximum;

    public DefaultTaskTypeValidator(@Value("${validation.task-type.name.length.maximum:64}") int nameLengthMaximum) {
        this.nameLengthMaximum = nameLengthMaximum;
    }

    @Override
    public String taskId(String taskId) {
        String actualTaskId = StringUtils.trim(taskId);
        if (StringUtils.isEmpty(taskId)) {
            throw new BadRequestException(ErrorCodes.TASK_ID_REQUIRED);
        }
        return Entities.validateId(actualTaskId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));
    }

    @Override
    public String name(String name) {
        String actual = StringUtils.trim(name);
        String versionName = VersionUtils.getRealName(actual);
        if (StringUtils.isEmpty(versionName) || versionName.length() > this.nameLengthMaximum) {
            throw new BadRequestException(ErrorCodes.TYPE_NAME_LENGTH_OUT_OF_BOUNDS);
        }
        return actual;
    }

    @Override
    public String parentId(String parentId) {
        String actual = StringUtils.trim(parentId);
        if (StringUtils.isEmpty(actual)) {
            return null;
        }
        return Entities.validateId(actual, () -> new BadRequestException(ErrorCodes.TYPE_PARENT_ID_INVALID));
    }
}
