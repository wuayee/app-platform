/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.InstanceService;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.common.exception.AippJsonDecodeException;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.entity.W3Task;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.entity.InstanceInfo;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * W3TaskPublish
 *
 * @author l00611472
 * @since 2024/1/19
 */
@Component
public class W3TaskPublish implements FlowableService {
    private static final Logger log = Logger.get(W3TaskPublish.class);
    private static final String TASK_DISPLAY_FORMAT = "任务%d: %s(%s)\n";

    private final InstanceService instanceService;
    private final AippLogService aippLogService;
    private final String taskId;
    private final String typeId;
    private final String taskEndpoint;

    public W3TaskPublish(@Fit InstanceService instanceService, @Fit AippLogService aippLogService,
            @Value("${w3-task.taskId}") String taskId, @Value("${w3-task.typeId}") String typeId,
            @Value("${w3-task.taskEndpoint}") String taskEndpoint) {
        this.instanceService = instanceService;
        this.aippLogService = aippLogService;
        this.taskId = taskId;
        this.typeId = typeId;
        this.taskEndpoint = taskEndpoint;
    }

    private Map<String, Object> buildInfoMap(W3Task task) {
        Map<String, Object> info = new HashMap<>();
        info.put("title", task.getTitle());
        info.put("task_detail", task.getTaskDetail());
        info.put("owner", task.getOwner());
        info.put("status", "I"); // 'I'-待处理 'A'-处理中 'C'-已完成

        return info;
    }

    private com.huawei.fit.jober.entity.OperationContext getJoberOpContext(Map<String, Object> businessData) {
        OperationContext opContext = Utils.getOpContext(businessData);

        com.huawei.fit.jober.entity.OperationContext joberOpContext =
                new com.huawei.fit.jober.entity.OperationContext();
        joberOpContext.setLanguage(opContext.getLanguage());
        joberOpContext.setOperator(opContext.getOperator());
        joberOpContext.setOperatorIp(opContext.getOperatorIp());
        joberOpContext.setTenantId(opContext.getTenantId());
        joberOpContext.setSourcePlatform(opContext.getSourcePlatform());

        return joberOpContext;
    }

    private List<W3Task> getW3Tasks(List<Map<String, Object>> flowData, String w3TaskStr) {
        List<W3Task> w3Task;
        try {
            w3Task = JsonUtils.parseArray(w3TaskStr, W3Task[].class);
        } catch (AippJsonDecodeException e) {
            log.error("error={}, invalid json string={}", e.getMessage(), w3TaskStr);
            String msg = "很抱歉！解析音频错误，您可以尝试换个音频";
            Utils.persistAippErrorLog(aippLogService, msg, flowData);
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "w3Task is invalid json string.");
        }
        return w3Task;
    }

    private String buildTaskResult(List<W3Task> w3Task) {
        StringBuilder stringBuilder =
                new StringBuilder("任务已成功下发，责任人将收到w3待办和welink应用号通知。您可以前往天舟首页查看任务的最新状态。\n");
        for (int i = 0; i < w3Task.size(); i++) {
            W3Task task = w3Task.get(i);
            String item = String.format(Locale.ROOT, TASK_DISPLAY_FORMAT, i, task.getTitle(), task.getOwner());
            stringBuilder.append(item);
        }

        return stringBuilder.toString();
    }

    /**
     * 处理流程中的任务调用
     *
     * @param flowData 流程执行上下文数据
     * @return 任务执行返回结果
     */
    @Fitable("com.huawei.fit.jober.aipp.fitable.W3TaskPublish")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = Utils.getBusiness(flowData);
        log.debug("W3TaskPublish businessData {}", businessData);

        String w3TaskStr = (String) businessData.get(AippConst.BS_W3_TASK_RESULT);
        Validation.notNull(w3TaskStr, "w3Task cant be null.");

        com.huawei.fit.jober.entity.OperationContext joberOpContext = getJoberOpContext(businessData);
        List<W3Task> w3Task = getW3Tasks(flowData, w3TaskStr);

        try {
            w3Task.forEach(task -> {
                InstanceInfo info = new InstanceInfo();
                info.setTaskTypeId(typeId);
                info.setInfo(buildInfoMap(task));
                log.info("create w3Task {} info {}", taskId, info);
                instanceService.createTaskInstance(taskId, info, joberOpContext);
            });
        } catch (Exception e) {
            String msg = "很抱歉！创建w3待办失败，请稍后重试";
            Utils.persistAippErrorLog(aippLogService, msg, flowData);
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "create w3Task failed.");
        }
        String displayResult =
                "任务已成功下发，责任人将收到w3待办和welink应用号通知。您可以前往天舟工作台查看任务的最新状态。\n" + "\n"
                        + "天舟工作台：" + taskEndpoint;
        businessData.put(AippConst.BS_W3_TASK_DISPLAY_KEY, displayResult);

        return flowData;
    }
}
