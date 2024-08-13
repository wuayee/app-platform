/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.dao;

import com.huawei.fit.jober.taskcenter.dao.po.SourceObject;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 为任务数据源提供持久化能力。
 *
 * @author 陈镕希
 * @since 2023-08-08
 */
@Mapper
public interface SourceMapper {
    /**
     * 创建任务数据源。
     *
     * @param sourceObject 表示任务数据源ORM数据对象的 {@link SourceObject}。
     * @return 表示创建操作影响行数的 {@link Integer}。
     */
    Integer insert(SourceObject sourceObject);

    /**
     * 删除指定任务数据源。
     *
     * @param sourceId 表示待删除的任务数据源的唯一标识的 {@link String}。
     * @return 表示删除操作影响行数的 {@link Integer}。
     */
    Integer delete(String sourceId);

    /**
     * 检索指定任务数据源。
     *
     * @param sourceId 表示待检索的任务数据源的唯一标识的 {@link String}。
     * @return 表示该任务数据源的 {@link SourceObject}。
     */
    SourceObject select(String sourceId);

    /**
     * 列出指定任务定义的数据源。
     *
     * @param taskId 表示待查询的任务数据源所在的任务定义的唯一标识的 {@link String}。
     * @return 表示该任务的数据源列表的 {@link List}{@code <}{@link SourceObject}{@code >}。
     */
    List<SourceObject> selectByTaskId(String taskId);

    /**
     * 列出指定任务定义的数据源。
     *
     * @param taskIds 表示待查询的任务数据源所在的任务定义的唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示相关任务的数据源列表的 {@link List}{@code <}{@link SourceObject}{@code >}。
     */
    List<SourceObject> selectByTaskIds(List<String> taskIds);

    /**
     * 列出指定数据源。
     *
     * @param sourceIds 表示待查询的任务数据源所在的唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param offset 表示待查询的结果集在所有符合条件的结果集中的偏移量的 64 位整数。
     * @param limit 表示待查询的结果集中允许包含结果的最大数量的 32 位整数。
     * @return 表示相关任务的数据源列表的 {@link List}{@code <}{@link SourceObject}{@code >}。
     */
    List<SourceObject> selectBySourceIds(List<String> sourceIds, long offset, int limit);

    /**
     * 批量保存任务数据源。
     *
     * @param sourceObjects 表示待保存的任务数据源的 {@link List}{@code <}{@link SourceObject}{@code >}。
     * @return 保存的任务数据源唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> batchInsert(List<SourceObject> sourceObjects);

    /**
     * 提供给batchInsert使用的批量删除任务数据源。
     *
     * @param taskIds 表示需要删除的任务数据源所属任务定义唯一标识的 {@link List}{@code <}{@link String}{@code >}。
     * @param sourceIds 表示不需要删除的的任务数据源唯一标识的 {@link List}{@code <}{@link String}{@code >}。
     * @return 被删除的数据源ORM数据对象列表的 {@link List}{@code <}{@link SourceObject}{@code >}。
     */
    List<SourceObject> batchDelete(List<String> taskIds, List<String> sourceIds);
}
