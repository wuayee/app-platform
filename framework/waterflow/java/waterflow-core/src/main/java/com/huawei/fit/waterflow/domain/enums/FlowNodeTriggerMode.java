/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.enums;

import lombok.Getter;

/**
 * 流程节点触发类型
 *
 * @author 杨祥宇
 * @since 1.0
 */
@Getter
public enum FlowNodeTriggerMode {
    AUTO(true),
    MANUAL(false);

    private final boolean auto;

    FlowNodeTriggerMode(boolean auto) {
        this.auto = auto;
    }
}
