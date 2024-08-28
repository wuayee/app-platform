/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.edatamate.service;

import static com.huawei.fit.jober.common.ErrorCodes.ENTITY_NOT_FOUND;

import com.huawei.fit.jober.InstanceService;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.entity.InstanceInfo;
import com.huawei.fit.jober.entity.InstanceQueryFilter;
import com.huawei.fit.jober.entity.OperationContext;
import com.huawei.fit.jober.entity.instance.Instance;
import modelengine.fit.waterflow.common.utils.TimeUtil;
import modelengine.fit.waterflow.edatamate.dao.po.FlowNotificationPo;
import modelengine.fit.waterflow.edatamate.entity.InstanceFinishedTaskInfo;
import modelengine.fit.waterflow.edatamate.enums.ScanStatus;
import modelengine.fit.waterflow.edatamate.repo.FlowNotificationRepo;
import modelengine.fit.waterflow.flowsengine.biz.service.entity.FlowTransCompletionInfo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.callbacks.FlowEventCallback;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo.DefaultFlowDefinitionRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowTraceStatus;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.annotation.Scheduled;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import com.huawei.waterflow.edatamate.TaskInstanceService;

import com.alibaba.fastjson.JSONObject;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

/**
 * 接收trans完成事件，并触发更新task是否完成
 *
 * @author 宋永坦
 * @since 2024/2/29
 */
@Component
public class TaskUpdater implements FlowEventCallback {
    private static final Logger log = Logger.get(OrchestratorService.class);

    private static final String INSTANCE_FINISHED_TASK_GENERICABLE = "afc63686857d47cab4343ea1847f769f";

    private static final long TASK_NOTIFY_INTERVAL = 30000L;

    private final List<String> instanceFinishedTaskFitables = Arrays.asList(
            "com.huawei.eDataMate.operators.nas_umount_plugin",
            "com.huawei.eDataMate.operators.delete_file_features_plugin");

    private final DefaultFlowDefinitionRepo flowDefinitionRepo;

    private final FlowContextPersistRepo flowContextPersistRepo;

    private final FlowNotificationRepo flowNotificationRepo;

    private final InstanceService instanceService;

    private final FlowLocks locks;

    private final BrokerClient brokerClient;

    public TaskUpdater(DefaultFlowDefinitionRepo flowDefinitionRepo, FlowContextPersistRepo flowContextPersistRepo,
                       FlowNotificationRepo flowNotificationRepo, InstanceService instanceService, FlowLocks locks,
                       BrokerClient brokerClient) {
        this.flowDefinitionRepo = flowDefinitionRepo;
        this.flowContextPersistRepo = flowContextPersistRepo;
        this.flowNotificationRepo = flowNotificationRepo;
        this.instanceService = instanceService;
        this.locks = locks;
        this.brokerClient = brokerClient;
    }

    @Override
    public void onFlowTransCompleted(FlowTransCompletionInfo info) {
        log.info("Received the flow trans completion, transId={}, status={}.", info.getFlowTransId(), info.getStatus());
        // 获取start节点的context, 从而获取到task的标识信息
        Map<String, Object> businessData = getBusinessData(info.getFlowTransId());
        instanceCallback(info.getStatus().name(), businessData);
        String taskId = ObjectUtils.cast(businessData.get("taskId"));
        String taskInstanceId = ObjectUtils.cast(businessData.get("taskInstanceId"));
        tryUpdateTaskStatusEnd(taskId, taskInstanceId, info.getFlowTransId(), info.getStatus());
    }

    /**
     * 任务终态触发回调
     *
     * @param status 任务状态
     * @param businessData 业务数据
     */
    public void instanceCallback(String status, Map<String, Object> businessData) {
        Map<String, Object> finishedTaskInfo = (Map<String, Object>) JSONObject.toJSON(
                getFinishedTaskInfo(status, businessData));
        instanceFinishedTaskFitables.forEach(fitableId -> {
            log.info("start call back, id: {}", fitableId);
            flowNotificationRepo.create(fitableId, finishedTaskInfo);
        });
    }

