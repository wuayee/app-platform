/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service.impl;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.transaction.Transactional;
import com.huawei.jade.app.engine.eval.code.AppEvalRetCode;
import com.huawei.jade.app.engine.eval.dto.EvalDataQueryParam;
import com.huawei.jade.app.engine.eval.entity.EvalDataEntity;
import com.huawei.jade.app.engine.eval.exception.AppEvalException;
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

    /**
     * 表示评估数据服务实现的构建器。
     *
     * @param dataMapper 表示评估数据持久层接口的 {@link EvalDataMapper}。
     * @param dataValidator 评估数据校验器的 {@link EvalDataValidator}。
     * @param versionManager 表示评估数据集版本管理器的 {@link EvalDatasetVersionManager}。
     */
    public EvalDataServiceImpl(EvalDataMapper dataMapper, EvalDataValidator dataValidator,
            EvalDatasetVersionManager versionManager) {
        this.dataMapper = dataMapper;
        this.dataValidator = dataValidator;
        this.versionManager = versionManager;
    }

    @Override
    public void insertAll(Long datasetId, List<String> contents) {
        this.dataValidator.verify(datasetId, contents);
        long version = this.versionManager.applyVersion();
        insert(datasetId, contents, version);
    }

    @Override
    public void delete(List<Long> dataIds) {
        long version = this.versionManager.applyVersion();
        softDelete(dataIds, version);
    }

    @Override
    @Transactional
    public void update(Long datasetId, Long dataId, String content) throws AppEvalException {
        this.dataValidator.verify(datasetId, content);
        long version = this.versionManager.applyVersion();
        int effectRows = softDelete(Collections.singletonList(dataId), version);
        if (effectRows == 0) {
            throw new AppEvalException(AppEvalRetCode.EVAL_DATA_DELETED_ERROR, dataId);
        }
        insert(datasetId, Collections.singletonList(content), version);
    }

    @Override
    public PageVo<EvalDataEntity> listEvalData(EvalDataQueryParam queryParam) {
        List<EvalDataEntity> evalData = this.dataMapper.listEvalData(queryParam);
        int evalDataCount = this.dataMapper.countEvalData(queryParam);
        return PageVo.of(evalDataCount, evalData);
    }

    @Override
    public void hardDelete(List<Long> datasetIds) {
        this.dataMapper.deleteAll(datasetIds);
    }

    private void insert(Long datasetId, List<String> contents, long createdVersion) {
        List<EvalDataPo> evalDataPoList = contents.stream().map(content -> {
            EvalDataPo evalDataPo = new EvalDataPo();
            evalDataPo.setContent(content);
            evalDataPo.setCreatedVersion(createdVersion);
            evalDataPo.setDatasetId(datasetId);
            return evalDataPo;
        }).collect(Collectors.toList());
        this.dataMapper.insertAll(evalDataPoList);
    }

    private int softDelete(List<Long> dataIds, long expiredVersion) {
        List<EvalDataPo> evalDataPoList = dataIds.stream().map(id -> {
            EvalDataPo evalDataPo = new EvalDataPo();
            evalDataPo.setId(id);
            return evalDataPo;
        }).collect(Collectors.toList());
        return this.dataMapper.updateExpiredVersion(evalDataPoList, expiredVersion);
    }
}