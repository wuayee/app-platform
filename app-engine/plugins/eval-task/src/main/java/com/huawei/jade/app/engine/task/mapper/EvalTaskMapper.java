/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.mapper;

import com.huawei.jade.app.engine.task.po.EvalTaskPo;

import org.apache.ibatis.annotations.Mapper;

/**
 * 表示评估任务持久层接口。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@Mapper
public interface EvalTaskMapper {
    /**
     * 创建评估任务。
     *
     * @param evalTaskPo 表示评估任务信息的 {@link EvalTaskPo}。
     */
    void create(EvalTaskPo evalTaskPo);
}
