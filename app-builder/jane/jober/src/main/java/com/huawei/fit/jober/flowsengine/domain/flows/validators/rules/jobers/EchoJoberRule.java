/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.validators.rules.jobers;

import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowJoberType.ECHO_JOBER;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import com.huawei.fitframework.inspection.Validation;

/**
 * 节点自动任务校验规则
 *
 * @author g00564732
 * @since 2023/08/14
 */
public class EchoJoberRule implements JoberRule {
    /**
     * 校验不同流程节点自动任务类型的合法性
     * 当校验不通过时，抛出运行时异常{@link JobberParamException}
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
