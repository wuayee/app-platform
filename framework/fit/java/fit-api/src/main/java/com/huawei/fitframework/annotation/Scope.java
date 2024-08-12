/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.annotation;

/**
 * 表示组件生效范围的枚举类。
 *
 * @author 邬涨财
 * @since 2023-07-17
 */
public enum Scope {
    /**
     * 表示组件仅在当前插件范围内生效。
     */
    PLUGIN,

    /**
     * 表示组件在全局范围生效。
     */
    GLOBAL
}
