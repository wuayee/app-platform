/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.enums;

import lombok.Getter;

/**
 * 流程节点触发类型
 *
 * @author 杨祥宇
 * @since 2023/8/16
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
