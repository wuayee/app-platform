/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service;

import com.huawei.jade.app.engine.task.dto.EvalInstanceQueryParam;
import com.huawei.jade.app.engine.task.entity.EvalInstanceEntity;
import com.huawei.jade.common.vo.PageVo;

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

    /**
     * 分页查询评估任务实例。
     *
     * @param queryParam 表示评估任务实例查询参数的 {@link EvalInstanceQueryParam}。
     * @return 表示评估任务实例查询结果的 {@link PageVo}{@code <}{@link EvalInstanceEntity}{@code >}。
     */
    PageVo<EvalInstanceEntity> listEvalInstance(EvalInstanceQueryParam queryParam);
}
