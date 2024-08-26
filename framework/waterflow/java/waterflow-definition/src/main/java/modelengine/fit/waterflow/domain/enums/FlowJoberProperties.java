/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.enums;

/**
 * 流程实例Jober已知属性合集
 *
 * @author 李哲峰
 * @since 1.0
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
