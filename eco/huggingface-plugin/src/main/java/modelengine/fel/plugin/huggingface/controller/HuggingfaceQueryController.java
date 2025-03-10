/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.controller;

import static modelengine.fel.plugin.huggingface.code.HuggingfacePluginRetCode.MODEL_NOT_FOUND;

import modelengine.fel.plugin.huggingface.dto.HuggingfaceModelQueryParam;
import modelengine.fel.plugin.huggingface.dto.HuggingfaceTaskInfo;
import modelengine.fel.plugin.huggingface.entity.HuggingfaceModelEntity;
import modelengine.fel.plugin.huggingface.service.HuggingfaceModelQueryService;
import modelengine.fel.plugin.huggingface.service.HuggingfaceTaskQueryService;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.validation.Validated;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.common.vo.PageVo;

import java.util.List;

/**
 * 表示 Huggingface 任务数据管理接口集。
 *
 * @author 邱晓霞
 * @since 2024-09-09
 */
@Component
@RequestMapping(path = "/store/huggingface")
public class HuggingfaceQueryController {
    private final HuggingfaceTaskQueryService taskQueryService;
    private final HuggingfaceModelQueryService modelQueryService;

    /**
     * 表示 {@link HuggingfaceQueryController} 的构造器。
     *
     * @param taskQueryService 表示任务查询服务的 {@link HuggingfaceTaskQueryService}。
     * @param modelQueryService 表示模型查询服务的 {@link HuggingfaceModelQueryService}。
     */
    public HuggingfaceQueryController(HuggingfaceTaskQueryService taskQueryService,
            HuggingfaceModelQueryService modelQueryService) {
        this.taskQueryService = taskQueryService;
        this.modelQueryService = modelQueryService;
    }

    /**
     * 获取可用的 Huggingface 任务列表。
     *
     * @return 表示携带 Huggingface 任务数据列表的 {@link List}{@code <}{@link HuggingfaceTaskInfo}{@code >}。
     */
    @GetMapping("/tasks")
    public List<HuggingfaceTaskInfo> getAvailableTasks() {
        return this.taskQueryService.listAvailableTasks();
    }

    /**
     * 获取指定 Huggingface 任务的分页模型数据。
     *
     * @param queryParam 表示查询参数的 {@link HuggingfaceModelQueryParam}。
     * @return 表示 Huggingface 指定任务的模型分页列表的 {@link PageVo}{@code <}{@link HuggingfaceModelEntity}{@code >}。
     */
    @GetMapping("/models")
    public PageVo<HuggingfaceModelEntity> getModels(@RequestBean @Validated HuggingfaceModelQueryParam queryParam) {
        return this.modelQueryService.listModelInfoQuery(queryParam);
    }

    /**
     * 获取指定 Huggingface 任务的默认模型数据。
     *
     * @param taskId 表示查询任务的主键 {@link Long}。
     * @return 表示 Huggingface 指定任务的默认模型的 {@link HuggingfaceModelEntity}。
     */
    @GetMapping("/tasks/default-model")
    public HuggingfaceModelEntity getDefaultModel(@RequestParam("taskId")@Validated Long taskId) {
        PageVo<HuggingfaceModelEntity> pageVo =
                this.modelQueryService.listModelInfoQuery(new HuggingfaceModelQueryParam(taskId, 1, 1));
        List<HuggingfaceModelEntity> list = pageVo.getItems();
        if (list.isEmpty()) {
            throw new ModelEngineException(MODEL_NOT_FOUND, taskId);
        }
        return list.get(0);
    }
}