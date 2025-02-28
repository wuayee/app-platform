/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.entity;

import static modelengine.jade.carver.util.SerializeUtils.json2obj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.jade.carver.entity.CommonDo;
import modelengine.jade.store.entity.transfer.TaskData;

/**
 * 存入数据库的任务的实体类。
 *
 * @author 鲁为
 * @since 2024-06-06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDo extends CommonDo {
    /**
     * 表示任务的唯一标识。
     */
    private String taskName;

    /**
     * 表示任务的上下文。
     */
    private String context;

    /**
     * 表示工具的唯一标识。
     */
    private String toolUniqueName;

    /**
     * 将 {@link TaskDo} 转换为 {@link TaskData}。
     *
     * @param taskDo 表示任务数据库实体类的 {@link TaskDo}。
     * @param serializer 表示序列化工具的 {@link ObjectSerializer}。
     * @return 返回任务传输层类的 {@link TaskData}。
     */
    public static TaskData convertToTaskData(TaskDo taskDo, ObjectSerializer serializer) {
        TaskData taskData = new TaskData();
        taskData.setTaskName(taskDo.getTaskName());
        taskData.setContext(json2obj(taskDo.getContext(), serializer));
        taskData.setToolUniqueName(taskDo.getToolUniqueName());
        return taskData;
    }
}