    /**
     * 获取业务数据
     *
     * @param transId 流程trans id标识
     * @return 业务数据
     */
    public Map<String, Object> getBusinessData(String transId) {
        String streamId = Optional.ofNullable(flowContextPersistRepo.getStreamIdByTransId(transId))
                .orElseThrow(() -> new JobberException(ENTITY_NOT_FOUND, "streamId", transId));
        FlowDefinition flowDefinition = Optional.ofNullable(flowDefinitionRepo.findByStreamId(streamId))
                .orElseThrow(() -> new JobberException(ENTITY_NOT_FOUND, "flowDefinition", streamId));
        FlowNode flowStartNode = flowDefinition.getFlowNode(FlowNodeType.START);

        List<FlowContext<FlowData>> taskStartContext = flowContextPersistRepo.findFinishedContextsPagedByTransId(
                transId, flowStartNode.getMetaId(), 1, 1);
        Validation.notEmpty(taskStartContext,
                () -> new JobberException(ENTITY_NOT_FOUND, "taskStartContext", transId));

        return taskStartContext.get(0).getData().getBusinessData();
    }

    private InstanceFinishedTaskInfo getFinishedTaskInfo(String status, Map<String, Object> businessData) {
        Map<String, Object> params = Optional.ofNullable(
                ObjectUtils.<Map<String, Object>>cast(businessData.get("params"))).orElse(new HashMap<>());
        return InstanceFinishedTaskInfo.builder()
                .status(status)
                .params(params)
                .build();
    }

    /**
     * trans结束时，根据任务情况尝试更新任务到终态
     *
     * @param taskId taskId
     * @param taskInstanceId taskInstanceId
     * @param flowTransId flowTransId
     * @param flowTransStatus flowTransStatus
     */
    public void tryUpdateTaskStatusEnd(String taskId, String taskInstanceId, String flowTransId,
                                       FlowTraceStatus flowTransStatus) {
        updateTaskInfoWithLock(taskId, taskInstanceId, taskInfo -> {
            String taskFlowTransId = taskInfo.get("flow_context_id");
            if (!Objects.equals(flowTransId, taskFlowTransId)) {
                log.warn("Not same flow trans id, update task instance failed. taskTransId={}, completedTransId={}.",
                        taskFlowTransId, flowTransId);
                return null;
            }
            if (FlowTraceStatus.isEndStatus(FlowTraceStatus.valueOf(taskInfo.get("status")))) {
                log.warn("The task is already ended. taskTransId={}.", taskFlowTransId);
                return null;
            }
            Map<String, Object> extensions = Optional.ofNullable(taskInfo.get("extensions"))
                    .map(jsonString -> ObjectUtils.<Map<String, Object>>cast(JSONObject.parseObject(jsonString)))
                    .orElse(new HashMap<>());
            String scanStatus = ObjectUtils.cast(extensions.get("scanStatus"));
            if (ScanStatus.RUNNING.getCode().equals(scanStatus)) {
                log.info("The task is not end. taskTransId={}.", taskFlowTransId);
                return null;
            }
            return getEndStatusUpdateFields(taskId, taskInstanceId, taskInfo, flowTransStatus);
        });
    }

