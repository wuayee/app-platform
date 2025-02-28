/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.waterflow.entity;

import java.util.List;

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
    private String createdAt;
    /**
     * 节点信息的fitableId列表
     */
    private List<String> publishNodeFitables;
    /**
     * 流程上的节点信息
     * 流程发布后，将返回流程上的节点信息
     */
    private List<FlowNodeInfo> flowNodes;

    public FlowDefinitionResult() {
        this("");
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

    private FlowDefinitionResult(Builder builder) {
        this.flowDefinitionId = builder.flowDefinitionId;
        this.metaId = builder.metaId;
        this.name = builder.name;
        this.tenantId = builder.tenantId;
        this.version = builder.version;
        this.status = builder.status;
        this.graph = builder.graph;
        this.publishNodeFitables = builder.publishNodeFitables;
        this.flowNodes = builder.flowNodes;
    }

    public List<String> getPublishNodeFitables() {
        return publishNodeFitables;
    }

    public void setPublishNodeFitables(List<String> publishNodeFitables) {
        this.publishNodeFitables = publishNodeFitables;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<FlowNodeInfo> getFlowNodes() {
        return flowNodes;
    }

    public void setFlowNodes(List<FlowNodeInfo> flowNodes) {
        this.flowNodes = flowNodes;
    }

    public static class Builder {
        private final String flowDefinitionId;
        private String metaId;
        private String name;
        private String tenantId;
        private String version;
        private String status;
        private String graph;
        private List<String> publishNodeFitables;
        private String createdAt;
        private List<FlowNodeInfo> flowNodes;

        public Builder(String flowDefinitionId) {
            this.flowDefinitionId = flowDefinitionId;
        }

        public Builder setMetaId(String metaId) {
            this.metaId = metaId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setTenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setGraph(String graph) {
            this.graph = graph;
            return this;
        }

        public Builder setPublishNodeFitables(List<String> publishNodeFitables) {
            this.publishNodeFitables = publishNodeFitables;
            return this;
        }

        public Builder setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setFlowNodes (List<FlowNodeInfo> flowNodes) {
            this.flowNodes = flowNodes;
            return this;
        }

        public FlowDefinitionResult build() {
            return new FlowDefinitionResult(this);
        }
    }
}
