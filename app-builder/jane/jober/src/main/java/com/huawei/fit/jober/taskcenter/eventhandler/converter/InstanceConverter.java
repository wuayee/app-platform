/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.eventhandler.converter;

import com.huawei.fit.jober.entity.instance.Instance;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;

/**
 * Instance相关转换类。
 *
 * @author 陈镕希
 * @since 2023-09-08
 */
public interface InstanceConverter {
    /**
     * 将 {@link TaskInstance} 转为 {@link Instance}。
     *
     * @param task 表示任务实例所属的任务定义的 {@link TaskEntity}。
     * @param instance 表示待转换的任务实例的 {@link TaskInstance}。
     * @return 表示转换得到的任务实例的 {@link Instance}。
     */
    Instance convert(TaskEntity task, TaskInstance instance);
}
