/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service;

import com.huawei.jade.app.engine.task.entity.EvalReportEntity;

/**
 * 表示评估任务报告服务。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
public interface EvalReportService {
    /**
     * 创建评估任务报告。
     *
     * @param entity 表示评估任务报告业务对象的 {@link EvalReportEntity}。
     */
    void createEvalReport(EvalReportEntity entity);
}
