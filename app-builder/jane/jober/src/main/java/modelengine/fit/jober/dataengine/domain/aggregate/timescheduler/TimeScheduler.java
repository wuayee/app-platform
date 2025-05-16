/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.dataengine.domain.aggregate.timescheduler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.fit.jober.dataengine.domain.aggregate.timescheduler.repo.TimeSchedulerRepo;

import java.util.Map;

/**
 * 定时任务数据库实体
 *
 * @author 晏钰坤
 * @since 2023/6/13
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeScheduler {
    /**
     * 定时任务ID
     */
    private String schedulerId;

    /**
     * 任务定义ID
     */
    private String taskDefinitionId;

    /**
     * 任务数据源定义唯一标识
     */
    private String taskSourceId;

    /**
     * 任务类型ID
     */
    private String taskTypeId;

    /**
     * 数据获取类型
     */
    private String schedulerDataType;

    /**
     * 数据来源平台
     */
    private String sourceApp;

    /**
     * 第一次执行定时任务时间
     */
    private long createTime;

    /**
     * 第一次执行定时任务结束时间
     */
    private long endTime;

    /**
     * 定时任务时间间隔
     */
    private long schedulerInterval;

    /**
     * 上一次拉取数据的结束时间
     */
    private long latestExecutorTime;

    /**
     * 上一次修改定时任务时间
     */
    private long modifyTime;

    /**
     * 定时任务得过滤条件
     */
    private String filter;

    /**
     * 任务其他条件
     */
    private Map<String, String> properties;

    /**
     * 定时任务的执行节点IP
     */
    private String ownerAddress;

    /**
     * 定时任务Repo {@link TimeSchedulerRepo}
     */
    private TimeSchedulerRepo timeSchedulerRepo;

    /**
     * 保存定时任务
     *
     * @return 定时任务
     */
    public TimeScheduler save() {
        return timeSchedulerRepo.save(this);
    }

    /**
     * 删除定时任务
     */
    public void delete() {
        timeSchedulerRepo.delete(this);
    }
}
