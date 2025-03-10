/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.controller;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNegative;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.common.Result;
import modelengine.jade.store.entity.query.TaskQuery;
import modelengine.jade.store.entity.transfer.TaskData;
import modelengine.jade.store.service.EcoTaskService;

import java.util.List;

/**
 * 表示任务的 Http 方法的控制器。
 *
 * @author 鲁为
 * @since 2024-06-06
 */
@Component
@RequestMapping("/tasks")
public class TaskController {
    private final EcoTaskService taskService;

    /**
     * 通过任务服务来初始化 {@link TaskController} 的新实例。
     *
     * @param taskService 表示任务服务的 {@link EcoTaskService}。
     */
    public TaskController(EcoTaskService taskService) {
        this.taskService = notNull(taskService, "The task service cannot be null.");
    }

    /**
     * 基于任务的唯一标识查询某个任务。
     *
     * @param taskId 表示任务的唯一标识的 {@link String}。
     * @return 表示查询到的指定任务的信息的 {@link Result}{@code <}{@link TaskData}{@code >}。
     */
    @GetMapping("/{taskId}")
    public Result<TaskData> getTask(@PathVariable("taskId") String taskId) {
        notBlank(taskId, "The tool unique name cannot be blank.");
        return Result.ok(this.taskService.getTask(taskId), 1);
    }

    /**
     * 根据动态查询条件准确获取工具列表。
     *
     * @param toolUniqueName 表示工具名的 {@link String}。
     * @param pageNum 表示页码的 {@link Integer}。
     * @param pageSize 表示限制的 {@link Integer}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link TaskData}{@code >}{@code >}。
     */
    @GetMapping
    public Result<List<TaskData>> getTasks(
            @RequestQuery(value = "toolUniqueName", required = false) String toolUniqueName,
            @RequestQuery(value = "pageNum", required = false) Integer pageNum,
            @RequestQuery(value = "pageSize", required = false) Integer pageSize) {
        if (pageNum != null) {
            notNegative(pageNum, "The page num cannot be negative. [pageNum={0}]", pageNum);
        }
        if (pageSize != null) {
            notNegative(pageSize, "The page size cannot be negative. [pageSize={0}]", pageSize);
        }
        TaskQuery taskQuery = new TaskQuery(toolUniqueName, pageNum, pageSize);
        return Result.ok(this.taskService.getTasks(taskQuery), 1);
    }
}