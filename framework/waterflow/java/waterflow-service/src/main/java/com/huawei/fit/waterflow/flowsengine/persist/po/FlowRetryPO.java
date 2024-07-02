/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.persist.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * flow retry持久化类
 *
 * @author l00862071
 * @since 2024/1/31
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FlowRetryPO {
    private String entityId;

    private String entityType;

    private LocalDateTime nextRetryTime;

    private LocalDateTime lastRetryTime;

    private int retryCount;

    private int version;
}
