/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.controller;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNegative;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestQuery;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.value.Result;
import com.huawei.jade.store.entity.query.TaskQuery;
import com.huawei.jade.store.entity.transfer.TaskData;
import com.huawei.jade.store.service.TaskService;

import java.util.List;

/**
 * 表示任务的 Http 方法的控制器。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-06
 */
@Component
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    /**
     * 通过任务服务来初始化 {@link TaskController} 的新实例。
     *
     * @param taskService 表示任务服务的 {@link TaskService}。
     */
    public TaskController(TaskService taskService) {
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
        return Result.createResult(this.taskService.getTask(taskId), 0);
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
        TaskQuery toolTagQuery =
                new TaskQuery(toolUniqueName, pageNum, pageSize);
        return Result.createResult(this.taskService.getTasks(toolTagQuery), 0);
    }
}