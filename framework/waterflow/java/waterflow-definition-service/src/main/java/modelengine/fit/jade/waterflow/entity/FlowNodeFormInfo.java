/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.waterflow.entity;

/**
 * 节点上的表单信息
 *
 * @author 夏斐
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
