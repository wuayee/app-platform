/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.biz.converter.support;

import com.huawei.fit.jober.dataengine.biz.converter.TaskInstanceMetaDataConverter;
import com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;
import com.huawei.fit.jober.dataengine.rest.response.TaskInstanceMetaData;
import com.huawei.fit.jober.entity.TaskEntity;

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
