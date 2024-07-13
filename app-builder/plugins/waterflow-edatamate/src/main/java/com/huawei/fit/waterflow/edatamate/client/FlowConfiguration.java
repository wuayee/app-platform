/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */


package com.huawei.fit.waterflow.edatamate.client;

import java.util.List;

/**
 * 流程配置信息
 *
 * @author y00679285
 * @since 2023/10/24
 */
public class FlowConfiguration {
    private List<String> tags;
    private String operator;
    private String definitionData;
    private String previous;

    public FlowConfiguration() {
    }

    public void setTags(final List<String> tags) {
        this.tags = tags;
    }

    public void setOperator(final String operator) {
        this.operator = operator;
    }

    public void setDefinitionData(final String definitionData) {
        this.definitionData = definitionData;
    }

    public void setPrevious(final String previous) {
        this.previous = previous;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public String getOperator() {
        return this.operator;
    }

    public String getDefinitionData() {
        return this.definitionData;
    }

    public String getPrevious() {
        return this.previous;
    }
}
