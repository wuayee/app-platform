/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.enums;

import static java.util.Locale.ROOT;
import static modelengine.fit.jade.waterflow.ErrorCodes.ENUM_CONVERT_FAILED;

import lombok.Getter;
import modelengine.fit.jade.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.domain.parsers.nodes.tasks.TaskParser;

import java.util.Arrays;

/**
 * 流程定义手动任务类型
 *
 * @author 杨祥宇
 * @since 1.0
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
                .orElseThrow(() -> new WaterflowParamException(ENUM_CONVERT_FAILED, "FlowTaskType", code));
    }
}
