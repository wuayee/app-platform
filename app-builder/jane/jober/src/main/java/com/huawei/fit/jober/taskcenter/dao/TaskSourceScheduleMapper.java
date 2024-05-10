/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.dao;

import com.huawei.fit.jober.taskcenter.dao.po.SourceObject;
import com.huawei.fit.jober.taskcenter.dao.po.TaskSourceScheduleObject;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 为定时任务数据源提供持久化能力。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-14
 */
@Mapper
public interface TaskSourceScheduleMapper {
    /**
     * 创建定时任务同步数据的数据源。
     *
     * @param taskSourceScheduleObject 表示定时任务同步数据的数据源ORM数据对象的 {@link TaskSourceScheduleObject}。
     * @return 表示创建操作影响行数的 {@link Integer}。
     */
    Integer insert(TaskSourceScheduleObject taskSourceScheduleObject);

    /**
     * 删除定时任务同步数据的数据源。
     *
     * @param sourceId 表示待删除的定时任务同步数据的数据源的唯一标识的 {@link String}。
     * @return 表示删除操作影响行数的 {@link Integer}。
     */
    Integer delete(String sourceId);

    /**
     * 检索定时任务同步数据的数据源。
     *
     * @param sourceId 表示待检索的定时任务同步数据的数据源的唯一标识的 {@link String}。
     * @return 表示该定时任务同步数据的数据源的 {@link SourceObject}。
     */
    TaskSourceScheduleObject select(String sourceId);

    /**
     * 列出指定定时任务同步数据的数据源。
     *
     * @param sourceIds 表示待查询的定时任务同步数据的数据源所在的任务定义的唯一标识的 {@link String}{@code <}{@link String}{@code >}。
     * @return 表示该定时任务同步数据的的数据源列表的 {@link List}{@code <}{@link TaskSourceScheduleObject}{@code >}。
     */
    List<TaskSourceScheduleObject> selectBySourceIds(List<String> sourceIds);

    /**
     * 批量保存定时任务同步数据的数据源。
     *
     * @param taskSourceScheduleObjects 表示待保存的定时任务同步的数据源的
     * {@link List}{@code <}{@link TaskSourceScheduleObject}{@code >}。
     */
    void batchInsert(List<TaskSourceScheduleObject> taskSourceScheduleObjects);

    /**
     * 批量删除定时任务同步数据的数据源。
     *
     * @param ids 表示待删除的定时任务同步的数据源唯一标识的 {@link List}{@code <}{@link String}{@code >}。
     */
    void batchDelete(List<String> ids);
}
