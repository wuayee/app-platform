/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.context;

import lombok.Getter;

/**
 * 每次offer后返回的标识
 *
 * @author 宋永坦
 * @since 2024/2/18
 */
@Getter
public class FlowOfferId {
    private FlowTrans trans;

    private String traceId;

    public FlowOfferId(FlowTrans trans, String traceId) {
        this.trans = trans;
        this.traceId = traceId;
    }
}
