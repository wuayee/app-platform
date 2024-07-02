/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.enums;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 流程实例节点状态
 * 状态流转顺序：NEW -> PENDING(停留在EVENT边上) -> READY(进入到节点) -> PROCESSING(开始处理) -> ARCHIVED(处理完成)
 *
 * @author g00564732
 * @since 2023/08/14
 */
public enum FlowNodeStatus {
    NEW,
    PENDING,
    READY, // 未更新数据库
    PROCESSING, // 未更新数据库
    ARCHIVED,
    TERMINATE,
    ERROR,
    RETRYABLE;

    private static final Set<FlowNodeStatus> END_STATUS = new HashSet<>(Arrays.asList(ARCHIVED, ERROR, TERMINATE));

    /**
     * 是否是终态
     *
     * @param status 目标状态
     * @return 是否是终态
     */
    public static boolean isEndStatus(FlowNodeStatus status) {
        return END_STATUS.contains(status);
    }
}
