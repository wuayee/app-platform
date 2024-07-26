/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.service;

import static com.huawei.fit.jober.common.ErrorCodes.ENTITY_NOT_FOUND;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_DEFINITION_DELETE_ERROR;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_TASK_OPERATOR_NOT_SUPPORT;
import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;
import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static com.huawei.fit.jober.common.ErrorCodes.SERVER_INTERNAL_ERROR;
import static com.huawei.fit.jober.common.ErrorCodes.UN_EXCEPTED_ERROR;

import com.huawei.fit.jober.InstanceService;
import com.huawei.fit.jober.TaskService;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.common.util.ParamUtils;
import com.huawei.fit.jober.entity.InstanceInfo;
import com.huawei.fit.jober.entity.InstanceQueryFilter;
import com.huawei.fit.jober.entity.OperationContext;
import com.huawei.fit.jober.entity.instance.Instance;
import com.huawei.fit.jober.entity.task.Task;
import com.huawei.fit.jober.entity.task.TaskType;
import com.huawei.fit.waterflow.common.utils.TimeUtil;
import com.huawei.fit.waterflow.edatamate.client.flowsengine.request.CleanDataListQuery;
import com.huawei.fit.waterflow.edatamate.entity.CleanTaskPageResult;
import com.huawei.fit.waterflow.edatamate.enums.ScanStatus;
import com.huawei.fit.waterflow.edatamate.enums.TaskStartType;
import com.huawei.fit.waterflow.flowsengine.biz.service.FlowContextsService;
import com.huawei.fit.waterflow.flowsengine.biz.service.FlowsService;
import com.huawei.fit.waterflow.flowsengine.biz.service.entity.FlowsErrorInfo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowOfferId;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.repo.DefaultFlowDefinitionRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowTraceStatus;
import com.huawei.fit.waterflow.flowsengine.persist.po.FlowContextPO;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

import lombok.Setter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * a3000服务层
 *
 * @author y00679285
 * @since 2023/10/30
 */
@Component
@Setter
public class OrchestratorService {
    private static final Logger log = Logger.get(OrchestratorService.class);

    private static final Map<String, String> conditionFitableCode = MapBuilder.<String, String>get()
            .put("com.huawei.eDataMate.operators.pdf_extractor_plugin",
                    "if(type==\"pdf\"){\n" + "    let context1 = entity{\n"
                            + "        .async = true; .format = \"cbor\"; .id = "
                            + "\"com.huawei.eDataMate.operators.pdf_extractor_plugin\";\n" + "    };\n"
                            + "    let f1 = fit::handleTask(context1);\n" + "    ext::context >> f1\n" + "}")
            .put("com.huawei.eDataMate.operators.word_extractor_plugin",
                    "if (type == \"word\") {\n" + "    let context1 = entity{\n"
                            + "        .async = true; .format = \"cbor\"; .id = "
                            + "\"com.huawei.eDataMate.operators.word_extractor_plugin\";\n" + "    };\n"
                            + "    let f1 = fit::handleTask(context1);\n" + "    ext::context >> f1\n" + "}")
            .put("com.huawei.eDataMate.operators.md_extractor_plugin",
                    "if (type == \"md\") {\n" + "    let context1 = entity{\n"
                            + "        .async = true; .format = \"cbor\"; .id = "
                            + "\"com.huawei.eDataMate.operators.md_extractor_plugin\";\n" + "    };\n"
                            + "    let f1 = fit::handleTask(context1);\n" + "    ext::context >> f1\n" + "}\n" + "\n"
                            + "if (type == \"markdown\") {\n" + "    let context1 = entity{\n"
                            + "        .async = true; .format = \"cbor\"; .id = "
                            + "\"com.huawei.eDataMate.operators.md_extractor_plugin\";\n" + "    };\n"
                            + "    let f1 = fit::handleTask(context1);\n" + "    ext::context >> f1\n" + "}")
            .put("com.huawei.eDataMate.operators.xml_extractor_plugin",
                    "if (type == \"xml\") {\n" + "    let context1 = entity{\n"
                            + "        .async = true; .format = \"cbor\"; .id = "
                            + "\"com.huawei.eDataMate.operators.xml_extractor_plugin\";\n" + "    };\n"
                            + "    let f1 = fit::handleTask(context1);\n" + "    ext::context >> f1\n" + "}")
            .put("com.huawei.eDataMate.operators.html_extractor_plugin",
                    "if (type == \"html\") {\n" + "    let context1 = entity{\n"
                            + "        .async = true; .format = \"cbor\"; .id = "
                            + "\"com.huawei.eDataMate.operators.html_extractor_plugin\";\n" + "    };\n"
                            + "    let f1 = fit::handleTask(context1);\n" + "    ext::context >> f1\n" + "}")
            .put("com.huawei.eDataMate.operators.txt_extractor_plugin",
                    "if (type == \"txt\") {\n" + "    let context1 = entity{\n"
                            + "        .async = true; .format = \"cbor\"; .id = "
                            + "\"com.huawei.eDataMate.operators.txt_extractor_plugin\";\n" + "    };\n"
                            + "    let f1 = fit::handleTask(context1);\n" + "    ext::context >> f1\n" + "}")
            .build();

