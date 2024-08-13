/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.biz.converter.support;

import com.huawei.fit.jober.common.Constant;
import com.huawei.fit.jober.dataengine.biz.converter.TimeSchedulerConverter;
import com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;
import com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.repo.TimeSchedulerRepo;
import com.huawei.fit.jober.dataengine.rest.request.StaticMetaDataTaskDto;
import com.huawei.fit.jober.entity.Filter;
import com.huawei.fitframework.annotation.Component;

import com.alibaba.fastjson.JSON;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * {@link TimeSchedulerConverter} 实现类
 *
 * @author 晏钰坤
 * @since 2023/6/27
 */
@Component
public class DefaultTimeSchedulerConverter implements TimeSchedulerConverter {
    private final TimeSchedulerRepo timeSchedulerRepo;

    public DefaultTimeSchedulerConverter(TimeSchedulerRepo timeSchedulerRepo) {
        this.timeSchedulerRepo = timeSchedulerRepo;
    }

    @Override
    public TimeScheduler converter(StaticMetaDataTaskDto staticMetaDataTaskDTO) {
        return TimeScheduler.builder()
                .taskDefinitionId(staticMetaDataTaskDTO.getTaskDefinitionId())
                .taskSourceId(staticMetaDataTaskDTO.getTaskSourceId())
                .taskTypeId(staticMetaDataTaskDTO.getTaskTypeId())
                .schedulerDataType(Constant.SCHEDULE)
                .sourceApp(staticMetaDataTaskDTO.getSourceApp())
                .createTime(System.currentTimeMillis())
                .endTime(Optional.ofNullable(staticMetaDataTaskDTO.getFilter())
                        .map(Filter::getEndTime)
                        .orElse(System.currentTimeMillis()))
                .schedulerInterval(TimeUnit.MINUTES.toMillis(
                        Long.parseLong(staticMetaDataTaskDTO.getProperties().get(Constant.SCHEDULER_INTERVAL))))
                .latestExecutorTime(0)
                .filter(Optional.ofNullable(staticMetaDataTaskDTO.getFilter()).map(JSON::toJSONString).orElse("{}"))
                .properties(staticMetaDataTaskDTO.getProperties())
                .modifyTime(0)
                .timeSchedulerRepo(timeSchedulerRepo)
                .build();
    }
}
