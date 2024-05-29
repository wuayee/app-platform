/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapstruct.mapper;

import com.huawei.jade.app.engine.eval.dto.EvalTaskDto;
import com.huawei.jade.app.engine.eval.po.EvalTaskPo;
import com.huawei.jade.app.engine.eval.vo.EvalTaskVo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 评估任务相关类转换接口。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Mapper
public interface EvalTaskStructMapper {
    /**
     * Mapper实例。
     */
    EvalTaskStructMapper INSTANCE = Mappers.getMapper(EvalTaskStructMapper.class);

    /**
     * 评估报告 DTO 转 PO 的接口。
     *
     * @param evalTaskDto 表示评估报告DTO的 {@link EvalTaskDto}
     * @return 表示评估报告PO的 {@link EvalTaskPo}
     */
    EvalTaskPo dtoToPO(EvalTaskDto evalTaskDto);

    /**
     * 评估报告 PO 转 VO 的接口。
     *
     * @param evalTaskPo 表示评估报告PO的 {@link EvalTaskPo}
     * @return 表示评估报告VO的 {@link EvalTaskVo}
     */
    EvalTaskVo poToVO(EvalTaskPo evalTaskPo);
}
