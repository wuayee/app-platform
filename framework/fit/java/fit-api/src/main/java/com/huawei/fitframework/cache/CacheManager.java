/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.cache;

import java.util.Optional;
import java.util.Set;

/**
 * 表示缓存管理器。
 *
 * @author 季聿阶 j00559309
 * @since 2022-12-13
 */
public interface CacheManager {
    /**
     * 获取指定名字的缓存实例。
     *
     * @param name 表示指定名字的 {@link String}。
     * @return 表示指定名字的缓存实例的 {@link Optional}{@code <}{@link Cache}{@code >}。
     */
    Optional<Cache> getInstance(String name);

    /**
     * 获取所有缓存实例的名字集合。
     *
     * @return 表示所有缓存实例的名字集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> getInstanceNames();
}
