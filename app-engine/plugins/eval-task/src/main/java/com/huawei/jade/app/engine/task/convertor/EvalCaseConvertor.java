/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.convertor;

import com.huawei.jade.app.engine.task.entity.EvalCaseEntity;
import com.huawei.jade.app.engine.task.po.EvalCasePo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 定义评估任务用例的转换器接口。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Mapper
public interface EvalCaseConvertor {
    /**
     * 获取 EvalTaskCaseConvertor 的实现。
     */
    EvalCaseConvertor INSTANCE = Mappers.getMapper(EvalCaseConvertor.class);

    /**
     * 将 {@link EvalCaseEntity} 转换为 {@link EvalCasePo}。
     *
     * @param entity 表示待转换的 {@link EvalCaseEntity}。
     * @return 表示转换完成的 {@link EvalCasePo}。
     */
    EvalCasePo entityToPo(EvalCaseEntity entity);
}
