/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.validators.rules.callbacks;

import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fit.waterflow.domain.definitions.nodes.callbacks.FlowCallback;
import com.huawei.fit.waterflow.domain.validators.rules.Rules;

/**
 * 不同流程节点回调函数类型校验规则
 *
 * @author l00862071
 * @since 1.0
 */
public interface CallbackRule extends Rules {
    /**
     * 校验不同流程节点回调函数类型的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowCallback 流程节点回调函数
     */
    void apply(FlowCallback flowCallback);
}
