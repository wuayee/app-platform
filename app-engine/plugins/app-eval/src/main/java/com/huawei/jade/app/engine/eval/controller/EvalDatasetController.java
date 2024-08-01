/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.controller;

import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.validation.Validated;
import com.huawei.jade.app.engine.eval.convertor.EvalDatasetConvertor;
import com.huawei.jade.app.engine.eval.dto.EvalDatasetCreateDto;
import com.huawei.jade.app.engine.eval.entity.EvalDatasetEntity;
import com.huawei.jade.app.engine.eval.service.EvalDatasetService;

/**
 * 表示评估数据集管理接口集。
 *
 * @author 何嘉斌
 * @since 2024-07-31
 */
@Component
@RequestMapping(path = "/eval/dataset", group = "评估数据集管理接口")
public class EvalDatasetController {
    private final EvalDatasetService evaldatasetService;

    /**
     * 评估数据集管理控制器构造函数。
     *
     * @param evalDatasetService 表示评估数据服务的 {@link EvalDatasetService}。
     */
    public EvalDatasetController(EvalDatasetService evalDatasetService) {
        this.evaldatasetService = evalDatasetService;
    }

    /**
     * 创建评估数据集。
     *
     * @param createDto 表示评估数据集创建传输对象的 {@link EvalDatasetCreateDto}。
     */
    @PostMapping(description = "创建评估数据集")
    public void createEvalDataset(@RequestBody @Validated EvalDatasetCreateDto createDto) {
        EvalDatasetEntity bo = EvalDatasetConvertor.INSTANCE.createDtoToBo(createDto);
        this.evaldatasetService.create(bo);
    }
}
