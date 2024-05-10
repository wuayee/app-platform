/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.jober.bff.service.a3000;

import static com.huawei.fit.jober.common.Constant.STREAM_ID_SEPARATOR;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_GRAPH_AMOUNT_EXCEED_LIMITATION;
import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;
import static com.huawei.fit.jober.common.ErrorCodes.SERVER_INTERNAL_ERROR;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.flow.graph.entity.FlowSaveEntity;
import com.huawei.fit.jober.bff.client.flowsengine.request.CleanDataListQuery;
import com.huawei.fit.jober.bff.service.FlowsEngineWebService;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.flowsengine.biz.service.FlowContextsService;
import com.huawei.fit.jober.flowsengine.biz.service.FlowsService;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.jober.taskcenter.controller.Views;
import com.huawei.fit.jober.taskcenter.service.TagService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.hisp.clean.client.FlowConfiguration;
import com.huawei.hisp.clean.client.QueryCriteria;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * a3000接口
 *
 * @author y00679285
 * @since 2023/10/30
 */
@Component
public class OrchestratorFitService {
    private static final Logger log = Logger.get(OrchestratorFitService.class);

    private final OrchestratorService orchestratorService;

    private final FlowContextsService flowContextsService;

    private final FlowsService flowsService;

    private final FlowsEngineWebService flowsEngineService;

    private final String tenantId;

    private final String operator;

    private final FlowLocks locks;

    private final TagService tagService;

    public OrchestratorFitService(OrchestratorService orchestratorService, FlowContextsService flowContextsService,
            FlowsService flowsService, FlowsEngineWebService flowsEngineService,
            @Value("${a3000.tenantId}") String tenantId,
            @Value("${a3000.operator}") String operator, FlowLocks locks, TagService tagService) {
        this.orchestratorService = orchestratorService;
        this.flowContextsService = flowContextsService;
        this.flowsService = flowsService;
        this.flowsEngineService = flowsEngineService;
        this.tenantId = tenantId;
        this.operator = operator;
        this.locks = locks;
        this.tagService = tagService;
    }

    /**
     * 终止任务接口
     *
     * @param instanceId 任务实例id
     * @param taskId 任务id
     */
    public void terminateCleanTask(String instanceId, String taskId) {
        orchestratorService.terminateTask(taskId, instanceId);
    }

    /**
     * 执行任务接口
     *
     * @param instanceId 任务实例id
     * @param taskId 任务id
     * @return String 流程实例id
     */
    public String executeCleanTask(String instanceId, String taskId) {
        return orchestratorService.startTask(taskId, instanceId);
    }

    /**
     * 创建任务接口
     *
     * @param flowTransInfo 任务全部信息map
     * @param taskId 任务id
     * @return String
     */
    @Fitable(id = "a2ea3880872c50a95a8844ac1bf5b95e")
    public String createCleanTask(Map<String, Object> flowTransInfo, String taskId) {
        return orchestratorService.createTaskInstance(flowTransInfo, taskId);
    }

    /**
     * 向指定任务追加数据
     *
     * @param taskId 任务id
     * @param instanceId 任务实例id
     * @param data 追加数据，每项对应{@link FlowData}
     */
    public void appendCleanTask(String taskId, String instanceId, List<Map<String, Object>> data) {
        orchestratorService.appendTask(taskId, instanceId, data);
    }

    /**
     * 获取全部任务列表接口
     *
     * @param request 获取列表所需要排序字段和顺序的请求
     * @param dataCleanTaskId dataCleanTaskId
     * @return Map<String, String> 返回展示列表和总数
     */
    public RangedResultSet<Map<String, String>> getCleanDatasetList(CleanDataListQuery request,
            String dataCleanTaskId) {
        try {
            return orchestratorService.getDatasetList(request, dataCleanTaskId);
        } catch (FitException e) {
            log.error("GetCleanDatasetList error", e);
            throw new JobberException(SERVER_INTERNAL_ERROR);
        }
    }

