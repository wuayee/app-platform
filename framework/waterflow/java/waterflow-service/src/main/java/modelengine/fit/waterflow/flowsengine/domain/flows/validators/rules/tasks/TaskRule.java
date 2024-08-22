/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.tasks;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.tasks.FlowTask;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.Rules;

/**
 * 不同流程节点手动任务类型校验规则
 *
 * @author 高诗意
 * @since 2023/08/29
 */
public interface TaskRule extends Rules {
    /**
     * 校验不同流程节点任务类型的合法性
     * 当校验不通过时，抛出运行时异常{@link JobberParamException}
     *
     * @param flowTask 流程节点人工任务
     */
    void apply(FlowTask flowTask);
}
