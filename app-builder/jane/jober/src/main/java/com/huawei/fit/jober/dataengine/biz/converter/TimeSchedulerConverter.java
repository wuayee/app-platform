/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.biz.converter;

import com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;
import com.huawei.fit.jober.dataengine.rest.request.StaticMetaDataTaskDto;

/**
 * 定时任务Converter
 *
 * @author 晏钰坤
 * @since 2023/6/27
 */
public interface TimeSchedulerConverter {
    /**
     * {@link TimeScheduler} 转换类
     *
     * @param staticMetaDataTaskDTO {@link StaticMetaDataTaskDto}
     * @return {@link TimeScheduler}
     */
    TimeScheduler converter(StaticMetaDataTaskDto staticMetaDataTaskDTO);
}
