/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.util.wildcard;

/**
 * 表示路径的匹配模式。
 *
 * @author 季聿阶 j00559309
 * @since 2022-12-21
 */
public interface PathPattern extends Pattern<String> {
    /**
     * 匹配一个指定的路径。
     *
     * @param path 表示待匹配的指定路径的 {@link String}。
     * @return 如果匹配成功，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean matches(String path);
}
