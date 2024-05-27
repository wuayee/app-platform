/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

/**
 * 流程定义节点事件关键类
 * 流程实例流转时需要构建该对象
 *
 * @author g00564732
 * @since 2023/08/14
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowEvent {
    /**
     * 节点事件metaId，与前端保持一致
     */
    private String metaId;

    /**
     * 节点事件名称
     */
    private String name;

    /**
     * 来源节点metaId，与前端保持一致
     */
    private String from;

    /**
     * 下一节点metaId，与前端保持一致
     */
    private String to;

    /**
     * 节点事件中条件属性
     */
    private String conditionRule;

    /**
     * 节点事件执行的优先级，数字越小越早执行
     */
    private Integer priority;

    /**
     * 获取优先级，对于未设置优先级的节点，默认优先级为-1
     *
     * @return 优先级对应的数字的 {@link Integer}。
     */
    public Integer getPriority() {
        return Optional.ofNullable(priority).orElse(-1);
    }
}
