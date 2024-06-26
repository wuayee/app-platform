/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.callbacks;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowCallbackType;
import com.huawei.fitframework.inspection.Validation;

/**
 * 节点通用型回调函数校验规则
 *
 * @author l00862071
 * @since 2023/12/11
 */
public class GeneralCallbackRule implements CallbackRule {
    /**
     * 校验不同流程节点通用型回调函数类型的合法性
     * 当校验不通过时，抛出运行时异常{@link JobberParamException}
     *
     * @param flowCallback 流程节点回调函数
     */
    @Override
    public void apply(FlowCallback flowCallback) {
        Validation.notNull(flowCallback.getType(), exception("flow callback type"));
        Validation.equals(FlowCallbackType.GENERAL_CALLBACK, flowCallback.getType(), exception("flow callback type"));
        Validation.equals(1, flowCallback.getFitables().size(), exception("flow callback fitables"));
    }
}
