/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.conf;

/**
 * 为 {@link ConfigLoaderChain} 提供监听程序，以观察状态变化。
 *
 * @author 梁济时 l00815032
 * @since 2022-12-16
 */
public interface ConfigLoaderChainListener {
    /**
     * 当配置记载程序被添加到链中时被调用的方法。
     *
     * @param chain 表示添加了加载程序的链的 {@link ConfigLoaderChain}。
     * @param loader 表示被添加的配置加载程序的 {@link ConfigLoader}。
     */
    default void onConfigLoaderAdded(ConfigLoaderChain chain, ConfigLoader loader) {}

    /**
     * 当配置记载程序从链中被移除时被调用的方法。
     *
     * @param chain 表示移除了加载程序的链的 {@link ConfigLoaderChain}。
     * @param loader 表示从链中被移除的配置加载程序的 {@link ConfigLoader}。
     */
    default void onConfigLoaderRemoved(ConfigLoaderChain chain, ConfigLoader loader) {}
}
