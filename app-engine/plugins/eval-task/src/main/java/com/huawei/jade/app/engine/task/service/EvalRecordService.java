/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service;

import com.huawei.jade.app.engine.task.entity.EvalRecordEntity;

import java.util.List;

/**
 * 表示评估任务记录服务。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
public interface EvalRecordService {
    /**
     * 创建评估任务用例结果。
     *
     * @param result 表示评估任务用例结果对象的 {@link List}{@code <}{@link EvalRecordEntity}{@code >}。
     */
    void createEvalRecord(List<EvalRecordEntity> result);
}