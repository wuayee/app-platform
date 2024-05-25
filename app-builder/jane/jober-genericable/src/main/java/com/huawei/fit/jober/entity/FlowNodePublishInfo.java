/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.entity;

import java.util.Map;

/**
 * 流程节点信息推送对象。
 *
 * @author 陈镕希 c00572808
 * @since 2024-05-23
 */
public class FlowNodePublishInfo {
    /**
     * 表示流程定义id的 {@link String}。
     */
    private String flowDefinitionId;

    /**
     * 表示节点id的 {@link String}。
     */
    private String nodeId;

    /**
     * 表示节点类型的 {@link String}。
     */
    private String nodeType;

    /**
     * 表示操作上下文的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    private Map<String, Object> businessData;

    /**
     * 表示操作上下文的 {@link FlowPublishContext}。
     */
    private FlowPublishContext flowContext;

    /**
     * 流程节点信息推送对象的无参构造方法。
     */
    public FlowNodePublishInfo() {
    }

    /**
     * 流程节点信息推送对象的全参构造方法。
     *
     * @param flowDefinitionId 流程定义唯一标识的 {@link String}。
     * @param nodeId 流程节点唯一标识的 {@link String}。
     * @param nodeType 流程节点类型的 {@link String}。
     * @param businessData 流程执行所需的业务参数的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @param flowContext 流程节点上下文信息对象的 {@link FlowPublishContext}。
     */
    public FlowNodePublishInfo(String flowDefinitionId, String nodeId, String nodeType,
            Map<String, Object> businessData, FlowPublishContext flowContext) {
        this.flowDefinitionId = flowDefinitionId;
        this.nodeId = nodeId;
        this.nodeType = nodeType;
        this.businessData = businessData;
        this.flowContext = flowContext;
    }

    public String getFlowDefinitionId() {
        return flowDefinitionId;
    }

    public void setFlowDefinitionId(String flowDefinitionId) {
        this.flowDefinitionId = flowDefinitionId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Map<String, Object> getBusinessData() {
        return businessData;
    }

    public void setBusinessData(Map<String, Object> businessData) {
        this.businessData = businessData;
    }

    public FlowPublishContext getFlowContext() {
        return flowContext;
    }

    public void setFlowContext(FlowPublishContext flowContext) {
        this.flowContext = flowContext;
    }
}
