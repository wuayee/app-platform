/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.eventhandler.converter;

import modelengine.fit.jober.entity.instance.Instance;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskInstance;

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
