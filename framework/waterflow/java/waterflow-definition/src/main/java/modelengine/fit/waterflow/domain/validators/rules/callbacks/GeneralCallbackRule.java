/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.validators.rules.callbacks;

import modelengine.fit.waterflow.common.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.domain.definitions.nodes.callbacks.FlowCallback;
import modelengine.fit.waterflow.domain.enums.FlowCallbackType;
import modelengine.fitframework.inspection.Validation;

/**
 * 节点通用型回调函数校验规则
 *
 * @author 李哲峰
 * @since 1.0
 */
public class GeneralCallbackRule implements CallbackRule {
    /**
     * 校验不同流程节点通用型回调函数类型的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
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
