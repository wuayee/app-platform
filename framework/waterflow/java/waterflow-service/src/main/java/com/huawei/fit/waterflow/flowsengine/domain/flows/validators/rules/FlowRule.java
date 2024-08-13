/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;

/**
 * 流程定义校验规则
 *
 * @author 高诗意
 * @since 2023/08/29
 */
public interface FlowRule extends Rules {
    /**
     * 校验流程定义的合法性
     * 当校验不通过时，抛出运行时异常{@link JobberParamException}
     *
     * @param flowDefinition 流程定义实体
     */
    void apply(FlowDefinition flowDefinition);
}
