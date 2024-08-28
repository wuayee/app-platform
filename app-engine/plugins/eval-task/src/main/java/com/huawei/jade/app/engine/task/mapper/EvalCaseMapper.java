/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.mapper;

import com.huawei.jade.app.engine.task.po.EvalCasePo;

import org.apache.ibatis.annotations.Mapper;

/**
 * 表示评估任务用例持久层接口。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Mapper
public interface EvalCaseMapper {
    /**
     * 创建评估任务用例。
     *
     * @param casePo 表示评估任务用例信息的 {@link EvalCasePo}。
     */
    void create(EvalCasePo casePo);
}
