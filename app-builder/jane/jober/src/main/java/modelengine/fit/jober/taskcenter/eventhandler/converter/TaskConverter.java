/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.eventhandler.converter;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.entity.task.Task;
import modelengine.fit.jober.entity.task.TaskProperty;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;

/**
 * Task相关转换类。
 *
 * @author 陈镕希
 * @since 2023-09-08
 */
public interface TaskConverter {
    /**
     * convert
     *
     * @param entity entity
     * @param context context
     * @return Task
     */
    Task convert(TaskEntity entity, OperationContext context);

    /**
     * 将TaskProperty实体转换为TaskProperty对象。
     *
     * @param entity 待转换的TaskProperty实体
     * @return 转换后的TaskProperty对象
     */
    TaskProperty convert(modelengine.fit.jane.task.domain.TaskProperty entity);
}
