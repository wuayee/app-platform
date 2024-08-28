/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service.impl;

import com.huawei.jade.app.engine.task.convertor.EvalCaseConvertor;
import com.huawei.jade.app.engine.task.entity.EvalCaseEntity;
import com.huawei.jade.app.engine.task.entity.EvalRecordEntity;
import com.huawei.jade.app.engine.task.mapper.EvalCaseMapper;
import com.huawei.jade.app.engine.task.po.EvalCasePo;
import com.huawei.jade.app.engine.task.service.EvalCaseService;
import com.huawei.jade.app.engine.task.service.EvalRecordService;
import modelengine.fitframework.annotation.Component;

import java.util.List;

/**
 * 表示 {@link EvalCaseService} 的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Component
public class EvalCaseServiceImpl implements EvalCaseService {
    private final EvalCaseMapper evalCaseMapper;
    private final EvalRecordService evalRecordService;

    public EvalCaseServiceImpl(EvalCaseMapper evalCaseMapper,
            EvalRecordService evalRecordService) {
        this.evalCaseMapper = evalCaseMapper;
        this.evalRecordService = evalRecordService;
    }

    @Override
    public void createEvalCase(EvalCaseEntity entity, List<EvalRecordEntity> records) {
        EvalCasePo evalCasePo = EvalCaseConvertor.INSTANCE.entityToPo(entity);
        this.evalCaseMapper.create(evalCasePo);
        records.forEach(record -> record.setTaskCaseId(evalCasePo.getId()));
        this.evalRecordService.createEvalRecord(records);
    }
}