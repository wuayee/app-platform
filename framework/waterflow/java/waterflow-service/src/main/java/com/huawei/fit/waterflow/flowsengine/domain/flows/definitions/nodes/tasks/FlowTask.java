/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.tasks;

import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowTaskType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

/**
 * 流程定义节点手动任务类
 *
 * @author y00679285
 * @since 2023/9/20
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlowTask {
    /**
     * 手动操作任务ID标识
     */
    private String taskId;

    /**
     * 手动操作任务类型
     */
    private FlowTaskType taskType;

    /**
     * 节点任务异常处理fitables集合
     */
    private Set<String> exceptionFitables;

    /**
     * 手动操作任务自定义属性
     * key为属性的键值，value为属性具体的值
     */
    private Map<String, String> properties;
}
