/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.convertor;

import com.huawei.jade.app.engine.task.dto.EvalTaskCreateDto;
import com.huawei.jade.app.engine.task.entity.EvalTaskEntity;
import com.huawei.jade.app.engine.task.po.EvalTaskPo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 定义评估应用的转换器接口。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@Mapper
public interface EvalTaskConvertor {
    /**
     * 获取 EvalTaskConvertor 的实现。
     */
    EvalTaskConvertor INSTANCE = Mappers.getMapper(EvalTaskConvertor.class);

    /**
     * 将 {@link EvalTaskCreateDto} 转换为 {@link EvalTaskEntity}。
     *
     * @param createDto 表示待转换的 {@link EvalTaskCreateDto}。
     * @return 表示转换完成的 {@link EvalTaskEntity}。
     */
    EvalTaskEntity convertDtoToEntity(EvalTaskCreateDto createDto);

    /**
     * 将 {@link EvalTaskEntity} 转换为 {@link EvalTaskPo}。
     *
     * @param entity 表示待转换的 {@link EvalTaskEntity}。
     * @return 表示转换完成的 {@link EvalTaskPo}。
     */
    EvalTaskPo entityToPo(EvalTaskEntity entity);
}
