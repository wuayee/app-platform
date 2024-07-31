/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.persist.mapper;

import com.huawei.fit.jober.dataengine.persist.po.TimeSchedulerPo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定时任务MybatisMapper类
 *
 * @author 00693950
 * @since 2023/6/20
 */
@Mapper
public interface TimeSchedulerMapper {
    /**
     * 创建定时任务
     *
     * @param timeScheduler 定时任务Entity {@link TimeSchedulerPo}
     */
    void create(@Param("scheduler") TimeSchedulerPo timeScheduler);

    /**
     * 更新定时任务
     *
     * @param timeScheduler 定时任务Entity {@link TimeSchedulerPo}
     */
    void update(@Param("scheduler") TimeSchedulerPo timeScheduler);

    /**
     * 根据定时任务ID搜索定时任务
     *
     * @param schedulerId 定时任务ID {@link String}
     * @return 定时任务ID对应的定时任务 {@link TimeSchedulerPo}
     */
    TimeSchedulerPo find(@Param("schedulerId") String schedulerId);

    /**
     * 根据定时任务ID删除定时任务
     *
     * @param schedulerId 定时任务ID {@link String}
     */
    void delete(@Param("schedulerId") String schedulerId);

    /**
     * 查询所有定时任务
     *
     * @return 所有的定时任务
     */
    List<TimeSchedulerPo> findAll();

    /**
     * 根据任务数据源ID搜索定时任务
     *
     * @param taskSourceId 任务数据源ID {@link String}
     * @return 任务数据源ID对应的定时任务 {@link TimeSchedulerPo}
     */
    TimeSchedulerPo queryByTaskSourceId(@Param("taskSourceId") String taskSourceId);
}
