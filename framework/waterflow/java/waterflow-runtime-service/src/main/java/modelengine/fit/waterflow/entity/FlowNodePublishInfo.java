/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.entity;

import java.util.Map;

/**
 * 流程节点信息推送对象。
 *
 * @author 陈镕希
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
     * 表示当前数据将要走到的连线id {@link String}
     *
     */
    private String nextPositionId;

    /**
     * 表示节点类型的 {@link String}。
     */
    private String nodeType;

    /**
     * 节点属性
     */
    private Map<String, Object> nodeProperties;

    /**
     * 表示操作上下文的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    private Map<String, Object> businessData;

    /**
     * 表示操作上下文的 {@link FlowPublishContext}。
     */
    private FlowPublishContext flowContext;

    private FlowErrorInfo errorMsg;

    /**
     * 流程节点信息推送对象的无参构造方法。
     */
    public FlowNodePublishInfo() {}

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

    public FlowErrorInfo getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(FlowErrorInfo errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getNextPositionId() {
        return nextPositionId;
    }

    public void setNextPositionId(String nextPositionId) {
        this.nextPositionId = nextPositionId;
    }

    /**
     * 获取节点属性
     *
     * @return 节点属性
     */
    public Map<String, Object> getNodeProperties() {
        return nodeProperties;
    }

    /**
     * 设置节点属性
     *
     * @param nodeProperties 节点属性
     */
    public void setNodeProperties(Map<String, Object> nodeProperties) {
        this.nodeProperties = nodeProperties;
    }
}
