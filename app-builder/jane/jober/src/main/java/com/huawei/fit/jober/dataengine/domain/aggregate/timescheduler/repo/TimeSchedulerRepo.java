/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.repo;

import com.huawei.fit.jober.dataengine.domain.Repo;
import com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;

import java.util.List;

/**
 * 定时任务Repo
 *
 * @author 00693950
 * @since 2023/6/13
 */
public interface TimeSchedulerRepo extends Repo<TimeScheduler> {
    /**
     * 查询所有的定时任务数据
     *
     * @return 定时任务列表
     */
    List<TimeScheduler> queryAllScheduler();

    /**
     * 根据定时任务ID查询对应的定时任务
     *
     * @param schedulerId 定时任务ID
     * @return {@link TimeScheduler}
     */
    TimeScheduler querySchedulerById(String schedulerId);

    /**
     * 根据taskSourceID查询对应的定时任务
     *
     * @param taskSourceId 任务数据源定义唯一标识
     * @return {@link TimeScheduler}
     */
    TimeScheduler queryByTaskSourceId(String taskSourceId);
}
