/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.manual.operation.operator;

import static com.huawei.fit.jober.common.ErrorCodes.ENTITY_NOT_FOUND;
import static com.huawei.fit.jober.common.ErrorCodes.FAILED_TO_SAVE_INSTANCE;

import com.huawei.fit.jober.InstanceService;
import com.huawei.fit.jober.TaskService;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.entity.InstanceInfo;
import com.huawei.fit.jober.entity.OperationContext;
import com.huawei.fit.jober.entity.TaskFilter;
import com.huawei.fit.jober.entity.task.Task;
import com.huawei.fit.jober.entity.task.TaskProperty;
import com.huawei.fit.waterflow.common.Constant;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.tasks.FlowTask;
import com.huawei.fit.waterflow.flowsengine.utils.FlowUtil;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.log.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 任务中心Operator
 *
 * @author 00693950
 * @since 2023/9/15
 */
public class TaskCenterOperator implements Operator {
    private static final Logger log = Logger.get(TaskCenterOperator.class);

    private static final String DEFAULT_TENANT_ID = "public";

    private static final String DEFAULT_OPERATOR = "tianzhou";

    private static final String QUERY_TASK_GENERICABLE = "5a7683b3b6ac495198efc492790a3a5f";

    private static final String QUERY_TASK_FITABLE = "e3c57537f76d44c3b1a6fffedbec297b";

    private static final String CREATE_TASK_INSTANCE_GENERICABLE = "f1b88d9eb48b48959365a24e27dabb80";

    private static final String SAVE_TASK_INSTANCE_FITABLE = "afd6771a02704266b0a825d70be21ef6";

    private final BrokerClient brokerClient;

    public TaskCenterOperator(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
    }

    @Override
    public void operate(List<FlowContext<FlowData>> contexts, FlowTask manualTask) {
        Task task = getTask(manualTask.getTaskId());

        for (FlowContext<FlowData> context : contexts) {
            String flowContextId = context.getId();
            log.info("[TaskCenterOperator]: Start to create task instance, flowContextId is {}.", flowContextId);
            Map<String, Object> businessData = context.getData().getBusinessData();
            createTaskInstance(businessData, manualTask, task, flowContextId);
        }
    }

    private void createTaskInstance(Map<String, Object> businessData, FlowTask manualTask, Task task,
            String flowContextId) {
        String taskId = manualTask.getTaskId();
        String sourceId = task.getSources().get(0).getId();
        List<TaskProperty> propertyList = task.getProperties();
        InstanceInfo instanceInfo = new InstanceInfo(null, sourceId,
                convertProperty(propertyList, businessData, manualTask.getProperties(), flowContextId),
                Collections.singletonList(DEFAULT_OPERATOR));
        try {
            brokerClient.getRouter(InstanceService.class, CREATE_TASK_INSTANCE_GENERICABLE)
                    .route(new FitableIdFilter(SAVE_TASK_INSTANCE_FITABLE))
                    .invoke(taskId, instanceInfo, getOperationContext());
        } catch (FitException e) {
            log.error("Catch throwable when remote invoke, fitableId is {}. Caused by {}", SAVE_TASK_INSTANCE_FITABLE,
                    e.getMessage());
            throw new JobberException(FAILED_TO_SAVE_INSTANCE);
        }
        log.info("[TaskCenterOperator]: Create task instance success, flowContextId is {}.", flowContextId);
    }

    private Map<String, Object> convertProperty(List<TaskProperty> propertyList, Map<String, Object> businessData,
            Map<String, String> inputProperties, String flowContextId) {
        Map<String, Object> propertyMap = new HashMap<String, Object>() {
            {
                put(Constant.STATUS, "pending_approval");
                put(Constant.PRIORITY, "ordinary");
                put(Constant.REQUIREMENT_ID, flowContextId);
                put(Constant.CREATED_DATE, getTime());
            }
        };

        propertyList.stream()
                .filter(taskProperty -> !Objects.equals(taskProperty.getName(), Constant.STATUS))
                .forEach(taskProperty -> {
                    String key = taskProperty.getName();
                    Object instancePropertyValue = inputProperties.containsKey(key) ? FlowUtil.replace(
                            inputProperties.get(key), businessData) : businessData.get(key);
                    Optional.ofNullable(instancePropertyValue).ifPresent(propValue -> propertyMap.put(key, propValue));
                });
        return propertyMap;
    }

    /**
     * getTask
     *
     * @param taskId taskId
     * @return Task
     */
    public Task getTask(String taskId) {
        TaskFilter taskFilter = new TaskFilter();
        taskFilter.setIds(Collections.singletonList(taskId));
        OperationContext operationContext = getOperationContext();
        Task task;
        try {
            task = brokerClient.getRouter(TaskService.class, QUERY_TASK_GENERICABLE)
                    .route(new FitableIdFilter(QUERY_TASK_FITABLE))
                    .invoke(taskId, operationContext);
        } catch (FitException e) {
            log.error("Catch throwable when remote invoke, fitableId is {}. Caused by {}", QUERY_TASK_FITABLE,
                    e.getMessage());
            throw new JobberException(ENTITY_NOT_FOUND, "task", taskId);
        }
        log.info("TaskCenterOperator]: Get task definition success, taskId is {}", taskId);
        return task;
    }

    private OperationContext getOperationContext() {
        return new OperationContext(DEFAULT_TENANT_ID, DEFAULT_OPERATOR, null, null, null);
    }

    private String getTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
}
