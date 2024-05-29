/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapstruct.mapper;

import com.huawei.jade.app.engine.eval.po.EvalReportTracePo;
import com.huawei.jade.app.engine.eval.vo.EvalReportTraceVo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 评估报告调用轨迹相关类转换接口。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Mapper
public interface EvalReportTraceStructMapper {
    /**
     * Mapper实例。
     */
    EvalReportTraceStructMapper INSTANCE = Mappers.getMapper(EvalReportTraceStructMapper.class);

    /**
     * 评估报告调用轨迹 PO 转 VO 的接口。
     *
     * @param evalReportTracePo 表示评估报告PO的 {@link EvalReportTracePo}
     * @return 表示评估报告VO的 {@link EvalReportTraceVo}
     */
    EvalReportTraceVo poToVO(EvalReportTracePo evalReportTracePo);
}
