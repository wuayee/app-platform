/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service;

/**
 * 表示评估任务实例服务。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
public interface EvalInstanceService {
    /**
     * 插入评估任务。
     *
     * @param taskId 表示评估任务业务对象的 {@link Long}。
     */
    void createEvalInstance(Long taskId);
}
