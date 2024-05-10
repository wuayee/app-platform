/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.dao;

import com.huawei.fit.jober.taskcenter.dao.po.TriggerObject;
import com.huawei.fit.jober.taskcenter.domain.TriggerEntity;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 为任务属性触发器提供持久化能力。
 *
 * @author w30020313
 * @since 2023-08-08
 */
@Mapper
public interface TriggerMapper {
    /**
     * 插入的Trigger信息
     *
     * @param triggerObject triggerObject
     */
    void create(TriggerObject triggerObject);

    /**
     * 获取需要插入的Trigger信息
     *
     * @param taskId taskId
     * @param sourceId sourceId
     * @param propertyName propertyName
     * @return Trigger信息
     */
    TriggerObject selectTriggerByTaskId(@Param("taskId") String taskId, @Param("sourceId") String sourceId,
            @Param("propertyName") String propertyName);

    /**
     * 获取Trigger信息
     *
     * @param triggerId triggerId
     * @return data
     */
    TriggerEntity retrieve(@Param("triggerId") String triggerId);

    /**
     * 根据过滤条件删除任务trigger
     *
     * @param ids ids
     * @param sourceIds sourceIds
     * @param fitableIds fitableIds
     * @param propertyIds propertyIds
     */
    void delete(@Param("ids") List<String> ids, @Param("sourceIds") List<String> sourceIds,
            @Param("propertyIds") List<String> propertyIds, @Param("fitableIds") List<String> fitableIds);

    /**
     * 根据过滤条件查询任务trigger
     *
     * @param ids ids
     * @param sourceIds sourceIds
     * @param fitableIds fitableIds
     * @param propertyIds propertyIds
     * @return data
     */
    List<TriggerObject> list(@Param("ids") List<String> ids, @Param("sourceIds") List<String> sourceIds,
            @Param("propertyIds") List<String> propertyIds, @Param("fitableIds") List<String> fitableIds);

    /**
     * 批量添加任务属性触发器根据taskId
     *
     * @param declarations declarations
     * @return taskId
     */
    List<String> batchSave(List<TriggerObject> declarations);

    /**
     * 提供给batchInsert使用的批量删除任务Trigger。
     *
     * @param taskSourceIds 表示需要删除的任务Trigger所属任务来源唯一标识的 {@link List}{@code <}{@link String}{@code >}。
     * @param triggerIds 表示不需要删除的的任务Trigger唯一标识的 {@link List}{@code <}{@link String}{@code >}。
     */
    void batchDelete(List<String> taskSourceIds, List<String> triggerIds);

    /**
     * 获取taskPropertyId
     *
     * @param taskId taskId
     * @param propertyName propertyName
     * @return string
     */
    String selectTaskPropertyIdByTaskIdAndName(@Param("taskId") String taskId,
            @Param("propertyName") String propertyName);
}