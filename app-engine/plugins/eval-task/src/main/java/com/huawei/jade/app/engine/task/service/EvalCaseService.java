/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service;

import com.huawei.jade.app.engine.task.entity.EvalCaseEntity;
import com.huawei.jade.app.engine.task.entity.EvalRecordEntity;

import java.util.List;

/**
 * 表示评估任务用例服务。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
public interface EvalCaseService {
    /**
     * 创建评估任务用例。
     *
     * @param entity 表示评估任务用例业务对象的 {@link EvalCaseEntity}。
     * @param results 表示评估任务用例结果对象的 {@link List}{@code <}{@link EvalRecordEntity}{@code >}。
     */
    void createEvalCase(EvalCaseEntity entity, List<EvalRecordEntity> results);
}