/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.controller;

import static modelengine.fitframework.inspection.Validation.notNegative;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.common.Result;
import modelengine.jade.store.entity.query.ModelQuery;
import modelengine.jade.store.entity.transfer.ModelData;
import modelengine.jade.store.service.EcoTaskService;
import modelengine.jade.store.service.HuggingFaceModelService;

import java.util.List;

/**
 * 表示模型的 Http 方法的控制器。
 *
 * @author 鲁为
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
     * @param taskName 表示任务唯一标识的 {@link String}。
     * @param pageNum 表示页码的 {@link Integer}。
     * @param pageSize 表示限制的 {@link Integer}。
     * @return 表示查询到的指定模型的信息的 {@link Result}{@code <}{@link ModelData}{@code >}。
     */
    @GetMapping
    public Result<List<ModelData>> getModels(@RequestQuery(value = "taskName", required = false) String taskName,
            @RequestQuery(value = "pageNum", required = false) Integer pageNum,
            @RequestQuery(value = "pageSize", required = false) Integer pageSize) {
        if (pageNum != null) {
            notNegative(pageNum, "The page num cannot be negative. [pageNum={0}]", pageNum);
        }
        if (pageSize != null) {
            notNegative(pageSize, "The page size cannot be negative. [pageSize={0}]", pageSize);
        }
        ModelQuery modelQuery = new ModelQuery(taskName, pageNum, pageSize);
        return Result.ok(this.modelService.getModels(modelQuery), this.modelService.getCount(taskName));
    }
}