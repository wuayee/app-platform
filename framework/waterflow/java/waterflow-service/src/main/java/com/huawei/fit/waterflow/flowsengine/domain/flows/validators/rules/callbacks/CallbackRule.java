/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.callbacks;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.Rules;

/**
 * 不同流程节点回调函数类型校验规则
 *
 * @author 李哲峰
 * @since 2023/12/11
 */
public interface CallbackRule extends Rules {
    /**
     * 校验不同流程节点回调函数类型的合法性
     * 当校验不通过时，抛出运行时异常{@link JobberParamException}
     *
     * @param flowCallback 流程节点回调函数
     */
    void apply(FlowCallback flowCallback);
}
