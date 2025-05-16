/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation;

import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.common.util.ParamUtils;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fitframework.log.Logger;

/**
 * 表示抽象校验器。
 *
 * @author 陈镕希
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