    /**
     * 根据任务id获取该列表详细信息
     *
     * @param taskId 详细信息的任务id
     * @param dataCleanTaskId 数据清洗任务默认id
     * @return Optional<List < Map < String, String>>> 返回展示列表
     */
    public List<Map<String, String>> getCleanDatasetListById(String taskId, String dataCleanTaskId) {
        return orchestratorService.getDatasetById(taskId, dataCleanTaskId);
    }

    /**
     * 删除模板接口
     *
     * @param flowId 流程定义ID
     * @param version 流程定义版本
     * @param dataCleanTaskId dataCleanTaskId
     */
    public void deleteFlow(String flowId, String version, String dataCleanTaskId) {
        orchestratorService.checkTaskInstance(flowId, version, dataCleanTaskId);
        flowsEngineService.forceDeleteFlows(flowId, version, getContext());
    }

    /**
     * 创建模板接口
     *
     * @param flowId flowId
     * @param version version
     * @param flowConfiguration flowConfiguration
     * @param dataCleanTaskId dataCleanTaskId
     * @param limit limit
     */
    public void createFlow(String flowId, String version, FlowConfiguration flowConfiguration, String dataCleanTaskId,
            Integer limit) {
        Lock lock = this.locks.getDistributedLock(StringUtils.join(STREAM_ID_SEPARATOR, "createFlow", flowId, version));
        lock.lock();
        try {
            List<String> flowIds = tagService.list("FLOW GRAPH", flowConfiguration.getTags());
            if (flowIds.size() >= limit) {
                log.error("Flow graph amount exceeds limitation {}", limit);
                throw new JobberParamException(FLOW_GRAPH_AMOUNT_EXCEED_LIMITATION, limit);
            }
            FlowSaveEntity flowSaveEntity = new FlowSaveEntity();
            flowSaveEntity.setId(flowId);
            flowSaveEntity.setVersion(version);
            flowSaveEntity.setTags(flowConfiguration.getTags());
            flowSaveEntity.setGraphData(getNewGraphData(flowConfiguration.getDefinitionData()));
            flowsEngineService.publishFlows(flowSaveEntity, getContext());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 查询模板详情接口
     *
     * @param queryCriteria queryCriteria
     * @param dataCleanTaskId dataCleanTaskId
     * @return 模板定义和分页参数
     */
    public Optional<Map<String, Object>> getAllFlows(QueryCriteria queryCriteria, String dataCleanTaskId) {
        if (queryCriteria == null) {
            return Optional.of(
                    Views.viewOf(flowsEngineService.findFlowList(null, new ArrayList<>(), 0, 10, getContext()),
                            "definitions", Views::viewOfFlowGraphDefinition));
        }
        List<String> tagList = queryCriteria.getTag() == null
                ? new ArrayList<>()
                : Arrays.stream(queryCriteria.getTag().split(",")).collect(Collectors.toList());
        String offset = queryCriteria.getOffset() == null ? "0" : queryCriteria.getOffset();
        String limit = queryCriteria.getLimit() == null ? "10" : queryCriteria.getLimit();
        return Optional.of(Views.viewOf(
                flowsEngineService.findFlowList(queryCriteria.getCreateUser(), tagList, Integer.parseInt(offset),
                        Integer.parseInt(limit), getContext()), "definitions", Views::viewOfFlowGraphDefinition));
    }

    /**
     * 根据id和version查询模板详情接口
     *
     * @param flowId 模板id
     * @param version 版本号
     * @param dataCleanTaskId dataCleanTaskId
     * @return 模板详情
     */
    public Optional<Map<String, Object>> getFlowConfigById(String flowId, String version, String dataCleanTaskId) {
        Map<String, Object> definitions = Views.viewOfFlowInfo(
                flowsEngineService.getFlows(flowId, version, getContext()));
        return Optional.of(definitions);
    }

    /**
     * 批量获取ohScript代码
     *
     * @param fitableIds 请求体，包含fitableId列表与type列表
     * @return List<String> 对应ohScript代码
     */
    public List<String> getScript(List<List<String>> fitableIds) {
        return orchestratorService.getScript(fitableIds);
    }

    /**
     * 启动任务
     *
     * @param metaId 流程定义id
     * @param version 流程定义版本
     * @param flowData 流程运行时配置
     * @return String 流程实例id
     */
    public String startTask(String metaId, String version, String flowData) {
        return flowContextsService.startFlows(metaId, version, flowData).getTrans().getId();
    }

    /**
     * 批量查询流程状态和百分比
     *
     * @param flowTransId 流程实例id列表
     * @return String 实例状态
     */
    public Map<String, Object> getFlowStatus(String flowTransId) {
        return orchestratorService.findFlowStatusByTransId(flowTransId, false);
    }

    /**
     * 批量查询流程状态和百分比
     *
     * @param flowTransId 流程实例id列表
     * @return String 实例状态
     */
    public Map<String, Object> getFlowStatusNew(String flowTransId) {
        return orchestratorService.findFlowStatusByTransId(flowTransId, true);
    }

    /**
     * 删除任务接口
     *
     * @param instanceId 任务实例id
     * @param taskId 任务id
     */
    public void deleteCleanTask(String instanceId, String taskId) {
        orchestratorService.deleteTask(taskId, instanceId);
    }

    /**
     * 根据fitable id查询流程定义
     *
     * @param fitableId fitable id
     * @param offset offset
     * @param limit limit
     * @return 流程定义列表和总数
     */
    public Map<String, Object> findFlowsByFitableId(String fitableId, Integer limit, Integer offset) {
        Validation.notNull(fitableId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "fitable id"));
        Validation.notNull(limit, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "limit"));
        Validation.notNull(offset, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "offset"));
        return flowsService.getFlowDefinitionByFitable(fitableId, offset, limit);
    }

    /**
     * 根据fitable id列表批量查询流程定义数量
     *
     * @param fitableIds fitable id列表
     * @return 流程定义计数
     */
    public Map<String, Integer> findCountByFitableIds(List<String> fitableIds) {
        Validation.notEmpty(fitableIds, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "fitable id list"));
        return flowsService.getCountByFitable(fitableIds);
    }

    private OperationContext getContext() {
        OperationContext context = new OperationContext();
        context.setOperator(operator);
        context.setName("A3000");
        context.setTenantId(tenantId);
        return context;
    }

    private String getNewGraphData(String data) {
        JSONObject json = JSONObject.parseObject(data);
        JSONObject page = ObjectUtils.cast(json.getJSONArray("pages").get(0));
        String pageId = page.getString("id");
        JSONArray shapes = page.getJSONArray("shapes");
        List<String> shapeIds = shapes.stream()
                .map(shape -> ObjectUtils.<JSONObject>cast(shape).getString("id"))
                .collect(Collectors.toList());

        StringBuilder newData = new StringBuilder(data);
        replaceIdInJsonString(newData, pageId, "elsa-page:" + generateId());

        for (String shapeId : shapeIds) {
            replaceIdInJsonString(newData, shapeId, generateId());
        }
        return newData.toString();
    }

    private String generateId() {
        Random random = new Random();
        int firstPart = random.nextInt(46656);
        int secondPart = random.nextInt(46656);
        String firstPartStr = "000" + Integer.toString(firstPart, 36);
        String secondPartStr = "000" + Integer.toString(secondPart, 36);
        return firstPartStr.substring(firstPartStr.length() - 3) + secondPartStr.substring(secondPartStr.length() - 3);
    }

    private void replaceIdInJsonString(StringBuilder json, String oldId, String newId) {
        int startIndex = json.indexOf(oldId);
        while (startIndex != -1) {
            json.replace(startIndex, startIndex + oldId.length(), newId);
            startIndex = json.indexOf(oldId, startIndex + newId.length());
        }
    }
}
