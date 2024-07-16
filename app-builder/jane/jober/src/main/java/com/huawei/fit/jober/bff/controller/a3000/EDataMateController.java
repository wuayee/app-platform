/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.bff.controller.a3000;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.jober.bff.client.flowsengine.request.CleanDataListQuery;
import com.huawei.fit.jober.bff.controller.a3000.entity.CleanTaskPageResult;
import com.huawei.fit.jober.bff.controller.a3000.entity.FlowConfiguration;
import com.huawei.fit.jober.bff.controller.a3000.entity.QueryCriteria;
import com.huawei.fit.jober.bff.service.a3000.OrchestratorFitService;
import com.huawei.fit.jober.bff.service.a3000.OrchestratorService;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.taskcenter.controller.Views;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * a3000 http接口
 *
 * @author 孙怡菲 s00664640
 * @since 2023-12-28
 */
@Component
@RequestMapping(value = "/a3000", group = "A3000 controller")
public class EDataMateController implements DataCleanClientV2 {
    private static final Logger log = Logger.get(EDataMateController.class);

    private final OrchestratorFitService orchestratorFitService;

    private final OrchestratorService orchestratorService;

    public EDataMateController(OrchestratorFitService orchestratorFitService, OrchestratorService orchestratorService) {
        this.orchestratorFitService = orchestratorFitService;
        this.orchestratorService = orchestratorService;
    }

