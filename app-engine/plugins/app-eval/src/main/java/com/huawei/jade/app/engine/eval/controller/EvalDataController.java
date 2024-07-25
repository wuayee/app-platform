/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.controller;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.PutMapping;
import com.huawei.fit.http.annotation.RequestBean;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.validation.Validated;
import com.huawei.jade.app.engine.eval.dto.EvalDataCreateDto;
import com.huawei.jade.app.engine.eval.dto.EvalDataDeleteDto;
import com.huawei.jade.app.engine.eval.dto.EvalDataUpdateDto;
import com.huawei.jade.app.engine.eval.dto.EvalDataQueryParam;
import com.huawei.jade.app.engine.eval.entity.EvalDataEntity;
import com.huawei.jade.app.engine.eval.service.EvalDataService;
import com.huawei.jade.common.vo.PageVo;

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
     * 查询评估数据。
     *
     * @param queryParam 表示查询参数的 {@link EvalDataQueryParam}。
     * @return 表示评估数据查询结果的 {@link PageVo}{@code <}{@link EvalDataEntity}{@code >}。
     */
    @GetMapping(description = "查询评估数据")
    public PageVo<EvalDataEntity> queryEvalData(@RequestBean EvalDataQueryParam queryParam) {
        return evalDataService.listEvalData(queryParam);
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

    /**
     * 修改评估数据。
     *
     * @param updateDto 表示评估数据修改传输对象的 {@link EvalDataUpdateDto}。
     */
    @PutMapping(description = "修改评估数据")
    public void updateEvalData(@RequestBody @Validated EvalDataUpdateDto updateDto) {
        this.evalDataService.update(updateDto.getDataId(), updateDto.getContent());
    }
}