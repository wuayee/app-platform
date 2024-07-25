/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.app.engine.eval.dto.EvalDataQueryParam;
import com.huawei.jade.app.engine.eval.entity.EvalDataEntity;
import com.huawei.jade.app.engine.eval.manager.EvalDataValidator;
import com.huawei.jade.app.engine.eval.manager.EvalDatasetVersionManager;
import com.huawei.jade.app.engine.eval.mapper.EvalDataMapper;
import com.huawei.jade.app.engine.eval.po.EvalDataPo;
import com.huawei.jade.app.engine.eval.service.EvalDataService;
import com.huawei.jade.common.vo.PageVo;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示 {@link EvalDataService} 的默认实现。
 *
 * @author 易文渊
 * @since 2024-07-19
 */
@Component
public class EvalDataServiceImpl implements EvalDataService {
    private final EvalDataMapper dataMapper;
    private final EvalDataValidator dataValidator;
    private final EvalDatasetVersionManager versionManager;

    public EvalDataServiceImpl(EvalDataMapper dataMapper, EvalDataValidator dataValidator,
            EvalDatasetVersionManager versionManager) {
        this.dataMapper = dataMapper;
        this.dataValidator = dataValidator;
        this.versionManager = versionManager;
    }

    @Override
    public void insertAll(Long datasetId, List<String> contents) {
        dataValidator.verify(datasetId, contents);
        long version = versionManager.applyVersion();
        List<EvalDataPo> evalDataPoList = contents.stream().map(content -> {
            EvalDataPo evalDataPo = new EvalDataPo();
            evalDataPo.setContent(content);
            evalDataPo.setCreatedVersion(version);
            evalDataPo.setDatasetId(datasetId);
            return evalDataPo;
        }).collect(Collectors.toList());
        this.dataMapper.insertAll(evalDataPoList);
    }

    @Override
    public PageVo<EvalDataEntity> listEvalData(EvalDataQueryParam queryParam) {
        List<EvalDataEntity> evalData = this.dataMapper.listEvalData(queryParam);
        int evalDataCount = this.dataMapper.countEvalData(queryParam);
        return PageVo.of(evalDataCount, evalData);
    }

    @Override
    public void delete(List<Long> dataIds) {
        long version = versionManager.applyVersion();
        List<EvalDataPo> evalDataPoList = dataIds.stream().map(id -> {
            EvalDataPo evalDataPo = new EvalDataPo();
            evalDataPo.setId(id);
            return evalDataPo;
        }).collect(Collectors.toList());
        this.dataMapper.updateExpiredVersion(evalDataPoList, version);
    }

    @Override
    public void update(Long datasetId, String content) {
        dataValidator.verify(datasetId, Collections.singletonList(content));
        long version = versionManager.applyVersion();
        EvalDataPo evalDataPo = new EvalDataPo();
        evalDataPo.setContent(content);
        evalDataPo.setCreatedVersion(version);
        evalDataPo.setExpiredVersion(version);
        evalDataPo.setDatasetId(datasetId);
        int effectRows = this.dataMapper.updateExpiredVersion(Collections.singletonList(evalDataPo), version);
        if (effectRows != 0) {
            this.dataMapper.insertAll(Collections.singletonList(evalDataPo));
        }
    }
}