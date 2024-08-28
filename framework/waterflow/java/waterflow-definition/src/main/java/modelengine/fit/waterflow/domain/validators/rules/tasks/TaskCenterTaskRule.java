/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.validators.rules.tasks;

import modelengine.fit.waterflow.common.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.domain.common.Constant;
import modelengine.fit.waterflow.domain.definitions.nodes.tasks.FlowTask;
import modelengine.fit.waterflow.domain.enums.FlowTaskType;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.inspection.Validation;

/**
 * 任务中心人工任务校验规则
 *
 * @author 高诗意
 * @since 1.0
 */
public class TaskCenterTaskRule implements TaskRule {
    private final BrokerClient brokerClient;

    public TaskCenterTaskRule(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
    }

    /**
     * 校验不同流程节点任务类型的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowTask 流程节点人工任务
     */
    @Override
    public void apply(FlowTask flowTask) {
        if (!FlowTaskType.APPROVING_TASK.equals(flowTask.getTaskType())) {
            return;
        }
        Validation.notBlank(flowTask.getTaskId(), exception("flow task id"));
        Validation.notBlank(flowTask.getProperties().get(Constant.TITLE), exception("flow task title"));
        Validation.notBlank(flowTask.getProperties().get(Constant.OWNER), exception("flow task owner"));
        Validation.notBlank(flowTask.getProperties().get(Constant.CREATED_BY), exception("flow task created by"));
    }
}
