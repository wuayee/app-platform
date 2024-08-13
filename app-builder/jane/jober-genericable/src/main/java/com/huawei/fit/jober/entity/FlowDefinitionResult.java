/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.entity;

/**
 * 流程定义返回值结构体
 *
 * @author 杨祥宇
 * @since 2023/12/11
 */
public class FlowDefinitionResult {
    /**
     * 流程定义唯一id
     */
    private final String flowDefinitionId;
    private String metaId;
    private String name;
    private String tenantId;
    private String version;
    private String status;
    private String graph;

    public FlowDefinitionResult() {
        this(null);
    }

    public FlowDefinitionResult(String flowDefinitionId) {
        this.flowDefinitionId = flowDefinitionId;
    }

    public String getFlowDefinitionId() {
        return flowDefinitionId;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }
}
