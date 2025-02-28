/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.mapper;

import modelengine.jade.store.entity.query.TaskQuery;
import modelengine.jade.store.repository.pgsql.entity.TaskDo;

import java.util.List;

/**
 * 表示用于 MyBatis 持久层引用的 Task 接口。
 *
 * @author 鲁为
 * @since 2024-06-06
 */
public interface TaskMapper {
    /**
     * 根据任务唯一标识获取任务。
     *
     * @param taskName 表示任务唯一标识的 {@link String}。
     * @return 所有任务的实体类的实例的 {@link TaskDo}。
     */
    TaskDo getTask(String taskName);

    /**
     * 根据工具的唯一标识分页查询任务。
     *
     * @param taskQuery 表示查询参数的实体类的 {@link TaskQuery}。
     * @return 所有任务的实体类的实例的 {@link TaskDo}。
     */
    List<TaskDo> getTasks(TaskQuery taskQuery);
}
