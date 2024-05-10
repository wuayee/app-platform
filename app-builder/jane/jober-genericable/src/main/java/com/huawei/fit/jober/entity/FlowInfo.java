/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.jober.entity;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 流程信息
 *
 * @author x00576283
 * @since 2023/12/13
 */
public class FlowInfo {
    /**
     * flow的id
     */
    private String flowId;

    /**
     * flow的版本
     */
    private String version;

    /**
     * flowEngine的id
     * 对应一条可运行的流程配置id，通过flow的id和version也可以找到
     * 只有发布后才有，否则为null
     */
    private String flowDefinitionId;

    /**
     * 流程的页面配置json信息
     * 在查询时返回
     */
    private String configData;

    /**
     * 流程上的节点信息
     * 流程发布后，将返回流程上的节点信息
     */
    private List<FlowNodeInfo> flowNodes;

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFlowDefinitionId() {
        return flowDefinitionId;
    }

    public void setFlowDefinitionId(String flowDefinitionId) {
        this.flowDefinitionId = flowDefinitionId;
    }

    public String getConfigData() {
        return configData;
    }

    public void setConfigData(String configData) {
        this.configData = configData;
    }

    public List<FlowNodeInfo> getFlowNodes() {
        return flowNodes;
    }

    public void setFlowNodes(List<FlowNodeInfo> flowNodes) {
        this.flowNodes = flowNodes;
    }

    /**
     * 根据参数名或者开始节点的参数配置列表
     *
     * @param inputParamName 参数名
     * @return 开始节点的参数配置列表
     */
    public List<Map<String, Object>> getInputParamsByName(String inputParamName) {
        FlowNodeInfo startNodeInfo = this.getFlowNodes()
                .stream()
                .filter(node -> StringUtils.endsWithIgnoreCase(node.getType(), "start"))
                .findFirst()
                .orElseThrow(() -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "no start node"));
        Map<String, Object> properties = startNodeInfo.getProperties();
        return ObjectUtils.<List<Object>>cast(properties.get("inputParams"))
                .stream()
                .map(ObjectUtils::<Map<String, Object>>cast)
                .filter(inputParamMap -> Objects.equals(inputParamMap.get("name").toString(), inputParamName))
                .map(inputParamMap -> ObjectUtils.<List<Map<String, Object>>>cast(inputParamMap.get("value")))
                .findFirst()
                .orElseThrow(() -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "no start node"));
    }
}
