/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.enums;

import static com.huawei.fit.jober.common.ErrorCodes.ENUM_CONVERT_FAILED;
import static java.util.Locale.ROOT;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.flowsengine.domain.flows.parsers.nodes.tasks.TaskParser;

import lombok.Getter;

import java.util.Arrays;

/**
 * 流程定义手动任务类型
 *
 * @author y00679285
 * @since 2023/9/22
 */
@Getter
public enum FlowTaskType {
    APPROVING_TASK("APPROVING_TASK", "TASK_CENTER", new TaskParser()),
    TASK_CENTER("TASK_CENTER", "TASK_CENTER", new TaskParser()),
    AIPP_SMART_FORM("AIPP_SMART_FORM", "SMART_FORM", new TaskParser());

    private final String code;

    private final String source;

    private final TaskParser taskParser;

    FlowTaskType(String code, String source, TaskParser taskParser) {
        this.code = code;
        this.source = source;
        this.taskParser = taskParser;
    }

    /**
     * getTaskType
     *
     * @param code code
     * @return FlowTaskType
     */
    public static FlowTaskType getTaskType(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code.toUpperCase(ROOT)))
                .findFirst()
                .orElseThrow(() -> new JobberParamException(ENUM_CONVERT_FAILED, "FlowTaskType", code));
    }
}
