/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.enums;

/**
 * 流程实例Jober已知属性合集
 *
 * @author l00862071
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
