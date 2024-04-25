/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.enums;

/**
 * 流程实例节点状态
 * 状态流转顺序：NEW -> PENDING(停留在EVENT边上) -> READY(进入到节点) -> PROCESSING(开始处理) -> ARCHIVED(处理完成)
 *
 * @author g00564732
 * @since 1.0
 */
public enum FlowNodeStatus {
    NEW,
    PENDING,
    READY, // 未更新数据库
    PROCESSING, // 未更新数据库
    ARCHIVED,
    TERMINATE,
    ERROR
}
