/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapper;

import com.huawei.jade.app.engine.eval.dto.EvalDataQueryParam;
import com.huawei.jade.app.engine.eval.entity.EvalDataEntity;
import com.huawei.jade.app.engine.eval.po.EvalDataPo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    /**
     * 查询评估数据。
     *
     * @param queryParam 表示评估数据查询参数的 {@code EvalDataQueryParam}。
     * @return 表示评估数据查询结果的 {@link List}{@code <}{@link EvalDataEntity}{@code >}。
     */
    List<EvalDataEntity> listEvalData(EvalDataQueryParam queryParam);

    /**
     * 统计评估数据数量。
     *
     * @param queryParam 表示评估数据查询参数的 {@code EvalDataQueryParam}。
     * @return 表示评估数据查询结果的 {@code int}。
     */
    int countEvalData(EvalDataQueryParam queryParam);

    /**
     * 批量软删除评估数据，更新数据到期时间。
     *
     * @param evalDataList 表示评估数据列表的 {@link List}{@code <}{@link EvalDataPo}{@code >}。
     * @param expiredVersion 表示数据到期版本 {@link Long}。
     * @return 表示成功修改的行数 {@code int}。
     */
    int updateExpiredVersion(@Param("list") List<EvalDataPo> evalDataList, @Param("version") Long expiredVersion);
}