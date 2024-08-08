/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool.annotation;

public @interface Attribute {
    /**
     * 获取属性的键。
     *
     * @return 表示属性键的 {@link String}。
     */
    String key();

    /**
     * 获取属性的值。
     *
     * @return 表示属性值的 {@link String}。
     */
    String value();
}