    private final InstanceService instanceService;

    private final TaskService taskService;

    private final FlowContextsService flowContextsService;

    private final FlowsService flowsService;

    private final FlowContextPersistRepo flowContextPersistRepo;

    private final QueryFlowContextPersistRepo queryFlowContextPersistRepo;

    private final DefaultFlowDefinitionRepo flowDefinitionRepo;

    private final FlowTraceRepo flowTraceRepo;

    private final Map<String, FlowAppendPoint> flowAppendPoints = new HashMap<>();

    private final FlowLocks locks;

    private final TaskUpdater taskUpdater;

    @Value("${a3000.tenantId}")
    private String tenantId;

    @Value("${a3000.operator}")
    private String operator;

    public OrchestratorService(InstanceService instanceService, TaskService taskService,
            FlowContextsService flowContextsService, FlowsService flowsService,
            FlowContextPersistRepo flowContextPersistRepo, QueryFlowContextPersistRepo queryFlowContextPersistRepo,
            DefaultFlowDefinitionRepo flowDefinitionRepo, FlowTraceRepo flowTraceRepo, FlowLocks locks,
            TaskUpdater taskUpdater) {
        this.instanceService = instanceService;
        this.taskService = taskService;
        this.flowContextsService = flowContextsService;
        this.flowsService = flowsService;
        this.flowContextPersistRepo = flowContextPersistRepo;
        this.queryFlowContextPersistRepo = queryFlowContextPersistRepo;
        this.flowDefinitionRepo = flowDefinitionRepo;
        this.flowTraceRepo = flowTraceRepo;
        this.locks = locks;
        this.taskUpdater = taskUpdater;
    }

    /**
     * 启动任务
     *
     * @param instanceId 任务实例id
     * @param taskId 任务id
     * @return String 流程实例id
     */
    public String startTask(String taskId, String instanceId) {
        log.info("Starting A3000 task, task id is {}, task instance id is {}", taskId, instanceId);
        Instance instance = getTaskInfo(instanceId, taskId);
        Map<String, String> info = instance.getInfo();
        String flowConfig = info.get("flow_config");
        Map<String, Object> map = JSONObject.parseObject(flowConfig, Map.class);
        Map<String, Object> businessData = ObjectUtils.cast(map.get("businessData"));
        businessData.put("taskId", taskId);
        businessData.put("taskInstanceId", instanceId);
        map.put("businessData", businessData);
        flowConfig = JSONObject.toJSONString(map);
        String flowId = info.get("flow_id");
        String flowVersion = info.get("flow_version");
        FlowOfferId flowOfferId = flowContextsService.startFlows(flowId, flowVersion, flowConfig);
        log.info("the task instance {} has been started, the flow offer id is {}:{}.", instanceId,
                flowOfferId.getTrans().getId(), flowOfferId.getTraceId());

        Map<String, Object> updateFiled = new HashMap<>();
        updateFiled.put("status", FlowTraceStatus.RUNNING.name());
        // task中的flow_context_id实际对应flow中的transId
        updateFiled.put("flow_context_id", flowOfferId.getTrans().getId());
        updateFiled.put("start_time", TimeUtil.getFormatCurTime());
        updateFiled.put("processed_num", "0");
        updateFiled.put("file_num", "0");
        updateFiled.put("cleaning_data", "0");
        updateFiled.put("progress_percent", "0.0");
        updateFiled.put("extensions", "{}");
        updateFiled.put("finish_time", null);

        taskUpdater.updateTaskInstance(taskId, instanceId, updateFiled);
        log.info("Start A3000 task successfully.");
        return flowOfferId.getTrans().getId();
    }

    /**
     * 终止任务
     *
     * @param instanceId 任务实例id
     * @param taskId 任务id
     * @return boolean 是否终止成功
     */
    @Transactional
    public boolean terminateTask(String taskId, String instanceId) {
        log.info("terminate task {}.", instanceId);
        Instance instance = getTaskInfo(instanceId, taskId);
        Map<String, String> info = instance.getInfo();
        String flowTransId = info.get("flow_context_id");

        // 更新流程实例状态
        updateFlowInstance(flowTransId);

        // 更新任务中心状态
        Map<String, Object> updateFiled = new HashMap<>();
        updateFiled.put("status", "TERMINATE");
        taskUpdater.updateTaskInstance(taskId, instanceId, updateFiled);
        log.info("terminate task {} success.", instanceId);
        return true;
    }

