/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.dataengine.biz.converter;

import modelengine.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;
import modelengine.fit.jober.dataengine.rest.response.TaskInstanceMetaData;
import modelengine.fit.jober.entity.TaskEntity;

/**
 * 任务实例元数据converter
 *
 * @author 晏钰坤
 * @since 2023/6/27
 */
public interface TaskInstanceMetaDataConverter {
    /**
     * {@link TaskInstanceMetaData} 转换类
     *
     * @param timeScheduler {@link TimeScheduler}
     * @param taskEntity {@link TaskEntity}
     * @return {@link TaskInstanceMetaData}
     */
    TaskInstanceMetaData converter(TimeScheduler timeScheduler, TaskEntity taskEntity);
}
