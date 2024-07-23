/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.service;

import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.waterflow.edatamate.enums.ScanStatus;
import com.huawei.fit.waterflow.edatamate.enums.TaskStartType;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 更新任务状态，用于初始化文件数量，是否分批扫描数据
 *
 * @author s00558940
 * @since 2024/2/26
 */
@Component
public class TaskUpdateScanStatus implements FlowableService {
    private static final Logger log = Logger.get(TaskFeedbackImpl.class);

    private final OrchestratorService orchestratorService;

    public TaskUpdateScanStatus(OrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @Override
    @Fitable(id = "fd04d6f24ecd4ebb8f889014a441cc63")
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowDataList) {
        if (flowDataList.isEmpty()) {
            log.warn("No flow data.");
            return flowDataList;
        }
        Map<String, Object> flowData = flowDataList.get(0);
        Map<String, Object> businessData = ObjectUtils.cast(flowData.get("businessData"));
        Map<String, Object> contextData = ObjectUtils.cast(flowData.get("contextData"));
        String taskId = ObjectUtils.cast(businessData.get("taskId"));
        String taskInstanceId = ObjectUtils.cast(businessData.get("taskInstanceId"));
        String flowTransId = ObjectUtils.cast(contextData.get("flowTransId"));
        Map<String, Object> params = ObjectUtils.cast(businessData.get("params"));
        String startType = ObjectUtils.cast(
                Optional.ofNullable(params.get("startType")).orElse(TaskStartType.NORMAL.getCode()));
        if (startType.equals(TaskStartType.RETRY.getCode())) {
            log.info("This is a retry data, task={}:{}, transId={}, flowDataCount={}.", taskId, taskInstanceId,
                    flowTransId, flowDataList.size());
            return flowDataList;
        }

        // scanStatus为running/end. running表示会进行分批扫盘，接下来还会有文件扫描出；end表示不会再有新文件扫出
        Map<String, Object> passDataMap = ObjectUtils.cast(flowData.get("passData"));
        Map<String, Object> metaMap = ObjectUtils.cast(passDataMap.get("meta"));
        String scanStatus = ObjectUtils.cast(
                Optional.ofNullable(metaMap.get("scanStatus")).orElse(ScanStatus.END.getCode()));
        log.info("Start to initialize task scan status, task={}:{}, transId={}, scanStatus={}.", taskId, taskInstanceId,
                flowTransId, scanStatus);

        orchestratorService.updateTaskInfoWithLock(taskId, taskInstanceId, info -> {
            String taskFlowTransId = info.get("flow_context_id");
            if (!Objects.equals(flowTransId, taskFlowTransId)) {
                log.warn("Flow trans id mismatch, update failed, task={}:{}, transId={}, taskTransId={}, .", taskId,
                        taskInstanceId, flowTransId, taskFlowTransId);
                return null;
            }
            Map<String, Object> updateFields = new HashMap<>();
            setProgressProperties(info, flowDataList.size(), updateFields);
            Map<String, Object> extensions = Optional.ofNullable(info.get("extensions"))
                    .map(jsonString -> ObjectUtils.<Map<String, Object>>cast(JSONObject.parseObject(jsonString)))
                    .orElse(new HashMap<>());
            // 批量时append结束可能会先于初始化进来，所以此时需要保留end状态
            if (!ScanStatus.END.getCode().equals(extensions.get("scanStatus"))) {
                extensions.put("scanStatus", scanStatus);
            }
            // 用于标识首次扫盘是否已经走下去，用于处理批量end先结束的情况
            extensions.put("firstScan", "true");
            updateFields.put("extensions", JSONObject.toJSON(extensions).toString());
            return updateFields;
        });

        log.info("Initialize task scan status successfully, task={}:{}, transId={}.", taskId, taskInstanceId,
                flowTransId);
        return flowDataList;
    }

    private static void setProgressProperties(Map<String, String> info, int fileCount,
            Map<String, Object> updateFields) {
        // 首次执行时，更新task中文件总数和设置批量状态，用作计算百分比使用
        int totalFileNum = Integer.parseInt(info.get("file_num")) + fileCount;
        int processedNum = Integer.parseInt(info.get("processed_num"));
        double curPercentage = (double) (processedNum * 100) / (double) totalFileNum;
        if (processedNum > totalFileNum) {
            log.warn("The progress num is larger than total num, processedNum={}, totalFileNum={}.", processedNum,
                    totalFileNum);
            curPercentage = 100.00;
        }
        // 目前保留防止更新数据库状态导致的计算百分比抖动问题，针对分批投递数据场景影响不大
        if (curPercentage > Double.parseDouble(info.get("progress_percent"))) {
            updateFields.put("progress_percent", String.format(Locale.ROOT, "%.2f", curPercentage));
        }

        // file_num为该次任务扫描的文件总数
        updateFields.put("file_num", String.valueOf(totalFileNum));
    }
}
