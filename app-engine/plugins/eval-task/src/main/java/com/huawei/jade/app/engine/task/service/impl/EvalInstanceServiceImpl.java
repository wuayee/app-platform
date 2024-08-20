/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.app.engine.task.mapper.EvalInstanceMapper;
import com.huawei.jade.app.engine.task.po.EvalInstancePo;
import com.huawei.jade.app.engine.task.service.EvalInstanceService;

/**
 * 表示 {@link EvalInstanceService} 的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
@Component
public class EvalInstanceServiceImpl implements EvalInstanceService {
    private final EvalInstanceMapper taskInstanceMapper;

    /**
     * 表示评估任务实例服务实现的构建器。
     *
     * @param taskInstanceMapper 表示评估任务实例持久层接口的 {@link EvalInstanceMapper}。
     */
    public EvalInstanceServiceImpl(EvalInstanceMapper taskInstanceMapper) {
        this.taskInstanceMapper = taskInstanceMapper;
    }

    @Override
    public void createEvalInstance(Long taskId) {
        EvalInstancePo po = new EvalInstancePo();
        this.taskInstanceMapper.create(po);
    }
}
