/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.resource;

/**
 * 表示类路径中的资源。
 *
 * @author 梁济时 l00815032
 * @since 2023-01-17
 */
public interface ClassPathResource extends Resource {
    /**
     * 获取资源所在的类加载程序。
     *
     * @return 表示类加载程序的 {@link ClassLoader}。
     */
    ClassLoader loader();

    /**
     * 获取资源的键。
     *
     * @return 表示资源的键的 {@link String}。
     */
    String key();
}
