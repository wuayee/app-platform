/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.jobers;

import com.huawei.fit.jober.common.exceptions.JobberParamException;

import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberType;
import modelengine.fitframework.inspection.Validation;

/**
 * 节点自动任务校验规则
 *
 * @author 高诗意
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
        Validation.equals(FlowJoberType.ECHO_JOBER, flowJober.getType(), exception("flow jober type"));
        Validation.equals(0, flowJober.getFitables().size(), exception("flow jober fitables"));
    }
}
