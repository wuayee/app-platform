/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapper;

import com.huawei.jade.app.engine.eval.po.EvalDataPo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 表示评估数据持久层接口。
 *
 * @author 易文渊
 * @since 2024-07-19
 */
@Mapper
public interface EvalDataMapper {
    /**
     * 批量插入评估数据。
     *
     * @param evalDataList 表示评估数据列表的 {@link List}{@code <}{@link EvalDataPo}{@code >}。
     */
    void insertAll(List<EvalDataPo> evalDataList);
}