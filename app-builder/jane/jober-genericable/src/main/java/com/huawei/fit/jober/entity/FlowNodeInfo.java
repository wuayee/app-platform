/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.jober.entity;

import java.util.Map;

/**
 * 节点信息
 *
 * @author x00576283
 * @since 2023/12/14
 */
public class FlowNodeInfo {
    /**
     * 节点id
     */
    private String id;

    /**
     * 节点任务名称
     */
    private String name;

    /**
     * 节点类型
     * 查看 {@link com.huawei.fit.jober.entity.consts.NodeTypes}
     */
    private String type;

    /**
     * 节点上的表单信息
     * 不存在表单时为null
     */
    private FlowNodeFormInfo flowNodeForm;

    /**
     * 节点属性信息
     */
    private Map<String, Object> properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FlowNodeFormInfo getFlowNodeForm() {
        return flowNodeForm;
    }

    public void setFlowNodeForm(FlowNodeFormInfo flowNodeForm) {
        this.flowNodeForm = flowNodeForm;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
