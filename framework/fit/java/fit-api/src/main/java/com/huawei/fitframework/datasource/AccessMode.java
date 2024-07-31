/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.datasource;

/**
 * 表示数据源访问模式的枚举。
 *
 * @author 易文渊
 * @since 2024-07-27
 */
public enum AccessMode {
    /**
     * 独占数据源。
     */
    EXCLUSIVE,

    /**
     * 共享数据源。
     */
    SHARED
}
