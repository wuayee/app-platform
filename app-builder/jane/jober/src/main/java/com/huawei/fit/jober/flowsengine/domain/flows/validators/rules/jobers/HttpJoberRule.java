/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.validators.rules.jobers;

import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowJoberProperties.ENTITY;
import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowJoberType.HTTP_JOBER;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import com.huawei.fitframework.inspection.Validation;

/**
 * HttpJoberRule
 *
 * @author 00693950
 * @since 2023/10/18
 */
public class HttpJoberRule implements JoberRule {
    /**
     * 校验不同流程节点自动任务类型的合法性
     * 当校验不通过时，抛出运行时异常{@link JobberParamException}
     *
     * @param flowJober 流程节点自动任务
     */
    @Override
    public void apply(FlowJober flowJober) {
        Validation.notNull(flowJober.getType(), exception("flow http jober type"));
        Validation.equals(HTTP_JOBER, flowJober.getType(), exception("flow http jober type"));
        Validation.equals(1, flowJober.getFitables().size(), exception("flow http jober fitables"));
        Validation.notNull(flowJober.getProperties().get(ENTITY.getValue()), exception("flow http jober entity"));
    }
}

