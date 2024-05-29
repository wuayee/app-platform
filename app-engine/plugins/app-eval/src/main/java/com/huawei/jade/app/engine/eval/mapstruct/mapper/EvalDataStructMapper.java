/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapstruct.mapper;

import com.huawei.jade.app.engine.eval.dto.EvalDataDto;
import com.huawei.jade.app.engine.eval.dto.EvalDataUpdateDto;
import com.huawei.jade.app.engine.eval.po.EvalDataPo;
import com.huawei.jade.app.engine.eval.vo.EvalDataVo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 评估数据相关类转换接口。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Mapper
public interface EvalDataStructMapper {
    /**
     * Mapper实例。
     */
    EvalDataStructMapper INSTANCE = Mappers.getMapper(EvalDataStructMapper.class);

    /**
     * 创建DTO 转 PO 接口。
     *
     * @param evalDataDto 表示创建评估数据DTO的 {@link EvalDataDto}
     * @return 表示评估数据PO的 {@link EvalDataPo}
     */
    EvalDataPo dtoToPO(EvalDataDto evalDataDto);

    /**
     * 更新DTO 转 PO 接口。
     *
     * @param evalDataUpdateDto 表示更新评估数据DTO的 {@link EvalDataUpdateDto}
     * @return 表示评估数据PO的 {@link EvalDataPo}
     */
    EvalDataPo updateDTOToPO(EvalDataUpdateDto evalDataUpdateDto);

    /**
     * PO 转 VO 接口。
     *
     * @param evalDataPo 表示评估数据PO的 {@link EvalDataPo}
     * @return 表示评估数据VO的 {@link EvalDataVo}
     */
    EvalDataVo poToVO(EvalDataPo evalDataPo);
}
