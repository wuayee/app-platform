/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 流程重试日志核心类型
 * 主要负责记录和跟踪流程实例执行过程中产生的异常重试日志
 *
 * @author 李哲峰
 * @since 2024/02/07
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class FlowRetry {
    /**
     * 上下文实体Id
     */
    private String entityId;

    /**
     * 上下文实体类型
     */
    private String entityType;

    /**
     * 下次重试时间
     */
    private LocalDateTime nextRetryTime;

    /**
     * 上次重试时间
     */
    private LocalDateTime lastRetryTime;

    /**
     * 重试次数
     */
    private int retryCount;

    /**
     * 重试版本号
     */
    private int version;
}
