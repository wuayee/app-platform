/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.app.engine.task.convertor.EvalRecordConvertor;
import com.huawei.jade.app.engine.task.entity.EvalRecordEntity;
import com.huawei.jade.app.engine.task.mapper.EvalRecordMapper;
import com.huawei.jade.app.engine.task.po.EvalRecordPo;
import com.huawei.jade.app.engine.task.service.EvalRecordService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示 {@link EvalRecordService} 的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Component
public class EvalRecordServiceImpl implements EvalRecordService {
    private final EvalRecordMapper evalRecordMapper;

    public EvalRecordServiceImpl(EvalRecordMapper evalRecordMapper) {
        this.evalRecordMapper = evalRecordMapper;
    }

    @Override
    public void createEvalRecord(List<EvalRecordEntity> result) {
        List<EvalRecordPo> resultPo =
                result.stream().map(EvalRecordConvertor.INSTANCE::entityToPo).collect(Collectors.toList());
        this.evalRecordMapper.create(resultPo);
    }
}