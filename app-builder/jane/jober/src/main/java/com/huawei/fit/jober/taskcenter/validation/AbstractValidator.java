/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation;

import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.util.ParamUtils;
import com.huawei.fitframework.log.Logger;

/**
 * 表示抽象校验器。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-15
 */
public abstract class AbstractValidator implements Validator {
    private static final Logger log = Logger.get(AbstractValidator.class);

    @Override
    public String validateTenantId(String tenantId) {
        if (tenantId == null) {
            throw new BadRequestException(ErrorCodes.TENANT_REQUIRED);
        } else {
            return tenantId;
        }
    }

    /**
     * 校验taskId
     *
     * @param taskId the task id
     * @param context the context
     * @return the string
     */
    public String validateTaskId(String taskId, OperationContext context) {
        if (taskId == null) {
            throw new BadRequestException(ErrorCodes.TASK_ID_REQUIRED, ParamUtils.convertOperationContext(context));
        } else {
            return Entities.validateId(taskId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID,
                    ParamUtils.convertOperationContext(context)));
        }
    }

    /**
     * 校验taskId
     *
     * @param taskId the task id
     * @return the string
     */
    public String validateTaskId(String taskId) {
        if (taskId == null) {
            throw new BadRequestException(ErrorCodes.TASK_ID_REQUIRED);
        } else {
            return Entities.validateId(taskId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));
        }
    }

    @Override
    public void validatePagination(long offset, int limit) {
        if (offset < 0L) {
            log.error("The offset of pagination out of range. Input offset is {}", offset);
            throw new BadRequestException(ErrorCodes.PAGINATION_OFFSET_INVALID);
        }
        if (limit < 0 || limit > 200) {
            log.error("The limit of pagination out of range. Input limit is {}", limit);
            throw new BadRequestException(ErrorCodes.PAGINATION_LIMIT_INVALID);
        }
    }
}