    /**
     * 向指定任务追加数据
     *
     * @param taskId 任务id
     * @param instanceId 任务实例id
     * @param data 追加数据，每项对应{@link FlowData}
     */
    public void appendTask(String taskId, String instanceId, List<Map<String, Object>> data) {
        log.info("begin to append, task={}:{}, data count={}.", taskId, instanceId, data.size());
        if (data.isEmpty()) {
            log.info("The last for loop appends, task={}:{}.", taskId, instanceId);
            taskUpdater.updateTaskInfoWithLock(taskId, instanceId, info -> {
                Map<String, Object> updateFields = new HashMap<>();
                Map<String, Object> extensions = Optional.ofNullable(info.get("extensions"))
                        .map(jsonString -> ObjectUtils.<Map<String, Object>>cast(JSONObject.parseObject(jsonString)))
                        .orElse(new HashMap<>());

                // 此时如果trans已经完成，则更新任务完成相关字段
                FlowTraceStatus transStatus = flowContextsService.calculateTransStatus(info.get("flow_context_id"));
                if (FlowTraceStatus.isEndStatus(transStatus)) {
                    updateFields.putAll(taskUpdater.getEndStatusUpdateFields(taskId, instanceId, info, transStatus));
                }
                extensions.put("scanStatus", ScanStatus.END.getCode());
                updateFields.put("extensions", JSONObject.toJSONString(extensions));

                return updateFields;
            });
            return;
        }
        int fileCount = data.size();
        Map<String, Object> contextDataMap = ObjectUtils.cast(data.get(0).get("contextData"));
        Validation.notNull(contextDataMap, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "contextData"));
        String nodeMetaId = ObjectUtils.cast(contextDataMap.get("nodeMetaId"));
        Validation.notBlank(nodeMetaId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "contextData.nodeMetaId"));
        String appendTransId = ObjectUtils.cast(contextDataMap.get("flowTransId"));
        // 追加任务文件总数
        Map<String, String> info = taskUpdater.updateTaskInfoWithLock(taskId, instanceId, taskInfo -> {
            String taskTransId = taskInfo.get("flow_context_id");
            if (!taskTransId.equals(appendTransId)) {
                return null;
            }
            Map<String, Object> updateFields = new HashMap<>();
            updateFields.put("file_num", String.valueOf(Integer.parseInt(taskInfo.get("file_num")) + fileCount));
            return updateFields;
        });

        String flowTransId = info.get("flow_context_id");
        if (!flowTransId.equals(appendTransId)) {
            log.warn("Not same flow trans id, update task instance failed. taskTransId={}, appendTransId={}.",
                    flowTransId, appendTransId);
            return;
        }

        String flowMetaId = info.get("flow_id");
        String flowVersion = info.get("flow_version");
        FlowAppendPoint appendPoint = getFlowAppendPoint(flowMetaId, flowVersion, nodeMetaId);
        FlowData[] flowDataArr = data.stream().map(d -> {
            FlowData flowData = FlowData.parseFrom(d);
            flowData.setOperator(Optional.ofNullable(flowData.getOperator()).orElse(operator));
            return flowData;
        }).toArray(FlowData[]::new);
        appendPoint.getPublisher().publish(flowDataArr, flowTransId);

        log.info("End to append, task={}:{}, transId={}.", taskId, instanceId, flowTransId);
    }

    /**
     * 重试任务失败的数据
     *
     * @param taskId 任务id
     * @param instanceId 任务实例id
     * @param retryList 失败的数据, 格式如下：
     * [
     * {
     * "contextId": "",
     * "origin": {}
     * }
     * ]
     * @return 返回该次重试的traceId
     */
    public String retryCleanTask(String taskId, String instanceId, List<Map<String, Object>> retryList) {
        log.info("Start to retry A3000 task, task={}:{}, retryCount={}.", taskId, instanceId, retryList.size());

        Instance instance = getTaskInfo(instanceId, taskId);
        Map<String, String> info = instance.getInfo();
        String flowTransId = info.get("flow_context_id");
        Validation.notBlank(flowTransId,
                () -> new JobberParamException(FLOW_TASK_OPERATOR_NOT_SUPPORT, "A3000", "retry"));
        int totalFileNum = Integer.parseInt(info.get("file_num"));
        int processedNum = Integer.parseInt(info.get("processed_num"));

        log.info("Retry A3000 task, task={}:{}, totalFileNum={}, processedNum={}.", taskId, instanceId, totalFileNum,
                processedNum);

        Validation.isTrue(totalFileNum == processedNum + retryList.size(),
                () -> new JobberParamException(INPUT_PARAM_IS_INVALID, "file num does not match"));

        String flowConfig = info.get("flow_config");
        Map<String, Object> map = JSONObject.parseObject(flowConfig, Map.class);
        Map<String, Object> businessData = ObjectUtils.cast(map.get("businessData"));
        businessData.put("taskId", taskId);
        businessData.put("taskInstanceId", instanceId);

        // 写入重试参数
        Map<String, Object> params = ObjectUtils.cast(businessData.get("params"));
        params.put("startType", TaskStartType.RETRY.getCode());
        List<Object> flowRetryList = retryList.stream()
                .map(retryData -> retryData.get("origin"))
                .collect(Collectors.toList());
        businessData.put("retryList", flowRetryList);

        map.put("businessData", businessData);
        flowConfig = JSONObject.toJSONString(map);

        List<String> retryContextIds = retryList.stream()
                .map(retryData -> ObjectUtils.<String>cast(retryData.get("contextId")))
                .collect(Collectors.toList());
        // 部分重试时，需要在原有flowTrans下投递数据，否则会替换flowTrans,会丢失任务的文件清单
        // 此时，防止计算flowTrans状态有干扰，直接认为之前的trace为成功，具体flowTrans状态依据新产生trace的状态
        List<String> traceIds = flowContextsService.findTraceIdsByContextIds(retryContextIds);
        flowContextsService.updateTraceStatus(traceIds, FlowTraceStatus.ARCHIVED);
        flowContextsService.deleteFlowContexts(retryContextIds,
                com.huawei.fit.jane.task.util.OperationContext.custom().tenantId(tenantId).operator(operator).build());

        String flowId = info.get("flow_id");
        String flowVersion = info.get("flow_version");
        FlowOfferId flowOfferId = flowContextsService.startFlowsWithTrans(flowId, flowVersion, flowTransId, flowConfig);

        log.info("The task instance {} has been retried, flowOfferId={}:{}.", instanceId,
                flowOfferId.getTrans().getId(), flowOfferId.getTraceId());

        Map<String, Object> updateFiled = new HashMap<>();
        updateFiled.put("status", FlowTraceStatus.RUNNING.name());
        updateFiled.put("start_time", TimeUtil.getFormatCurTime());
        updateFiled.put("extensions", "{}");
        updateFiled.put("finish_time", null);
        taskUpdater.updateTaskInstance(taskId, instanceId, updateFiled);

        log.info("Retry A3000 task successfully, task={}:{}, retryCount={}.", taskId, instanceId, retryList.size());
        return flowOfferId.getTraceId();
    }

    private synchronized FlowAppendPoint getFlowAppendPoint(String flowMetaId, String flowVersion, String nodeMetaId) {
        FlowAppendPoint appendPoint = flowAppendPoints.get(FlowAppendPoint.getFlowId(flowMetaId, flowVersion));
        if (Objects.isNull(appendPoint)) {
            appendPoint = new FlowAppendPoint(flowMetaId, flowVersion, nodeMetaId);
            flowAppendPoints.put(appendPoint.getFlowId(), appendPoint);
            flowContextsService.offerFlowNode(flowMetaId, flowVersion, appendPoint.getNodeMetaId(),
                    appendPoint.getPublisher());
        }
        return appendPoint;
    }

    /**
     * updateFlowInstance
     *
     * @param flowTransId flowTransId
     */
    private void updateFlowInstance(String flowTransId) {
        log.info("Start update flow Instance status terminate, transId:{}", flowTransId);
        OperationContext operationContext = new OperationContext(tenantId, operator, "", "", "");
        flowContextsService.terminateFlowsByTransId(flowTransId, ParamUtils.convertOperationContext(operationContext));
        log.info("update Flow Instance status terminate success, transId:{}", flowTransId);
    }

    /**
     * getTaskInfo
     *
     * @param instanceId instanceId
     * @param taskId taskId
     * @return Instance
     */
    public Instance getTaskInfo(String instanceId, String taskId) {
        Map<String, List<String>> infos = new HashMap<>();
        infos.put("id", Collections.singletonList(instanceId));
        InstanceQueryFilter filter = new InstanceQueryFilter();
        filter.setInfos(infos);

        RangedResultSet<Instance> list = instanceService.list(taskId, filter, 0, 1, false, new OperationContext());
        List<Instance> results = list.getResults();
        if (results.isEmpty()) {
            log.warn("can't found the A3000 task with task id {}, instance id {}", taskId, instanceId);
            throw new JobberParamException(ENTITY_NOT_FOUND, "taskInstance", instanceId);
        }
        return results.get(0);
    }

    /**
     * 删除任务实例
     * 先删除流程实例数据
     *
     * @param taskId 任务id
     * @param instanceId 任务实例id
     */
    public void deleteTask(String taskId, String instanceId) {
        log.info("Start to delete task instance with task id {}, instance id {}", taskId, instanceId);
        Instance info = getTaskInfo(instanceId, taskId);
        String flowTransId = info.getInfo().get("flow_context_id");
        flowContextsService.deleteFlow(flowTransId);
        instanceService.deleteTaskInstance(taskId, instanceId, new OperationContext());
        log.info("Delete task instance success with task id {}, instance id {}", taskId, instanceId);
    }

    /**
     * 创建任务接口
     *
     * @param flowTransInfo 任务全部信息map
     * @param taskId 创建任务实例对应任务定义id
     * @return String
     */
    public synchronized String createTaskInstance(Map<String, Object> flowTransInfo, String taskId) {
        log.info("Start to create A3000 Task, task id: {}.", taskId);
        Map<String, Object> infoMap = ObjectUtils.cast(flowTransInfo.get("info"));
        Object flowConfig = infoMap.get("flow_config");
        if (!(flowConfig instanceof String)) {
            String flowConfigStr = JSONObject.toJSONString(flowConfig);
            infoMap.put("flow_config", flowConfigStr);
        }
        // tag保持为空， sourceId为空
        String typeId = getTypeId(taskId);
        InstanceInfo instanceInfo = new InstanceInfo(typeId, "", infoMap, new ArrayList<>());
        OperationContext operationContext = new OperationContext(tenantId, "", "", "", "");

        try {
            Instance instance = instanceService.createTaskInstance(taskId, instanceInfo, operationContext);
            String instanceId = instance.getInfo().get("id");
            log.info("Creating A3000 task, id: {}", instanceId);
            log.debug("Creating A3000 task, instance info: {}", instanceInfo);
            log.info("Create A3000 Task successfully");
            return instanceId;
        } catch (FitException e) {
            log.error("create instance error:", e);
            throw e;
        }
    }

    private String getTypeId(String taskId) {
        Task task = Optional.ofNullable(taskService.retrieve(taskId, new OperationContext())).orElseThrow(() -> {
            log.error("Find task error, task id: {}.", taskId);
            return new JobberException(UN_EXCEPTED_ERROR);
        });
        List<TaskType> types = task.getTypes();
        if (types.size() != 1) {
            log.error("Create task instance failed, types size: {}, taskId: {}.", types.size(), taskId);
            throw new JobberException(UN_EXCEPTED_ERROR);
        }
        return types.get(0).getId();
    }

    /**
     * 获取全部任务列表接口
     *
     * @param request 获取列表所需要排序字段和顺序的请求
     * @param dataCleanTaskId dataCleanTaskId
     * @return Map<String, Object> 返回展示列表和总数
     */
    public RangedResultSet<Map<String, String>> getDatasetList(CleanDataListQuery request, String dataCleanTaskId) {
        RangedResultSet<Map<String, String>> res;
        try {
            log.info("Start to get A3000 task list by task id {}", dataCleanTaskId);
            log.debug("query params are: title {}, version {}, sortMap {}", request.getTitle(), request.getVersion(),
                    request.getSortMap());

            List<String> orderByList = this.buildOrderByList(request);
            Map<String, List<String>> infosMap = this.buildInfosMap(request);

            InstanceQueryFilter filter = new InstanceQueryFilter();
            filter.setOrderBy(orderByList);
            filter.setInfos(infosMap);
            // operator & operator IP不填
            OperationContext operationContext = new OperationContext(tenantId, "", "", "", "");
            long offset = (request.getPageNum() - 1) * request.getPageSize();
            res = getList(dataCleanTaskId, filter, offset, request.getPageSize(), operationContext);
            Optional<List<Map<String, String>>> optionalList = Optional.ofNullable(res.getResults());
            if (optionalList.isPresent()) {
                List<Map<String, String>> list = optionalList.get();
                compatibleRc1(dataCleanTaskId, list);
            }
            log.info("Get A3000 task list success by task id {}.", dataCleanTaskId);
        } catch (NullPointerException | NumberFormatException | FitException e) {
            log.error("Get A3000 task list error by task id {}.", dataCleanTaskId, e);
            throw new JobberException(SERVER_INTERNAL_ERROR);
        }
        log.debug("Get A3000 task list result: {}", res);
        return res;
    }

    private List<String> buildOrderByList(CleanDataListQuery request) {
        return request.getSortMap()
                .entrySet()
                .stream()
                .filter(entry -> !StringUtils.isBlank(ObjectUtils.cast(entry.getValue().get("type"))))
                .sorted(Comparator.comparingInt(entry -> ObjectUtils.cast(entry.getValue().get("order"))))
                .map(entry -> entry.getValue().get("type") + "(info." + entry.getKey() + ")")
                .collect(Collectors.toList());
    }

    private Map<String, List<String>> buildInfosMap(CleanDataListQuery request) {
        Map<String, List<String>> infosMap = new HashMap<>();
        Optional.ofNullable(request.getTitle())
                .filter(name -> !name.isEmpty())
                .ifPresent(id -> infosMap.put("title", Collections.singletonList(request.getTitle())));

        Optional.ofNullable(request.getVersion())
                .filter(version -> !version.isEmpty())
                .ifPresent(id -> infosMap.put("version", Collections.singletonList(request.getVersion())));

        Optional.ofNullable(request.getTaskId())
                .filter(id -> !id.isEmpty())
                .ifPresent(id -> infosMap.put("id", Collections.singletonList(request.getTaskId())));
        return infosMap;
    }

    private void compatibleRc1(String dataCleanTaskId, List<Map<String, String>> list) {
        for (Map<String, String> map : list) {
            // 为兼容老版本带单位的结果，如果值带单位如10G，直接返回，如果不带单位如100，先转换再返回
            String cleaningData = map.get("cleaning_data");
            try {
                map.put("cleaning_data", convertFromByte(Long.parseLong(cleaningData)));
            } catch (NumberFormatException e) {
                log.error("Get cleaning_data fail, clean data: {}, task id: {}.", cleaningData, dataCleanTaskId);
            }
            adaptRc1(map);
        }
    }

    private static void adaptRc1(Map<String, String> map) {
        // 兼容老版本，没有datasetType时，默认为文本类型
        String datasetType = map.get("datasetType");
        if (StringUtils.isBlank(datasetType)) {
            map.put("datasetType", "1");
        }
        String tag = map.get("tag");
        if (StringUtils.isBlank(tag)) {
            map.put("tag", "data clean");
        }
    }

    private String convertFromByte(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.0");
        String fileSizeString;
        if (fileSize == 0) {
            fileSizeString = "0.0 B";
        } else if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + " B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + " KB";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + " MB";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + " GB";
        }
        return fileSizeString;
    }

    /**
     * 根据flowTransId查询流程实例状态和百分比
     *
     * @param flowTransId 流程定义transId标识列表
     * @param isNewStatus 是否计算新状态的标识
     * @return 状态 对应状态计算Map结果
     */
    public Map<String, Object> findFlowStatusByTransId(String flowTransId, boolean isNewStatus) {
        log.info("Start find flow status, transId={}, newStatus={}.", flowTransId, isNewStatus);
        Map<String, Object> result = flowContextsService.getFlowCompletenessByTransId(flowTransId, isNewStatus);
        log.info("Find flow status successfully, transId={}, newStatus={}.", flowTransId, isNewStatus);

        return result;
    }

    /**
     * 根据任务id获取该列表详细信息
     * 查询清洗任务详情时，只有在任务状态为ERROR或是PARTIAL_ERROR才会去查询errorInfo一起返回，否则不返回errorInfo
     *
     * @param taskId 详细信息的任务id
     * @param dataCleanTaskId 数据清洗任务默认id
     * @return Optional<List < Map < String, String>>> 返回展示列表
     */
    public List<Map<String, String>> getDatasetById(String taskId, String dataCleanTaskId) {
        log.info("Start to get A3000 task by task id {}, instance id {}", dataCleanTaskId, taskId);
        Map<String, Map<String, Object>> sortMap = new HashMap<>();
        CleanDataListQuery request = new CleanDataListQuery();
        request.setTaskId(taskId);
        request.setPageNum(1);
        request.setPageSize(1);
        request.setSortMap(sortMap);
        Optional<List<Map<String, String>>> res;
        try {
            res = Optional.ofNullable(getDatasetList(request, dataCleanTaskId).getResults());
        } catch (FitException e) {
            log.error("get dataset list error, taskId: {}, dataCleanTaskId: {}.", taskId, dataCleanTaskId);
            throw new JobberException(SERVER_INTERNAL_ERROR);
        }
        if (!res.isPresent() || res.get().isEmpty()) {
            log.info("Get A3000 task by task id {}, instance id {} successful.", dataCleanTaskId, taskId);
            return res.orElse(Collections.emptyList());
        }
        List<Map<String, String>> taskInstances = res.get();
        Map<String, String> currentTaskInstance = taskInstances.get(0);
        boolean isStatusError = FlowTraceStatus.ERROR.name().equals(currentTaskInstance.get("status"));
        boolean isStatusPartError = FlowTraceStatus.PARTIAL_ERROR.name().equals(currentTaskInstance.get("status"));
        if (isStatusError || isStatusPartError) {
            // 更新错误详细信息
            String flowTransId = currentTaskInstance.get("flow_context_id");
            if (StringUtils.isBlank(flowTransId)) {
                return taskInstances;
            }
            List<FlowContextPO> contexts = queryFlowContextPersistRepo.findWithoutFlowDataByTransIdList(
                    Collections.singletonList(flowTransId));
            if (contexts.isEmpty()) {
                return taskInstances;
            }
            List<FlowsErrorInfo> flowErrorInfos = new ArrayList<>();
            contexts.stream().map(FlowContextPO::getTraceId).collect(Collectors.toSet()).forEach(traceId -> {
                List<FlowsErrorInfo> flowErrorInfo = flowContextsService.getFlowErrorInfo(traceId);
                flowErrorInfos.addAll(flowErrorInfo);
            });
            currentTaskInstance.put("errorInfo", JSONObject.toJSONString(flowErrorInfos));
            log.info("Get A3000 task by task id {}, instance id {} successful.", dataCleanTaskId, taskId);
            return taskInstances;
        } else {
            log.info("Get A3000 task by task id {}, instance id {} successful.", dataCleanTaskId, taskId);
            return res.orElse(Collections.emptyList());
        }
    }

    /**
     * 根据filter限制获取排序完成后的任务列表
     *
     * @param taskId 详细信息的任务id
     * @param filter 排序字段和顺序filter
     * @param offset 展示页面offset
     * @param limit 展示数量
     * @param context 操作人上下文
     * @return Map<String, Object> 返回展示列表和总数
     */
    public RangedResultSet<Map<String, String>> getList(String taskId, InstanceQueryFilter filter, long offset,
            int limit, OperationContext context) {
        log.info("instance query filter params are orderby {}, infos {}.", filter.getOrderBy(), filter.getInfos());
        RangedResultSet<Instance> list = instanceService.list(taskId, filter, offset, limit, false, context);
        Optional<List<Map<String, String>>> resultList = Optional.ofNullable(list.getResults())
                .filter(r -> !r.isEmpty())
                .map(r -> r.stream().map(Instance::getInfo).collect(Collectors.toList()));
        if (!resultList.isPresent()) {
            log.error("Task:{} dataset info was not found.", taskId);
        }
        return RangedResultSet.create(resultList.orElse(new ArrayList<>()), (int) offset, limit,
                list.getRange().getTotal());
    }

    /**
     * 批量获取ohScript代码
     *
     * @param fitableIds 请求体，包含fitableId列表与type列表
     * @return List<String> 对应ohScript代码
     */
    public List<String> getScript(List<List<String>> fitableIds) {
        log.info("get ohScript code for fitable ids: {}", fitableIds);
        List<String> response = new ArrayList<>();
        for (List<String> list : fitableIds) {
            if (CollectionUtils.isEmpty(list)) {
                response.add("ext::context");
                continue;
            }
            if (conditionFitableCode.containsKey(list.get(0))) {
                response.add(conditionScript(list));
            } else {
                response.add(flowsService.getScript(list));
            }
        }
        log.info("Get script success.");
        return response;
    }

    private String conditionScript(List<String> fitableIds) {
        StringBuilder script = new StringBuilder();
        script.append("var type = ext::context.get(0).get(\"passData\").get(\"meta\").get(\"fileType\");\n");
        fitableIds.forEach(id -> {
            if (!conditionFitableCode.containsKey(id)) {
                throw new JobberException(INPUT_PARAM_IS_INVALID, id);
            }
            script.append(conditionFitableCode.get(id));
        });
        // 不支持类型抛类型不支持异常
        script.append("else {\n" + "        let context1 = entity{\n"
                + "        .async = true; .id = \"5abc15280ea44cada216e094e3a76937\";\n" + "    };\n"
                + "    let f1 = fit::handleTask(context1);\n" + "    ext::context >> f1\n" + "}");
        return script.toString();
    }

    /**
     * checkTaskInstance
     *
     * @param flowId flowId
     * @param version version
     * @param taskId taskId
     */
    public void checkTaskInstance(String flowId, String version, String taskId) {
        log.info("Start to check task instance, flowId: {}, version: {}, tasKId: {}.", flowId, version, taskId);
        InstanceQueryFilter filter = new InstanceQueryFilter();
        Map<String, List<String>> infos = new HashMap<>();
        List<String> status = Arrays.asList("INIT", FlowTraceStatus.PARTIAL_ERROR.name(),
                FlowTraceStatus.RUNNING.name(), FlowTraceStatus.TERMINATE.name(), FlowTraceStatus.ERROR.name());
        infos.put("status", status);
        infos.put("flow_id", Collections.singletonList(flowId));
        infos.put("flow_version", Collections.singletonList(version));
        filter.setInfos(infos);
        RangedResultSet<Instance> list = instanceService.list(taskId, filter, 0, 1, false,
                new OperationContext(tenantId, "", "", "", ""));
        List<Instance> resultList = Optional.ofNullable(list.getResults())
                .orElseThrow(() -> new JobberException(FLOW_DEFINITION_DELETE_ERROR));
        if (!resultList.isEmpty()) {
            log.warn("The task has unexecute instance, cannot be deleted, task id: {}.", taskId);
            throw new JobberException(FLOW_DEFINITION_DELETE_ERROR);
        }
    }

    /**
     * 分页查询清洗任务结果
     *
     * @param taskId 任务定义id
     * @param instanceId 任务实例id
     * @param pageNum 第几页
     * @param limit 每页数量
     * @param status 查询文件状态
     * @return 清洗结果
     */
    public CleanTaskPageResult findCleanTaskPageResult(String taskId, String instanceId, Integer pageNum, Integer limit,
            String status) {
        log.info("Start find clean task page result, instance id: {}.", instanceId);
        Instance instance = getTaskInfo(instanceId, taskId);
        Map<String, String> instanceInfo = instance.getInfo();
        String flowTransId = instanceInfo.get("flow_context_id");
        String endNode = getEndNode(flowTransId);
        List<FlowContext<FlowData>> contexts = new ArrayList<>();
        int totalNum = 0;
        // pageNum为0时返回全量结果
        if (pageNum == 0) {
            contexts = flowContextPersistRepo.findFinishedContextsByTransId(flowTransId, endNode);
            totalNum = flowContextPersistRepo.findFinishedPageNumByTransId(flowTransId, endNode);
        } else if (StringUtils.isBlank(status)) {
            contexts = flowContextPersistRepo.findFinishedContextsPagedByTransId(flowTransId, endNode, pageNum, limit);
            totalNum = flowContextPersistRepo.findFinishedPageNumByTransId(flowTransId, endNode);
        } else if (status.equals(FlowNodeStatus.ARCHIVED.name())) {
            contexts = flowContextPersistRepo.getEndContextsPagedByTransId(flowTransId, endNode, pageNum, limit);
            totalNum = flowContextPersistRepo.findEndContextsPageNumByTransId(flowTransId, endNode);
        } else if (status.equals(FlowNodeStatus.ERROR.name())) {
            contexts = flowContextPersistRepo.getErrorContextsPagedByTransId(flowTransId, pageNum, limit);
            totalNum = flowContextPersistRepo.findErrorContextsPageNumByTransId(flowTransId);
        } else {
            log.warn("Unknown query condition, status={}.", status);
        }
        return buildCleanTaskPageResult(totalNum, contexts);
    }

    /**
     * 分页查询清洗任务结果
     *
     * @param taskId 任务定义id
     * @param instanceId 任务实例id
     * @param traceId 一批数据投递后的跟踪id
     * @param pageNum 第几页
     * @param limit 每页数量
     * @param status 查询文件状态
     * @return 清洗结果
     */
    public CleanTaskPageResult findCleanTaskPageResultByTraceId(String taskId, String instanceId, String traceId,
            Integer pageNum, Integer limit, String status) {
        log.info("Start to find clean task page result, task={}:{}, traceId={}.", taskId, instanceId, traceId);
        Instance instance = getTaskInfo(instanceId, taskId);
        Map<String, String> instanceInfo = instance.getInfo();
        String flowTransId = instanceInfo.get("flow_context_id");
        String endNode = getEndNode(flowTransId);
        List<FlowContext<FlowData>> contexts = new ArrayList<>();
        int totalNum = 0;
        // pageNum为0时返回全量结果
        if (pageNum == 0) {
            contexts = flowContextPersistRepo.findFinishedContextsByTraceId(traceId, endNode);
            totalNum = flowContextPersistRepo.findFinishedPageNumByTraceId(traceId, endNode);
        } else if (StringUtils.isBlank(status)) {
            contexts = flowContextPersistRepo.findFinishedContextsPagedByTraceId(traceId, endNode, pageNum, limit);
            totalNum = flowContextPersistRepo.findFinishedPageNumByTraceId(traceId, endNode);
        } else if (status.equals(FlowNodeStatus.ARCHIVED.name())) {
            contexts = flowContextPersistRepo.getEndContextsPagedByTraceId(traceId, endNode, pageNum, limit);
            totalNum = flowContextPersistRepo.findEndContextsPageNumByTraceId(traceId, endNode);
        } else if (status.equals(FlowNodeStatus.ERROR.name())) {
            contexts = flowContextPersistRepo.getErrorContextsPagedByTraceId(traceId, pageNum, limit);
            totalNum = flowContextPersistRepo.findErrorContextsPageNumByTraceId(traceId);
        } else {
            log.warn("Unknown query condition, status={}, traceId={}.", status, traceId);
        }
        return buildCleanTaskPageResult(totalNum, contexts);
    }

    private static CleanTaskPageResult buildCleanTaskPageResult(int totalNum, List<FlowContext<FlowData>> contexts) {
        List<Map<String, Object>> result = new ArrayList<>();
        contexts.forEach(context -> {
            Map<String, Object> res = new HashMap<>();
            res.put("contextId", context.getId());
            res.put("status", context.getStatus().toString());
            res.put("errorInfo", context.getData().getErrorInfo());
            res.put("businessData", context.getData().getBusinessData());
            result.add(res);
        });
        CleanTaskPageResult cleanTaskPageResult = new CleanTaskPageResult();
        cleanTaskPageResult.setTotalNum(totalNum);
        cleanTaskPageResult.setResult(result);
        return cleanTaskPageResult;
    }

    private String getEndNode(String flowTransId) {
        String streamId = Optional.ofNullable(flowContextPersistRepo.getStreamIdByTransId(flowTransId))
                .orElseThrow(() -> new JobberException(ENTITY_NOT_FOUND, "streamId", flowTransId));
        FlowDefinition flowDefinition = Optional.ofNullable(flowDefinitionRepo.findByStreamId(streamId))
                .orElseThrow(() -> new JobberException(ENTITY_NOT_FOUND, "flowDefinition", streamId));
        return flowDefinition.getEndNode();
    }

    /**
     * updateTaskInstance
     *
     * @param taskId taskId
     * @param taskInstanceId taskInstanceId
     * @param updateFields updateFields
     */
    public void updateTaskInstance(String taskId, String taskInstanceId, Map<String, Object> updateFields) {
        taskUpdater.updateTaskInstance(taskId, taskInstanceId, updateFields);
    }

    /**
     * updateTaskInfoWithLock
     *
     * @param taskId taskId
     * @param taskInstanceId taskInstanceId
     * @param getUpdateFields getUpdateFields
     */
    public void updateTaskInfoWithLock(String taskId, String taskInstanceId,
            Function<Map<String, String>, Map<String, Object>> getUpdateFields) {
        taskUpdater.updateTaskInfoWithLock(taskId, taskInstanceId, getUpdateFields);
    }
}
