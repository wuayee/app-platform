/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.validators.rules;

import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fit.waterflow.domain.definitions.FlowDefinition;
import com.huawei.fit.waterflow.domain.definitions.nodes.tasks.FlowTask;
import com.huawei.fit.waterflow.domain.enums.FlowTaskType;
import com.huawei.fit.waterflow.domain.validators.rules.tasks.TaskCenterTaskRule;
import com.huawei.fit.waterflow.domain.validators.rules.tasks.TaskRule;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 节点手动任务校验规则
 *
 * @author 杨祥宇
 * @since 1.0
 */
@Component
public class TasksRule implements FlowRule {
    private final BrokerClient brokerClient;

    private Map<FlowTaskType, TaskRule> taskRuleMap;

    public TasksRule(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
        initialTaskRuleMap();
    }

    private void initialTaskRuleMap() {
        taskRuleMap = new HashMap<FlowTaskType, TaskRule>() {{
            put(FlowTaskType.APPROVING_TASK, new TaskCenterTaskRule(brokerClient));
            put(FlowTaskType.TASK_CENTER, new TaskCenterTaskRule(brokerClient));
        }};
    }

    /**
     * 校验流程定义的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowDefinition 流程定义实体
     */
    @Override
    public void apply(FlowDefinition flowDefinition) {
        flowDefinition.getNodeIdSet()
                .forEach(nodeId -> Optional.ofNullable(flowDefinition.getFlowNode(nodeId).getTask())
                        .ifPresent(this::apply));
    }

    private void apply(FlowTask flowTask) {
        Validation.notNull(flowTask.getTaskType(), exception("flow task type"));
        Validation.notBlank(flowTask.getTaskId(),
                exception(StringUtils.format("flow {0} task id", flowTask.getTaskType().getCode())));
        Optional.ofNullable(taskRuleMap.get(flowTask.getTaskType())).ifPresent(taskRule -> taskRule.apply(flowTask));
    }
}
