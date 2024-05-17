/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.validators.rules.jobers;

import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fit.waterflow.domain.definitions.nodes.jobers.FlowJober;
import com.huawei.fit.waterflow.domain.validators.rules.Rules;

/**
 * 不同流程节点自动任务类型校验规则
 *
 * @author 高诗意
 * @since 1.0
 */
public interface JoberRule extends Rules {
    /**
     * 校验不同流程节点自动任务类型的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowJober 流程节点自动任务
     */
    void apply(FlowJober flowJober);
}
