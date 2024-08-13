/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.biz.converter;

import com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;
import com.huawei.fit.jober.dataengine.rest.response.TaskInstanceMetaData;
import com.huawei.fit.jober.entity.TaskEntity;

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
