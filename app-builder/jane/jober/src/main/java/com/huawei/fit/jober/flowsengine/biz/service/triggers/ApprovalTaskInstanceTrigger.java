/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.biz.service.triggers;

import static com.huawei.fit.jober.common.Constant.BUSINESS_DATA_KEY;
import static com.huawei.fit.jober.common.Constant.OPERATOR_KEY;
import static com.huawei.fit.waterflow.flowsengine.utils.FlowUtil.originalVariable;

import com.huawei.fit.jober.InstanceChangedService;
import com.huawei.fit.jober.entity.OperationContext;
import com.huawei.fit.jober.entity.instance.Instance;
import com.huawei.fit.jober.entity.task.Task;
import com.huawei.fit.waterflow.flowsengine.biz.service.FlowContextsService;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author 罗书强
 * @since 2023-10-10
 */
@Component
@Alias("approval-task")
public class ApprovalTaskInstanceTrigger implements InstanceChangedService {
    private static final Logger log = Logger.get(ApprovalTaskInstanceTrigger.class);

    private static final String CONTEXT_ID_KEY = "id";

    private static final String STATUS_KEY = "status";

    private static final String MODIFY_BY_KEY = "modified_by";

    private final FlowContextRepo repo;

    private final FlowDefinitionRepo definitionRepo;

    private final FlowContextsService service;

    public ApprovalTaskInstanceTrigger(@Fit(alias = "flowContextPersistRepo") FlowContextRepo repo,
            FlowDefinitionRepo definitionRepo, FlowContextsService service) {
        this.repo = repo;
        this.definitionRepo = definitionRepo;
        this.service = service;
    }

    @Override
    public void create(Task task, Instance instance, OperationContext context) {

    }

    @Override
    @Fitable(id = "ebd231876dfd41c497a19f9cc0057ba5")
    public void update(Task task, Instance instance, Map<String, Object> values, OperationContext context) {
        log.info("[ApprovalTaskInstanceChangedImpl]: Start the recovery process.");
        if (!values.containsKey(STATUS_KEY)) {
            log.info("The changed values not contain status, skip this restoration.");
            return;
        }
        String contextId = instance.getInfo().get(CONTEXT_ID_KEY);
        if (StringUtils.isEmpty(contextId)) {
            log.info("The instance info does not contain an ID, failed to restore the process.");
            return;
        }
        Map<String, Object> businessData = new HashMap<>();
        businessData.putAll(
                values.keySet().stream().collect(Collectors.toMap(key -> key, key -> instance.getInfo().get(key))));
        FlowContext<FlowData> flowContext = repo.getById(contextId);
        FlowDefinition flowDefinition = definitionRepo.findByStreamId(flowContext.getStreamId());
        FlowNode flowNode = flowDefinition.getFlowNodeByEvent(flowContext.getPosition());
        values.keySet()
                .forEach(changeKey -> Optional.ofNullable(flowNode.getTask().getProperties().get(changeKey))
                        .ifPresent(defKey -> originalVariable(defKey).forEach(targetKey -> {
                            businessData.remove(changeKey);
                            businessData.put(targetKey, instance.getInfo().get(changeKey));
                        })));

        Map<String, Object> changedContext = new HashMap<>();
        changedContext.put(OPERATOR_KEY,
                values.containsKey(MODIFY_BY_KEY) ? instance.getInfo().get(MODIFY_BY_KEY) : context.getOperator());
        changedContext.put(BUSINESS_DATA_KEY, businessData);

        Map<String, Map<String, Object>> changedContexts = new HashMap<>();
        changedContexts.put(contextId, changedContext);
        service.resumeFlows(flowDefinition.getDefinitionId(), changedContexts);
        log.info("[ApprovalTaskInstanceChangedImpl]: Recovery process success.");
    }

    @Override
    public void delete(Task task, Instance instance, OperationContext context) {

    }
}
