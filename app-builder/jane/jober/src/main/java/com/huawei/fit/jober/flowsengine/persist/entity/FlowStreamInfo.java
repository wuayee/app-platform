/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.persist.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * streamId对应结构
 *
 * @author y00679285
 * @since 2024/4/15
 */
@Getter
@Setter
public class FlowStreamInfo {
    /**
     * 流程对应metaId
     */
    private final String metaId;

    /**
     * 流程对应版本
     */
    private final String version;

    public FlowStreamInfo(String metaId, String version) {
        this.metaId = metaId;
        this.version = version;
    }
}
