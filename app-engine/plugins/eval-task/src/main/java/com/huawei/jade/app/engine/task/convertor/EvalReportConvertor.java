/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.convertor;

import com.huawei.jade.app.engine.task.entity.EvalReportEntity;
import com.huawei.jade.app.engine.task.po.EvalReportPo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 定义评估任务报告的转换器接口。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
@Mapper
public interface EvalReportConvertor {
    /**
     * 获取 EvalReportConvertor 的实现。
     */
    EvalReportConvertor INSTANCE = Mappers.getMapper(EvalReportConvertor.class);

    /**
     * 将 {@link EvalReportEntity} 转换为 {@link EvalReportPo}。
     *
     * @param entity 表示待转换的 {@link EvalReportEntity}。
     * @return 表示转换完成的 {@link EvalReportPo}。
     */
    EvalReportPo entityToPo(EvalReportEntity entity);
}