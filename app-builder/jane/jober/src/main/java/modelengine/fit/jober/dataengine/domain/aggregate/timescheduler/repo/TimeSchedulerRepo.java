/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.dataengine.domain.aggregate.timescheduler.repo;

import modelengine.fit.jober.dataengine.domain.Repo;
import modelengine.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;

import java.util.List;

/**
 * 定时任务Repo
 *
 * @author 晏钰坤
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
