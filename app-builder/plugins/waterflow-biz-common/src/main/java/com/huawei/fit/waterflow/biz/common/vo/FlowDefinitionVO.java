/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.biz.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

/**
 * 流程定义VO类
 *
 * @author 陈镕希
 * @since 2024-02-27
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowDefinitionVO {
    /**
     * 流程定义ID
     */
    private String definitionId;

    /**
     * 流程定义metaID，与前端保持一致
     */
    private String metaId;

    /**
     * 流程定义名称
     */
    private String name;

    /**
     * 流程定义描述信息
     */
    private String description;

    /**
     * 流程定义版本
     */
    private String version;

    /**
     * 租户唯一标识的
     */
    private String tenant;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 流程中间节点，key为节点metaId，value为节点实例
     */
    private Map<String, FlowNodeVO> nodeMap;

    /**
     * 流程定义状态
     */
    private String status;

    /**
     * 流程定义创建时间
     */
    private String releaseTime;

    /**
     * 流程定义的streamId
     */
    private String streamId;

    /**
     * 指向每个节点的event集合
     */
    private Map<String, Set<FlowEventVO>> fromEvents;

    /**
     * 流程定义中的节点ID集合
     */
    private Set<String> nodeIdSet;
}
