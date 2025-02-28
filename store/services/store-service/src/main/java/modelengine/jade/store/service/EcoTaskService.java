/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.service;

import modelengine.fitframework.annotation.Genericable;
import modelengine.jade.store.entity.query.TaskQuery;
import modelengine.jade.store.entity.transfer.TaskData;

import java.util.List;

/**
 * 任务的服务接口类。
 *
 * @author 鲁为
 * @since 2024-06-06
 */
public interface EcoTaskService {
    /**
     * 查询任务。
     *
     * @param taskId 表示任务的唯一标识的 {@link String}。
     * @return 表示查询到的任务的 {@link TaskData}。
     */
    @Genericable(id = "modelengine.jade.store.task.getTask")
    TaskData getTask(String taskId);

    /**
     * 根据动态条件准确查询工具列表。
     *
     * @param taskQuery 表示动态查询条件的 {@link TaskQuery}
     * @return 表示工具列表的 {@link List}{@code <}{@link TaskData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.store.task.getTasks.byTaskQuery")
    List<TaskData> getTasks(TaskQuery taskQuery);
}
