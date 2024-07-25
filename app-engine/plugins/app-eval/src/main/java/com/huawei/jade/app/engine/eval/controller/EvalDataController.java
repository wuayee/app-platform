/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.controller;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.validation.Validated;
import com.huawei.jade.app.engine.eval.dto.EvalDataCreateDto;
import com.huawei.jade.app.engine.eval.dto.EvalDataDeleteDto;
import com.huawei.jade.app.engine.eval.service.EvalDataService;

/**
 * 表示评估数据管理接口集。
 *
 * @author 易文渊
 * @since 2024-07-19
 */
@Component
@RequestMapping(path = "/eval/data", group = "评估数据管理接口")
public class EvalDataController {
    private final EvalDataService evalDataService;

    /**
     * 评估数据管理控制器构造函数。
     *
     * @param evalDataService 表示评估数据服务的 {@link EvalDataService}。
     */
    public EvalDataController(EvalDataService evalDataService) {
        this.evalDataService = evalDataService;
    }

    /**
     * 批量创建评估数据。
     *
     * @param createDto 表示评估数据集创建传输对象的 {@link EvalDataCreateDto}。
     */
    @PostMapping(description = "批量创建评估数据")
    public void createEvalData(@RequestBody @Validated EvalDataCreateDto createDto) {
        this.evalDataService.insertAll(createDto.getDatasetId(), createDto.getContents());
    }

    /**
     * 批量软删除评估数据。
     *
     * @param deleteDto 表示评估数据集软删除传输对象的 {@link EvalDataDeleteDto}。
     */
    @DeleteMapping(description = "批量软删除评估数据")
    public void deleteEvalData(@RequestBody @Validated EvalDataDeleteDto deleteDto) {
        this.evalDataService.delete(deleteDto.getDataIds());
    }
}