/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.enums;

/**
 * 流程实例Jober已知属性合集
 *
 * @author 李哲峰
 * @since 2024/01/09
 */
public enum FlowJoberProperties {
    ENTITY("entity"),
    ECHO_PREFIX("echoPrefix"),
    SKIP_VARIABLES("skipVariables");

    private String value;

    FlowJoberProperties(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
