/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service;

import com.huawei.jade.app.engine.eval.dto.EvalDatasetQueryParam;
import com.huawei.jade.app.engine.eval.entity.EvalDatasetEntity;
import com.huawei.jade.common.vo.PageVo;

/**
 * 表示评估数据集服务。
 *
 * @author 何嘉斌
 * @since 2024-07-31
 */
public interface EvalDatasetService {
    /**
     * 插入评估数据集。
     *
     * @param entity 评估数据集业务对象的 {@link EvalDatasetEntity}。
     */
    void create(EvalDatasetEntity entity);

    /**
     * 分页查询评估数据集元数据。
     *
     * @param queryParam 表示评估数据集查询参数的 {@link EvalDatasetQueryParam}。
     * @return 表示评估数据集元数据查询结果的 {@link PageVo}{@code <}{@link EvalDatasetEntity}{@code >}。
     */
    PageVo<EvalDatasetEntity> listEvalDataset(EvalDatasetQueryParam queryParam);

    /**
     * 通过 ID 查询评估数据集元数据。
     *
     * @param datasetId 表示评估数据集唯一标识的 {@link Long}。
     * @return 表示评估数据集元数据查询结果的 {@link EvalDatasetEntity}。
     */
    EvalDatasetEntity getEvalDatasetById(Long datasetId);

    /**
     * 更新评估数据集信息。
     *
     * @param updateEntity 表示新的评估数据集信息的 {@link EvalDatasetEntity}。
     */
    void updateEvalDataset(EvalDatasetEntity updateEntity);
}