    /**
     * viewOfCleanTasks
     *
     * @param res res
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOfCleanTasks(RangedResultSet<Map<String, String>> res) {
        Map<String, Object> resNew = new HashMap<>();
        resNew.put("total", res.getRange().getTotal());
        resNew.put("instances", res.getResults());
        return resNew;
    }

    /**
     * 删除模板
     *
     * @param flowId 流程定义ID
     * @param version 流程定义版本
     * @param taskId taskId
     */
    @DeleteMapping(value = "/flows/{flow_id}/versions/{version}/task-ids/{task_id}")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void deleteFlow(@PathVariable("flow_id") String flowId, @PathVariable("version") String version,
            @PathVariable("task_id") String taskId) {
        runWithTime("deleteFlow", () -> {
            Validation.notBlank(flowId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowId"));
            Validation.notBlank(version, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "version"));
            Validation.notBlank(taskId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "taskId"));
            orchestratorFitService.deleteFlow(flowId, version, taskId);
        });
    }

    /**
     * 根据id和version查询模板详情
     *
     * @param flowId 模板id
     * @param version 版本号
     * @return 模板详情
     */
    @GetMapping(value = "/flows/{flow_id}/versions/{version}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findFlow(@PathVariable("flow_id") String flowId,
            @PathVariable("version") String version) {
        return orchestratorFitService.getFlowConfigById(flowId, version, null).orElse(new HashMap<>());
    }

    /**
     * 创建模板
     *
     * @param flowId 模板id
     * @param version 版本号
     * @param limit limit
     * @param flowConfiguration 模板数据
     */
    @PostMapping(value = "/flows/{flow_id}/versions/{version}/limit/{limit}")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public void createFlow(@PathVariable("flow_id") String flowId, @PathVariable("version") String version,
            @PathVariable("limit") Integer limit, @RequestBody FlowConfiguration flowConfiguration) {
        orchestratorFitService.createFlow(flowId, version, flowConfiguration, null, limit);
    }

    /**
     * 分页查询模板列表
     *
     * @param queryCriteria 查询参数
     * @return 模板定义和分页参数
     */
    @PostMapping(value = "/flows/list")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> getFlowList(@RequestBody QueryCriteria queryCriteria) {
        return runWithTime("getFlowList",
                () -> orchestratorFitService.getAllFlows(queryCriteria, null).orElse(new HashMap<>()));
    }

    private void runWithTime(String name, Runnable runner) {
        runWithTime(name, () -> {
            runner.run();
            return null;
        });
    }

    private <T> T runWithTime(String name, Supplier<T> runner) {
        long begin = System.currentTimeMillis();
        T result = runner.get();
        log.warn("run " + name + " in {} ms.", (System.currentTimeMillis() - begin));
        return result;
    }

    /**
     * 查询流程状态和百分比
     *
     * @param flowTransId 流程实例id
     * @return 实例状态和百分比
     */
    @GetMapping(value = "/status/{trans_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> getFlowStatus(@PathVariable("trans_id") String flowTransId) {
        return runWithTime("getFlowStatus", () -> orchestratorFitService.getFlowStatus(flowTransId));
    }

    /**
     * 查询流程状态和百分比（t2使用）
     *
     * @param flowTransId 流程实例id
     * @return 实例状态和百分比
     */
    @GetMapping(value = "/status/{trans_id}/new")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> getFlowStatusNew(@PathVariable("trans_id") String flowTransId) {
        return runWithTime("getFlowStatusNew", () -> orchestratorFitService.getFlowStatusNew(flowTransId));
    }

    /**
     * 启动流程
     *
     * @param flowId 流程定义id
     * @param version 流程定义版本
     * @param flowData 流程运行时配置
     * @return 流程实例id
     */
    @PostMapping(value = "/flows/{flow_id}/versions/{version}/start")
    @ResponseStatus(HttpResponseStatus.OK)
    public String startFlow(@PathVariable("flow_id") String flowId, @PathVariable("version") String version,
            @RequestBody String flowData) {
        return runWithTime("StartFlow", () -> orchestratorFitService.startTask(flowId, version, flowData));
    }

    /**
     * 创建任务实例
     *
     * @param flowTransInfo 任务实例信息
     * @param taskId 任务id
     * @return String
     */
    @PostMapping(value = "/tasks/{task_id}/instances")
    @ResponseStatus(HttpResponseStatus.CREATED)
    public String createCleanTask(@PathVariable("task_id") String taskId,
            @RequestBody Map<String, Object> flowTransInfo) {
        return runWithTime("createCleanTask", () -> orchestratorFitService.createCleanTask(flowTransInfo, taskId));
    }

    /**
     * 根据任务实例id获得任务实例详情
     *
     * @param taskId 任务id
     * @param instanceId 任务实例id
     * @return 返回展示列表
     */
    @GetMapping(value = "/tasks/{task_id}/instances/{instance_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public List<Map<String, String>> getCleanTaskById(@PathVariable("task_id") String taskId,
            @PathVariable("instance_id") String instanceId) {
        return runWithTime("getCleanTaskById",
                () -> orchestratorFitService.getCleanDatasetListById(instanceId, taskId));
    }

    /**
     * 根据任务实例id查询任务执行完结果
     *
     * @param taskId 任务Id
     * @param instanceId 任务实例id
     * @param pageNum 页数
     * @param limit 每页条数
     * @param status 文件状态
     * @return 文件执行结果列表
     */
    @GetMapping(value = "/tasks/{task_id}/instances/{instance_id}/result")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findCleanTaskPageResult(@PathVariable("task_id") String taskId,
            @PathVariable("instance_id") String instanceId, @RequestParam("pageNum") Integer pageNum,
            @RequestParam("limit") Integer limit, @RequestParam("status") String status) {
        return runWithTime("findCleanTaskPageResult", () -> {
            Validation.notBlank(taskId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "task_id"));
            Validation.notBlank(instanceId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "instanceId"));
            Validation.notNull(pageNum, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "pageNum"));
            Validation.notNull(limit, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "limit"));
            CleanTaskPageResult cleanTaskPageResult = orchestratorService.findCleanTaskPageResult(taskId, instanceId,
                    pageNum, limit, status);
            return Views.viewOf(cleanTaskPageResult);
        });
    }

    /**
     * 根据任务实例id和traceId查询任务执行完结果
     *
     * @param taskId 任务Id
     * @param instanceId 任务实例id
     * @param pageNum 页数
     * @param limit 每页条数
     * @param status 文件状态
     * @param traceId traceId
     * @return 文件执行结果列表
     */
    @GetMapping(value = "/tasks/{taskId}/instances/{instanceId}/result/{traceId}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findCleanTaskPageResultByTraceId(@PathVariable("taskId") String taskId,
            @PathVariable("instanceId") String instanceId, @PathVariable("traceId") String traceId,
            @RequestParam("pageNum") Integer pageNum, @RequestParam("limit") Integer limit,
            @RequestParam("status") String status) {
        Validation.notBlank(taskId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "task_id"));
        Validation.notBlank(instanceId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "instanceId"));
        Validation.notBlank(traceId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "traceId"));
        Validation.notNull(pageNum, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "pageNum"));
        Validation.notNull(limit, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "limit"));
        CleanTaskPageResult cleanTaskPageResult = orchestratorService.findCleanTaskPageResultByTraceId(taskId,
                instanceId, traceId, pageNum, limit, status);
        return Views.viewOf(cleanTaskPageResult);
    }

    /**
     * 删除任务实例
     *
     * @param instanceId 任务实例id
     * @param taskId 任务id
     */
    @DeleteMapping(value = "/tasks/{task_id}/instances/{instance_id}")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void deleteCleanTask(@PathVariable("task_id") String taskId,
            @PathVariable("instance_id") String instanceId) {
        orchestratorFitService.deleteCleanTask(instanceId, taskId);
    }

    /**
     * 分页查询任务实例
     *
     * @param taskId taskId
     * @param request 获取列表所需要排序字段和顺序的请求
     * @return 返回展示列表和总数
     */
    @PostMapping(value = "/tasks/{task_id}/instances/list")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> getCleanDatasetList(@PathVariable("task_id") String taskId,
            @RequestBody CleanDataListQuery request) {
        return runWithTime("getCleanDatasetList",
                () -> viewOfCleanTasks(orchestratorFitService.getCleanDatasetList(request, taskId)));
    }

    /**
     * 启动任务实例
     *
     * @param instanceId 任务实例id
     * @param taskId 任务id
     * @return 流程实例id
     */
    @GetMapping(value = "/tasks/{task_id}/instances/{instance_id}/execute")
    @ResponseStatus(HttpResponseStatus.OK)
    public String executeCleanTask(@PathVariable("task_id") String taskId,
            @PathVariable("instance_id") String instanceId) {
        return orchestratorFitService.executeCleanTask(instanceId, taskId);
    }

    /**
     * 终止任务实例
     *
     * @param instanceId 任务实例id
     * @param taskId 任务id
     */
    @GetMapping(value = "/tasks/{task_id}/instances/{instance_id}/terminate")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void terminateCleanTask(@PathVariable("task_id") String taskId,
            @PathVariable("instance_id") String instanceId) {
        orchestratorFitService.terminateCleanTask(instanceId, taskId);
    }

    /**
     * 向指定任务追加数据
     *
     * @param taskId 任务id
     * @param instanceId 任务实例id
     * @param data 追加数据，每项对应{@link com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData}
     */
    @PostMapping(value = "/tasks/{taskId}/instances/{instanceId}/append")
    @ResponseStatus(HttpResponseStatus.OK)
    public void appendCleanTask(@PathVariable("taskId") String taskId, @PathVariable("instanceId") String instanceId,
            @RequestBody List<Map<String, Object>> data) {
        orchestratorFitService.appendCleanTask(taskId, instanceId, data);
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
    @PostMapping(value = "/tasks/{taskId}/instances/{instanceId}/retry")
    @ResponseStatus(HttpResponseStatus.OK)
    public String retryCleanTask(@PathVariable("taskId") String taskId, @PathVariable("instanceId") String instanceId,
            @RequestBody List<Map<String, Object>> retryList) {
        return orchestratorService.retryCleanTask(taskId, instanceId, retryList);
    }

    /**
     * 批量获取ohScript代码
     *
     * @param fitableIds fitableId列表
     * @return 对应ohScript代码
     */
    @PostMapping(value = "/script")
    @ResponseStatus(HttpResponseStatus.OK)
    public List<String> getScripts(@RequestBody List<List<String>> fitableIds) {
        return orchestratorFitService.getScript(fitableIds);
    }

    /**
     * 根据fitable id分页查询流程定义
     *
     * @param fitableId fitable id
     * @param offset offset
     * @param limit limit
     * @return 流程定义列表和总数
     */
    @GetMapping(value = "/fitable-usage/{fitable_id}")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> findFlowsByFitableId(@PathVariable("fitable_id") String fitableId,
            @RequestParam("limit") Integer limit, @RequestParam("offset") Integer offset) {
        return runWithTime("findFlowsByFitableId",
                () -> orchestratorFitService.findFlowsByFitableId(fitableId, limit, offset));
    }

    /**
     * 根据fitable id列表批量查询流程定义数量
     *
     * @param fitableIds fitable id列表
     * @return 流程定义计数
     */
    @PostMapping(value = "/fitable-usage/count")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Integer> findCountByFitableIds(@RequestBody List<String> fitableIds) {
        return runWithTime("findCountByFitableIds", () -> orchestratorFitService.findCountByFitableIds(fitableIds));
    }

    @Fitable("09fca09a5d2a4d4887b8a1559b258359")
    @Override
    public Map<String, Object> getAllFlowsV2(QueryCriteria queryCriteria, String dataCleanTaskId) {
        return orchestratorFitService.getAllFlows(queryCriteria, dataCleanTaskId).orElse(null);
    }

    @Fitable("3c265fc0c02a4e67893743e9e60e4f60")
    @Override
    public Map<String, Object> getFlowConfigByIdV2(String flowId, String version, String dataCleanTaskId) {
        return orchestratorFitService.getFlowConfigById(flowId, version, dataCleanTaskId).orElse(null);
    }
}
