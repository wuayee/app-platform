/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.definitions.nodes.events;

import com.huawei.fit.waterflow.domain.context.FlowData;
import com.huawei.fit.waterflow.domain.utils.FlowUtil;

import com.googlecode.aviator.AviatorEvaluator;

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
 * @since 1.0
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
     * 通过输入参数解析condition rule
     * condition rule解析完后可以通过{@link AviatorEvaluator}执行
     *
     * @param flowData {@link FlowData} 流程执行过程中的用户数据
     * @return 解析完的condition rule
     */
    public String getExecutableRule(FlowData flowData) {
        return Optional.ofNullable(FlowUtil.replace(this.conditionRule, flowData.getBusinessData()))
                .map(Object::toString)
                .orElse("");
    }
}
