/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.convertor;

import com.huawei.jade.app.engine.task.entity.EvalRecordEntity;
import com.huawei.jade.app.engine.task.po.EvalRecordPo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 定义评估任务用例结果的转换器接口。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Mapper
public interface EvalRecordConvertor {
    /**
     * 获取 EvalRecordConvertor 的实现。
     */
    EvalRecordConvertor INSTANCE = Mappers.getMapper(EvalRecordConvertor.class);

    /**
     * 将 {@link EvalRecordEntity} 转换为 {@link EvalRecordPo}。
     *
     * @param entity 表示待转换的 {@link EvalRecordEntity}。
     * @return 表示转换完成的 {@link EvalRecordPo}。
     */
    EvalRecordPo entityToPo(EvalRecordEntity entity);
}