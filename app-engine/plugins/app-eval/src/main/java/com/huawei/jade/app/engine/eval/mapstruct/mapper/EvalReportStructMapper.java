/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapstruct.mapper;

import com.huawei.jade.app.engine.eval.po.EvalReportPo;
import com.huawei.jade.app.engine.eval.vo.EvalReportVo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 评估报告相关类转换接口。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Mapper
public interface EvalReportStructMapper {
    /**
     * Mapper实例。
     */
    EvalReportStructMapper INSTANCE = Mappers.getMapper(EvalReportStructMapper.class);

    /**
     * 评估报告 PO 转 VO 的接口。
     *
     * @param evalReportPo 表示评估报告PO的 {@link EvalReportPo}
     * @return 表示评估报告VO的 {@link EvalReportVo}
     */
    EvalReportVo poToVO(EvalReportPo evalReportPo);
}
