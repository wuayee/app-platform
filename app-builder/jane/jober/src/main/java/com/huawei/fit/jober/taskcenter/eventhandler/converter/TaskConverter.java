/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.eventhandler.converter;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.entity.task.Task;
import com.huawei.fit.jober.entity.task.TaskProperty;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;

/**
 * Task相关转换类。
 *
 * @author 陈镕希 c00572808
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
    TaskProperty convert(com.huawei.fit.jane.task.domain.TaskProperty entity);
}
