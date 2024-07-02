/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.persist.po;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 流程实例Trace持久化类
 *
 * @author y00679285
 * @since 2023/8/30
 */
@Builder
@Getter
@Setter
public class FlowTracePO {
    private String traceId;

    private String streamId;

    private String operator;

    private String application;

    private String startNode;

    private String contextPool;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;
}
