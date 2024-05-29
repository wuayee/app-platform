/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.jade.app.engine.eval.mapper.EvalReportTraceMapper;
import com.huawei.jade.app.engine.eval.po.EvalReportTracePo;
import com.huawei.jade.app.engine.eval.service.EvalTaskReportTraceService;

import java.util.List;

/**
 * 评估任务调用轨迹服务的实现。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Component
public class EvalTaskReportTraceServiceImpl implements EvalTaskReportTraceService {
    @Fit
    private EvalReportTraceMapper evalReportTraceMapper;

    /**
     * 插入一条调用轨迹。
     *
     * @param evalReportTracePO 表示调用轨迹实体类的 {@link EvalReportTracePo}
     * @return 表示成功插入的条数的 {@link Long}
     */
    @Override
    public long insertTrace(EvalReportTracePo evalReportTracePO) {
        return evalReportTraceMapper.insert(evalReportTracePO);
    }

    /**
     * 批量插入调用轨迹。
     *
     * @param evalReportTracePoList 表示调用轨迹实体类列表的 {@link List}{@code <}{@link EvalReportTracePo}{@code >}
     * @return 表示成功插入的条数的 {@link Long}
     */
    @Override
    public long insertAllTrace(List<EvalReportTracePo> evalReportTracePoList) {
        return evalReportTraceMapper.insertAll(evalReportTracePoList);
    }
}
