/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.jober.entity;

/**
 * 节点上的表单信息
 *
 * @author x00576283
 * @since 2023/12/14
 */
public class FlowNodeFormInfo {
    /**
     * 表单id
     */
    private String formId;

    /**
     * 表单版本
     */
    private String version;

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
