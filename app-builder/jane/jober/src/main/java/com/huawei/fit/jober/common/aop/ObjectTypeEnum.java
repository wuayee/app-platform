/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.aop;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 实体类型
 *
 * @author yWX1299574
 * @since 2023-11-16 18:54
 */
@Getter
@AllArgsConstructor
public enum ObjectTypeEnum {
    /**
     * 实例
     */
    INSTANCE("任务实例"),

    /**
     * 任务定义
     */
    TASK("任务定义"),

    /**
     * 任务类型
     */
    TASK_TYPE("任务类型"),

    /**
     * 来源
     */
    SOURCE("任务来源"),
    ;

    private final String objectTypeName;
}
