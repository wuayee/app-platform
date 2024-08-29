/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service.impl;

import com.huawei.jade.app.engine.task.convertor.EvalReportConvertor;
import com.huawei.jade.app.engine.task.entity.EvalReportEntity;
import com.huawei.jade.app.engine.task.mapper.EvalReportMapper;
import com.huawei.jade.app.engine.task.po.EvalReportPo;
import com.huawei.jade.app.engine.task.service.EvalReportService;

import modelengine.fitframework.annotation.Component;

/**
 * 表示 {@link EvalReportService} 的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
@Component
public class EvalReportServiceImpl implements EvalReportService {
    private final EvalReportMapper reportMapper;

    public EvalReportServiceImpl(EvalReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    @Override
    public void createEvalReport(EvalReportEntity entity) {
        EvalReportPo reportPo = EvalReportConvertor.INSTANCE.entityToPo(entity);
        this.reportMapper.create(reportPo);
    }
}