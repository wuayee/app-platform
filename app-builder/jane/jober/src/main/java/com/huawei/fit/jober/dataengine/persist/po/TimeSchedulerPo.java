/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.persist.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TimeScheduler数据库对象
 *
 * @author 00693950
 * @since 2023/6/13
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSchedulerPo {
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
     * 上一次执行定时任务时间
     */
    private long latestExecutorTime;

    /**
     * 定时任务更改时间
     */
    private long modifyTime;

    /**
     * 定时任务得过滤条件
     */
    private String filter;

    /**
     * 不同数据获取方式所需的不同参数
     */
    private String properties;

    /**
     * 定时任务的执行节点IP
     */
    private String ownerAddress;
}
