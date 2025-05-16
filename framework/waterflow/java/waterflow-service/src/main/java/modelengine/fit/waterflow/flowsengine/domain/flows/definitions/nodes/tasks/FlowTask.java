/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.tasks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.FlowDataConverter;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowTaskType;

import java.util.Map;
import java.util.Set;

/**
 * 流程定义节点手动任务类
 *
 * @author 杨祥宇
 * @since 2023/9/20
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlowTask {
    /**
     * 任务数据转换器
     */
    protected FlowDataConverter converter;

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

    /**
     * 所在节点位置
     */
    private String nodeId;
}
