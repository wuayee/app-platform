/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.validators.rules.tasks;

import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fit.waterflow.domain.definitions.nodes.tasks.FlowTask;
import com.huawei.fit.waterflow.domain.validators.rules.Rules;

/**
 * 不同流程节点手动任务类型校验规则
 *
 * @author 高诗意
 * @since 1.0
 */
public interface TaskRule extends Rules {
    /**
     * 校验不同流程节点任务类型的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowTask 流程节点人工任务
     */
    void apply(FlowTask flowTask);
}
