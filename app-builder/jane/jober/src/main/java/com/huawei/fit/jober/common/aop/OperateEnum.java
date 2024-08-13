/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.aop;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对实体的操作类型
 *
 * @author 姚江
 * @since 2023-11-16 18:53
 */
@Getter
@AllArgsConstructor
public enum OperateEnum {
    /**
     * 创建
     */
    CREATED("创建"),

    /**
     * 更新
     */
    UPDATED("更新"),

    /**
     * 删除
     */
    DELETED("删除"),

    /**
     * 创建任务关联
     */
    RELADD("创建关联"),

    /**
     * 删除任务关联
     */
    RELDEL("删除关联");

    private final String description;
}
