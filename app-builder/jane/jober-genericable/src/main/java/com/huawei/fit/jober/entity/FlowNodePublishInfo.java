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

    private String errorMsg;

    /**
     * 流程节点信息推送对象的无参构造方法。
     */
    public FlowNodePublishInfo() {

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

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
