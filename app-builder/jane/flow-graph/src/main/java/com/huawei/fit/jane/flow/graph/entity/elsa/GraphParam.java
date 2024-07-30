/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jane.flow.graph.entity.elsa;

/**
 * 图的实体
 *
 * @author l00498867
 * @since 2024/7/27
 */
public class GraphParam {
    private String graphId;

    private String version;

    private String json;

    public void setGraphId(String graphId) {
        this.graphId = graphId;
    }

    public String getGraphId() {
        return graphId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }
}
