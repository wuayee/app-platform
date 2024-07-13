/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.service;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;

import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fitframework.annotation.Alias;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 任务回填实现
 *
 * @author l00853680
 * @since 2023/11/10
 */
@Component
@Alias("task-feedback")
public class TaskFeedbackImpl implements FlowableService {
    private static final Logger log = Logger.get(TaskFeedbackImpl.class);

    private final OrchestratorService orchestratorService;

    private final Pattern pattern = Pattern.compile("\\d+");

    private final FlowLocks locks;

    public TaskFeedbackImpl(OrchestratorService orchestratorService, FlowLocks locks) {
        this.orchestratorService = orchestratorService;
        this.locks = locks;
    }

    @Override
    @Fitable(id = "6a6084c0d1e243e7b327cb1a0ba068e2")
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        log.info("Start update task instance.");
        Map<String, Object> businessData = ObjectUtils.cast(flowData.get(0).get("businessData"));
        Map<String, Object> passData = ObjectUtils.cast(flowData.get(0).get("passData"));
        Map<String, Object> contextData = ObjectUtils.cast(flowData.get(0).get("contextData"));
        String taskId = ObjectUtils.cast(businessData.get("taskId"));
        String taskInstanceId = ObjectUtils.cast(businessData.get("taskInstanceId"));
        String fileSize = ObjectUtils.cast(ObjectUtils.<Map<String, Object>>cast(passData.get("meta")).get("fileSize"));
        String flowTransId = ObjectUtils.cast(contextData.get("flowTransId"));
        long fileSizeByte = 0L;
        try {
            fileSizeByte = convertToByte(fileSize);
        } catch (JobberParamException e) {
            log.error("Convert to byte failed, fileSize: {}, contextId: {}.", fileSize, contextData.get("contextId"));
        }
        long finalFileSizeByte = fileSizeByte;
        // 需要transId级别的分布式锁控制task中信息更新, 否则并发导致task中数量等信息不一致
        orchestratorService.updateTaskInfoWithLock(taskId, taskInstanceId, info -> {
            if (!Objects.equals(flowTransId, info.get("flow_context_id"))) {
                log.warn("Not same Flow_context_id, update task instance failed.");
                return null;
            }
            int totalFileNum = Integer.parseInt(info.get("file_num"));
            int processedNum = Integer.parseInt(info.get("processed_num")) + 1;

            Map<String, Object> updateFields = new HashMap<>();
            updateFields.put("processed_num", String.valueOf(processedNum));
            updateFields.put("cleaning_data",
                    String.valueOf(Long.parseLong(info.get("cleaning_data")) + finalFileSizeByte));

            Map<String, Object> extensions = Optional.ofNullable(info.get("extensions"))
                    .map(jsonString -> (Map<String, Object>) JSONObject.parseObject(jsonString))
                    .orElse(new HashMap<>());
            // 如果此时首次扫盘节点还没走完，此时先不计算进度，防止后面总数的变化导致进度跳变过大
            if (!"true".equals(extensions.get("firstScan"))) {
                log.warn("The first scan is not finished, task={}:{}, transId={}.", taskId, taskInstanceId,
                        flowTransId);
                return updateFields;
            }

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
            return updateFields;
        });

        log.info("End to update task instance, task={}:{}, transId={}.", taskId, taskInstanceId, flowTransId);
        return flowData;
    }

    private long convertToByte(String fileSize) throws JobberParamException {
        if (StringUtils.isBlank(fileSize)) {
            throw new JobberParamException(INPUT_PARAM_IS_INVALID, "file size");
        }
        long fileSizeLong;
        if (fileSize.endsWith("GB")) {
            fileSizeLong = (long) (Float.parseFloat(fileSize.substring(0, fileSize.length() - 2)) * 1073741824);
        } else if (fileSize.endsWith("MB")) {
            fileSizeLong = (long) (Float.parseFloat(fileSize.substring(0, fileSize.length() - 2)) * 1048576);
        } else if (fileSize.endsWith("KB")) {
            fileSizeLong = (long) (Float.parseFloat(fileSize.substring(0, fileSize.length() - 2)) * 1024);
        } else if (fileSize.endsWith("B")) {
            fileSizeLong = (long) (Float.parseFloat(fileSize.substring(0, fileSize.length() - 1)));
        } else {
            throw new JobberParamException(INPUT_PARAM_IS_INVALID, "file size");
        }
        return fileSizeLong;
    }
}
