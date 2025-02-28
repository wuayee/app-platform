/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.dataengine.biz.converter.support;

import modelengine.fit.jober.dataengine.biz.converter.TaskInstanceMetaDataConverter;
import modelengine.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;
import modelengine.fit.jober.dataengine.rest.response.TaskInstanceMetaData;
import modelengine.fit.jober.entity.TaskEntity;
import modelengine.fitframework.annotation.Component;

/**
 * {@link TaskInstanceMetaDataConverter} 实现类
 *
 * @author 晏钰坤
 * @since 2023/6/27
 */
@Component
public class DefaultTaskInstanceMetaDataConverter implements TaskInstanceMetaDataConverter {
    @Override
    public TaskInstanceMetaData converter(TimeScheduler timeScheduler, TaskEntity taskEntity) {
        return TaskInstanceMetaData.builder()
                .taskDefinitionId(timeScheduler.getTaskDefinitionId())
                .taskSourceId(timeScheduler.getTaskSourceId())
                .taskTypeId(timeScheduler.getTaskTypeId())
                .taskEntity(taskEntity)
                .build();
    }
}
