/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.dao;

import modelengine.fit.jober.taskcenter.dao.po.TaskObject;

import java.util.List;

/**
 * task表的数据访问层的Mapper接口
 *
 * @author 梁致强
 * @since 2023-08-08
 */
public interface TaskMapper {
    /**
     * 创建任务定义实例。
     *
     * @param entity 任务定义Entity的 {@link TaskObject}。
     * @return 表示影响的行数
     */
    int insert(TaskObject entity);

    /**
     * 根据任务定义唯一标识搜索任务定义实例。
     *
     * @param taskId 任务定义唯一标识的 {@link String}。
     * @return 任务定义唯一标识对应任务定义实例的 {@link TaskObject}。
     */
    TaskObject selectById(String taskId);

    /**
     * 根据任务定义唯一标识列表搜索任务定义实例。
     *
     * @param taskIds 任务定义唯一标识的 {@link List<String>}。
     * @return 任务定义唯一标识对应任务定义实例的 {@link List<TaskObject>}。
     */
    List<TaskObject> selectByIds(List<String> taskIds);

    /**
     * 根据任务定义唯一标识删除任务定义实例。
     *
     * @param taskId 任务定义唯一标识的 {@link String}。
     * @return 表示影响的行数
     */
    int delete(String taskId);
}
