/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service;

import com.huawei.jade.app.engine.task.dto.EvalTaskQueryParam;
import com.huawei.jade.app.engine.task.entity.EvalTaskEntity;
import com.huawei.jade.common.vo.PageVo;

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

    /**
     * 通过唯一标识查询评估任务元数据。
     *
     * @param queryParam 表示评估数据集查询参数的 {@link EvalTaskQueryParam}。
     * @return 表示评估任务元数据查询结果的 {@link PageVo}{@code <}{@link EvalTaskEntity}{@code >}。
     */
    PageVo<EvalTaskEntity> listEvalTask(EvalTaskQueryParam queryParam);
}
