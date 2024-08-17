/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.mapper;

import com.huawei.jade.app.engine.task.po.EvalInstancePo;

import org.apache.ibatis.annotations.Mapper;

/**
 * 表示评估任务实例持久层接口。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
@Mapper
public interface EvalInstanceMapper {
    /**
     * 创建评估任务实例。
     *
     * @param po 表示评估任务实例的 {@link EvalInstancePo}。
     */
    void create(EvalInstancePo po);
}
