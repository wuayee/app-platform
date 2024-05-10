/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.entity;

/**
 * 流程实例返回结构体
 *
 * @author y00679285
 * @since 2023/12/11
 */
public class FlowInstanceResult {
    /**
     * 流程实例id标识
     */
    private String traceId;

    public FlowInstanceResult() {
        this(null);
    }

    public FlowInstanceResult(String traceId) {
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }
}
