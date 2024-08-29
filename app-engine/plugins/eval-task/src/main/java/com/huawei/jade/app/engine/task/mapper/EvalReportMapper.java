/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.mapper;

import com.huawei.jade.app.engine.task.po.EvalReportPo;

import org.apache.ibatis.annotations.Mapper;

/**
 * 表示评估任务报告持久层接口。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
@Mapper
public interface EvalReportMapper {
    /**
     * 创建评估任务报告。
     *
     * @param evalReportPo 表示评估任务报告信息的 {@link EvalReportPo}。
     */
    void create(EvalReportPo evalReportPo);
}