    /**
     * 使用分布式锁更新任务信息
     *
     * @param taskId taskId
     * @param taskInstanceId taskInstanceId
     * @param getUpdateFields 获取更新字段
     * @return 返回更新前的任务信息
     */
    public Map<String, String> updateTaskInfoWithLock(String taskId, String taskInstanceId,
        Function<Map<String, String>, Map<String, Object>> getUpdateFields) {
        Lock lock = locks.getDistributedLock(taskInstanceId);
        lock.lock();
        try {
            Instance taskInfo = getTaskInfo(taskInstanceId, taskId);
            Map<String, String> info = taskInfo.getInfo();
            Map<String, Object> updateFields = getUpdateFields.apply(info);
            if (Objects.isNull(updateFields) || updateFields.isEmpty()) {
                log.warn("No fields for updating. task={}:{}.", taskId, taskInstanceId);
                return info;
            }

            updateTaskInstance(taskId, taskInstanceId, updateFields);
            return info;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 更新任务中心实例状态，详细见 <a href="https://jane-alpha.huawei.com/#/tenant/public/taskTypeConfig">
     * https://jane-alpha.huawei.com/#/tenant/public/taskTypeConfig</a>
     * ，将需要更新的字段放在Map中
     *
     * @param taskId 任务id
     * @param taskInstanceId 任务实例id
     * @param map 需要更新的字段key-value值
     */
    public void updateTaskInstance(String taskId, String taskInstanceId, Map<String, Object> map) {
        log.info("Start to update task instance with task id {}, instance id {}", taskId, taskInstanceId);
        if (MapUtils.isEmpty(map)) {
            return;
        }
        log.debug("Update contents are: {}", map);
        InstanceInfo instanceInfo = new InstanceInfo();
        instanceInfo.setInfo(map);
        instanceService.patchTaskInstance(taskId, taskInstanceId, instanceInfo, new OperationContext());
        log.info("Finish update task instance successfully.");
    }

    /**
     * 获取任务更新完成状态时的更新字段
     *
     * @param taskId taskId
     * @param taskInstanceId taskInstanceId
     * @param taskInfo taskInfo
     * @param flowTransStatus flowTransStatus
     * @return 需要更新的字段
     */
    public Map<String, Object> getEndStatusUpdateFields(String taskId, String taskInstanceId,
                                                        Map<String, String> taskInfo, FlowTraceStatus flowTransStatus) {
        int totalFileNum = Integer.parseInt(taskInfo.get("file_num"));
        int processedNum = Integer.parseInt(taskInfo.get("processed_num"));
        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("status", flowTransStatus.name());
        String finishTime = TimeUtil.getFormatCurTime();
        updateFields.put("finish_time", finishTime);
        // 没有扫盘数据时跟随trans状态, 成功时认为100%
        if (totalFileNum == 0) {
            log.info("The task is completed, task={}:{}, processedNum={}, totalFileNum={}.", taskId, taskInstanceId,
                    processedNum, totalFileNum);
            if (FlowTraceStatus.ARCHIVED.equals(flowTransStatus)) {
                updateFields.put("progress_percent", "100.00");
            }
            return updateFields;
        }
        double curPercentage = (double) (processedNum * 100) / (double) totalFileNum;
        if (processedNum >= totalFileNum) {
            log.info("The task is completed, task={}:{}, processedNum={}, totalFileNum={}.", taskId, taskInstanceId,
                    processedNum, totalFileNum);
            curPercentage = 100.00;
            updateFields.put("status", FlowTraceStatus.ARCHIVED.name());
        }
        // 目前保留防止更新数据库状态导致的计算百分比抖动问题，针对分批投递数据场景影响不大
        if (curPercentage > Double.parseDouble(taskInfo.get("progress_percent"))) {
            updateFields.put("progress_percent", String.format(Locale.ROOT, "%.2f", curPercentage));
        }
        return updateFields;
    }

    /**
     * 查询任务信息
     *
     * @param taskInstanceId instanceId
     * @param taskId taskId
     * @return Instance
     */
    public Instance getTaskInfo(String taskInstanceId, String taskId) {
        Map<String, List<String>> infos = new HashMap<>();
        infos.put("id", Collections.singletonList(taskInstanceId));
        InstanceQueryFilter filter = new InstanceQueryFilter();
        filter.setInfos(infos);

        RangedResultSet<Instance> list = instanceService.list(taskId, filter, 0, 1, false, new OperationContext());
        List<Instance> results = list.getResults();
        if (results.isEmpty()) {
            log.warn("can't found the A3000 task with task id {}, instance id {}", taskId, taskInstanceId);
            throw new JobberParamException(ENTITY_NOT_FOUND, "taskInstance", taskInstanceId);
        }
        return results.get(0);
    }

    /**
     * 任务回调重试定时任务
     */
    @Scheduled(strategy = Scheduled.Strategy.FIXED_RATE, value = "10000")
    public void flowNotifyTask() {
        while (true) {
            Lock lock = locks.getDistributedLock("TASK_NOTIFY");
            if (!lock.tryLock()) {
                return;
            }
            try {
                LocalDateTime nextNotifyTime = LocalDateTime.now();
                List<FlowNotificationPo> notifyPOList = flowNotificationRepo.findNextRetryList(nextNotifyTime);
                if (notifyPOList.isEmpty()) {
                    return;
                }
                flowNotify(notifyPOList);
            } finally {
                lock.unlock();
            }
        }
    }

    private void flowNotify(List<FlowNotificationPo> notifyPOList) {
        notifyPOList.forEach(notifyPO -> {
            try {
                brokerClient.getRouter(TaskInstanceService.class, INSTANCE_FINISHED_TASK_GENERICABLE)
                        .route(new FitableIdFilter(notifyPO.getFitableId()))
                        .timeout(3, TimeUnit.SECONDS)
                        .invoke(JSONObject.parseObject(notifyPO.getData()));
                flowNotificationRepo.delete(notifyPO.getId());
            } catch (FitException ex) {
                log.error("Invoke finish task fitable failed, fitable id: {}", notifyPO.getFitableId());
                log.error("Exception:", ex);
                notifyPO.setNotifyCount(notifyPO.getNotifyCount() + 1);
                notifyPO.setNextNotifyTime(LocalDateTime.now().plus(TASK_NOTIFY_INTERVAL, ChronoUnit.MILLIS));
                flowNotificationRepo.update(notifyPO);
            }
        });
    }
}
