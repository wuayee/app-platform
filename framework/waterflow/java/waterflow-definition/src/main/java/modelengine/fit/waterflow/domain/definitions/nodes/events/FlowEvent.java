/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.definitions.nodes.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 流程定义节点事件关键类
 * 流程实例流转时需要构建该对象
 *
 * @author 高诗意
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
}
