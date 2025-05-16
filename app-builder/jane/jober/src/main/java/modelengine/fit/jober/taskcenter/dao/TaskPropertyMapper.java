/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.dao;

import modelengine.fit.jober.taskcenter.dao.po.TaskPropertyObject;

import java.util.List;
import java.util.Set;

/**
 * (TaskProperty)Dao
 *
 * @author 董建华
 * @since 2023 -08-09
 */
public interface TaskPropertyMapper {
    /**
     * 通过主键查询单条数据
     *
     * @param id id
     * @return 实例对象
     */
    TaskPropertyObject queryById(String id);

    /**
     * Query by task id list.
     *
     * @param taskIds the task id list.
     * @return the list
     */
    List<TaskPropertyObject> queryByTaskIds(List<String> taskIds);

    /**
     * 插入一条数据
     *
     * @param toInsert 实例对象
     * @return 影响行数
     */
    int insert(TaskPropertyObject toInsert);

    /**
     * 批量插入多条数据
     *
     * @param entities 实例对象列表
     * @return 保存的任务数据属性唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> batchInsert(List<TaskPropertyObject> entities);

    /**
     * 提供给batchInsert使用的批量删除任务数据属性。
     *
     * @param taskIds 表示需要删除的任务属性所属任务定义唯一标识的 {@link List}{@code <}{@link String}{@code >}。
     * @param propertyIds 表示不需要删除的的任务属性唯一标识的 {@link List}{@code <}{@link String}{@code >}。
     */
    void batchDelete(List<String> taskIds, List<String> propertyIds);

    /**
     * 通过主键删除一条数据
     *
     * @param id id
     * @return 影响行数
     */
    int deleteById(String id);

    /**
     * 通过task_id批量删除
     *
     * @param taskIds the task ids
     * @return 影响行数 int
     */
    int deleteByTaskIds(Set<String> taskIds);

    /**
     * 通过主键集合删除多条数据
     *
     * @param ids 主键集合
     * @return 影响行数
     */
    int deleteByIds(Set<String> ids);
}

