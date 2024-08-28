/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.tasks;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.entity.task.Task;
import com.huawei.fit.jober.entity.task.TaskProperty;

import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.tasks.FlowTask;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowTaskType;
import modelengine.fit.waterflow.flowsengine.manual.operation.operator.TaskCenterOperator;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.inspection.Validation;

import java.util.List;

/**
 * 任务中心人工任务校验规则
 *
 * @author 高诗意
 * @since 2023/09/25
 */
public class TaskCenterTaskRule implements TaskRule {
    private final BrokerClient brokerClient;

    public TaskCenterTaskRule(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
    }

    /**
     * 校验不同流程节点任务类型的合法性
     * 当校验不通过时，抛出运行时异常{@link JobberParamException}
     *
     * @param flowTask 流程节点人工任务
     */
    @Override
    public void apply(FlowTask flowTask) {
        if (FlowTaskType.APPROVING_TASK.equals(flowTask.getTaskType())) {
            Validation.notBlank(flowTask.getTaskId(), exception("flow task id"));
            Validation.notBlank(flowTask.getProperties().get(Constant.TITLE), exception("flow task title"));
            Validation.notBlank(flowTask.getProperties().get(Constant.OWNER), exception("flow task owner"));
            Validation.notBlank(flowTask.getProperties().get(Constant.CREATED_BY), exception("flow task created by"));
            return;
        }
        TaskCenterOperator taskCenterOperator = new TaskCenterOperator(brokerClient);
        Task task = taskCenterOperator.getTask(flowTask.getTaskId());
        Validation.notNull(task, exception("task definition"));
        List<TaskProperty> properties = task.getProperties();
        properties.stream()
                .filter(p -> p.isRequired())
                .forEach(p -> Validation.notBlank(flowTask.getProperties().get(p.getName()),
                        exception("flow task " + p.getName())));
    }
}
