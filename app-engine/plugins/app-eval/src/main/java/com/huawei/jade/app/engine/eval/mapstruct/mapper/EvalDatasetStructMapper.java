/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapstruct.mapper;

import com.huawei.jade.app.engine.eval.dto.EvalDatasetDto;
import com.huawei.jade.app.engine.eval.dto.EvalDatasetUpdateDto;
import com.huawei.jade.app.engine.eval.po.EvalDatasetPo;
import com.huawei.jade.app.engine.eval.vo.EvalDatasetVo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 评估数据集相关类转换接口。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Mapper
public interface EvalDatasetStructMapper {
    /**
     * Mapper实例。
     */
    EvalDatasetStructMapper INSTANCE = Mappers.getMapper(EvalDatasetStructMapper.class);

    /**
     * 创建DTO 转 PO 接口。
     *
     * @param evalDatasetDto 表示创建评估数据集DTO的 {@link EvalDatasetDto}
     * @return 表示评估数据集PO的 {@link EvalDatasetPo}
     */
    EvalDatasetPo dtoToPO(EvalDatasetDto evalDatasetDto);

    /**
     * 更新DTO 转 PO 接口。
     *
     * @param evalDatasetUpdateDto 表示更新评估数据集DTO的 {@link EvalDatasetUpdateDto}
     * @return 表示评估数据集PO的 {@link EvalDatasetPo}
     */
    EvalDatasetPo updateDTOToPO(EvalDatasetUpdateDto evalDatasetUpdateDto);

    /**
     * PO 转 VO 接口。
     *
     * @param evalDatasetPo 表示评估数据集PO的 {@link EvalDatasetPo}
     * @return 表示评估数据集VO的 {@link EvalDatasetVo}
     */
    EvalDatasetVo poToVO(EvalDatasetPo evalDatasetPo);
}
