/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.jade.app.engine.eval.convertor.EvalDatasetConvertor;
import com.huawei.jade.app.engine.eval.entity.EvalDatasetEntity;
import com.huawei.jade.app.engine.eval.mapper.EvalDatasetMapper;
import com.huawei.jade.app.engine.eval.po.EvalDatasetPo;
import com.huawei.jade.app.engine.eval.service.EvalDataService;
import com.huawei.jade.app.engine.eval.service.EvalDatasetService;

/**
 * 表示 {@link EvalDatasetService} 的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-07-31
 */
@Component
public class EvalDatasetServiceImpl implements EvalDatasetService {
    private final EvalDatasetMapper datasetMapper;
    private final EvalDataService dataService;

    /**
     * 表示评估数据集服务实现的构建器。
     *
     * @param datasetMapper 表示评估数据集持久层接口的 {@link EvalDatasetMapper}。
     * @param dataService 表示评估数据集评估数据服务的 {@link EvalDataService}。
     */
    public EvalDatasetServiceImpl(EvalDatasetMapper datasetMapper, EvalDataService dataService) {
        this.datasetMapper = datasetMapper;
        this.dataService = dataService;
    }

    @Override
    @Transactional
    public void create(EvalDatasetEntity entity) {
        EvalDatasetPo evalDatasetPo = EvalDatasetConvertor.INSTANCE.entityToPo(entity);
        this.datasetMapper.create(evalDatasetPo);
        this.dataService.insertAll(evalDatasetPo.getId(), entity.getContents());
    }
}
