/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.controller;

import static com.huawei.fitframework.inspection.Validation.notNegative;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestQuery;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.value.Result;
import com.huawei.jade.store.entity.query.ModelQuery;
import com.huawei.jade.store.entity.transfer.ModelData;
import com.huawei.jade.store.service.EcoTaskService;
import com.huawei.jade.store.service.HuggingFaceModelService;

import java.util.List;

/**
 * 表示模型的 Http 方法的控制器。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-07
 */
@Component
@RequestMapping("/models")
public class ModelController {
    private final HuggingFaceModelService modelService;

    /**
     * 通过模型服务来初始化 {@link ModelController} 的新实例。
     *
     * @param modelService 表示任务服务的 {@link EcoTaskService}。
     */
    public ModelController(HuggingFaceModelService modelService) {
        this.modelService = notNull(modelService, "The model service cannot be null.");
    }

    /**
     * 根据动态查询条件准确获取模型列表。
     *
     * @param taskId 表示任务唯一标识的 {@link String}。
     * @param pageNum 表示页码的 {@link Integer}。
     * @param pageSize 表示限制的 {@link Integer}。
     * @return 表示查询到的指定模型的信息的 {@link Result}{@code <}{@link ModelData}{@code >}。
     */
    @GetMapping
    public Result<List<ModelData>> getModels(@RequestQuery(value = "taskId", required = false) String taskId,
            @RequestQuery(value = "pageNum", required = false) Integer pageNum,
            @RequestQuery(value = "pageSize", required = false) Integer pageSize) {
        if (pageNum != null) {
            notNegative(pageNum, "The page num cannot be negative. [pageNum={0}]", pageNum);
        }
        if (pageSize != null) {
            notNegative(pageSize, "The page size cannot be negative. [pageSize={0}]", pageSize);
        }
        ModelQuery modelQuery = new ModelQuery(taskId, pageNum, pageSize);
        return Result.create(this.modelService.getModels(modelQuery), 0);
    }
}