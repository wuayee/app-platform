/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service;

import com.huawei.jade.app.engine.eval.dto.EvalDataQueryParam;
import com.huawei.jade.app.engine.eval.entity.EvalDataEntity;
import com.huawei.jade.common.vo.PageVo;

import java.util.List;

/**
 * 表示评估数据服务。
 *
 * @author 易文渊
 * @since 2024-07-19
 */
public interface EvalDataService {
    /**
     * 批量插入评估数据。
     *
     * @param datasetId 表示评估数据集编号的 {@link Long}。
     * @param contents 表示评估内容集合的 {@link List}{@code <}{@link String}{@code >}。
     */
    void insertAll(Long datasetId, List<String> contents);

    /**
     * 分页查询数据。
     *
     * @param queryParam 表示查询相关参数的 {@link EvalDataQueryParam}。
     * @return 表示评估数据查询结果的 {@link PageVo}{@code <}{@link EvalDataEntity}{@code >}。
     */
    PageVo<EvalDataEntity> listEvalData(EvalDataQueryParam queryParam);

    /**
     * 批量软删除评估数据。
     *
     * @param dataIds 表示评估数据编号的 {@link List}{@code <}{@link Long}{@code >}。
     */
    void delete(List<Long> dataIds);
}