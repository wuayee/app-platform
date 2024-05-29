/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapper;

import com.huawei.jade.app.engine.eval.po.EvalReportTracePo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 评估报告调用轨迹相关的db接口。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Mapper
public interface EvalReportTraceMapper {
    /**
     * 插入一条调用轨迹。
     *
     * @param evalReportTracePo 表示调用轨迹的实体类的 {@link EvalReportTracePo}。
     * @return 表示插入成功的条目 {@link Long}。
     */
    long insert(EvalReportTracePo evalReportTracePo);

    /**
     * 批量插入调用轨迹。
     *
     * @param evalReportTracePoList 表示要插入的调用轨迹实体类列表的 {@link List}{@code <}{@link EvalReportTracePo}{@code >}。
     * @return 表示插入成功的条目 {@link Long}。
     */
    long insertAll(List<EvalReportTracePo> evalReportTracePoList);

    /**
     * 通过实例id获取调用轨迹列表。
     *
     * @param instanceId 表示实例id的 {@link String}
     * @return 表示调用轨迹列表的 {@link List}{@code <}{@link EvalReportTracePo}{@code >}
     */
    List<EvalReportTracePo> getByEvalInstanceId(String instanceId);
}
