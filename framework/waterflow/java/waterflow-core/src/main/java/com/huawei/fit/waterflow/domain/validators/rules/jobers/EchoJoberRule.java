/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.validators.rules.jobers;

import static com.huawei.fit.waterflow.domain.enums.FlowJoberType.ECHO_JOBER;

import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fit.waterflow.domain.definitions.nodes.jobers.FlowJober;
import com.huawei.fitframework.inspection.Validation;

/**
 * 节点自动任务校验规则
 *
 * @author g00564732
 * @since 1.0
 */
public class EchoJoberRule implements JoberRule {
    /**
     * 校验不同流程节点自动任务类型的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowJober 流程节点自动任务
     */
    @Override
    public void apply(FlowJober flowJober) {
        Validation.notNull(flowJober.getType(), exception("flow jober type"));
        Validation.equals(ECHO_JOBER, flowJober.getType(), exception("flow jober type"));
        Validation.equals(0, flowJober.getFitables().size(), exception("flow jober fitables"));
    }
}
