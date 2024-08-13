/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.runtime.entity;

import java.util.List;

/**
 * 节点信息.
 *
 * @author 张越
 * @since 2024-05-23
 */
public class NodeInfo {
    // 参数，正常情况下只会有一个参数(一个input和一个output).
    // 某些特殊情况会存在多个parameter.
    private List<Parameter> parameters;

    // 节点id.
    private String nodeId;

    // 节点类型.
    private String nodeType;

    // 开始时间戳.
    private long startTime;

    // 节点运行耗费的时间.
    private long runCost;

    private String status;

    private String errorMsg;

    /**
     * 获取参数列表.
     * 正常情况下只会有一个参数(一个input和一个output).
     * 某些特殊情况会存在多个parameter.
     *
     * @return {@link List}{@code <}{@link Parameter}{@code >} 参数列表.
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * 设置参数列表.
     *
     * @param parameters 参数列表.
     */
    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * 获取节点id.
     *
     * @return 节点id.
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * 设置节点id.
     *
     * @param nodeId 节点id.
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * 获取节点类型.
     *
     * @return 节点类型.
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * 设置节点类型.
     *
     * @param nodeType 节点类型.
     */
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * 获取开始时间.
     *
     * @return 开始时间.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * 设置开始时间.
     *
     * @param startTime 开始时间.
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取节点运行所耗费的时间.
     *
     * @return 耗时.
     */
    public long getRunCost() {
        return runCost;
    }

    /**
     * 设置运行耗时.
     *
     * @param runCost 耗费的时间.
     */
    public void setRunCost(long runCost) {
        this.runCost = runCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
