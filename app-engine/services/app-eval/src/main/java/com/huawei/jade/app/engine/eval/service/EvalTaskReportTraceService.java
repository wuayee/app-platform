/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service;

import com.huawei.jade.app.engine.eval.po.EvalReportTracePo;

import java.util.List;

/**
 * 评估报告调用轨迹持久化接口
 *
 * @since 2024/05/28
 */
public interface EvalTaskReportTraceService {
    /**
     * 插入单条轨迹
     *
     * @param evalReportTracePo 调用轨迹实体类
     * @return 成功插入的条数
     */
    long insertTrace(EvalReportTracePo evalReportTracePo);

    /**
     * 批量插入调用轨迹
     *
     * @param evalReportTracePoList 调用轨迹实体类列表
     * @return 成功插入的条数
     */
    long insertAllTrace(List<EvalReportTracePo> evalReportTracePoList);
}
