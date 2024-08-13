/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker;

import java.util.Set;

/**
 * 表示别名的集合。
 *
 * @author 季聿阶
 * @since 2023-03-27
 */
public interface Aliases {
    /**
     * 获取所有别名的集合。
     *
     * @return 表示所有别名结合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> all();

    /**
     * 判断是否包含指定别名。
     *
     * @param alias 表示指定别名的 {@link String}。
     * @return 如果包含指定别名，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean contains(String alias);
}
