/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.tool.annotation;

/**
 * 用于工具方法额外参数的定义。
 *
 * @author 易文渊
 * @since 2024-08-19
 */
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
