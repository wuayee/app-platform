/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.manager.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.app.engine.eval.entity.EvalVersionEntity;
import com.huawei.jade.app.engine.eval.manager.EvalDatasetVersionManager;
import com.huawei.jade.app.engine.eval.mapper.EvalDataMapper;
import com.huawei.jade.app.engine.uid.UidGenerator;

import java.util.List;

/**
 * 表示 {@link EvalDatasetVersionManager} 的默认实现。
 *
 * @author 易文渊
 * @author 何嘉斌
 * @since 2024-07-20
 */
@Component
public class EvalVersionManagerImpl implements EvalDatasetVersionManager {
    private final UidGenerator versionGenerator;
    private final EvalDataMapper dataMapper;

    /**
     * 表示评估数据集版本管理器实现的构建器。
     *
     * @param dataMapper 表示评估数据持久层接口的 {@link EvalDataMapper}。
     * @param generator 表示版本生成器持久层接口的 {@link UidGenerator}。
     */
    public EvalVersionManagerImpl(EvalDataMapper dataMapper, UidGenerator generator) {
        this.dataMapper = dataMapper;
        this.versionGenerator = generator;
    }

    @Override
    public long applyVersion() {
        return this.versionGenerator.getUid();
    }

    @Override
    public List<EvalVersionEntity> getAllVersion(Long datasetId) {
        return this.dataMapper.getAllVersion(datasetId);
    }
}