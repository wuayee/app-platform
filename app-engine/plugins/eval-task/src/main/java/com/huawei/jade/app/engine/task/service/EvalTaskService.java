/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service;

import com.huawei.jade.app.engine.task.entity.EvalTaskEntity;

/**
 * 表示评估任务服务。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
public interface EvalTaskService {
    /**
     * 创建评估任务。
     *
     * @param entity 表示评估任务业务对象的 {@link EvalTaskEntity}。
     */
    void createEvalTask(EvalTaskEntity entity);
}
