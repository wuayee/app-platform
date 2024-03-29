/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.plugin;

import java.net.URL;

/**
 * 未应用程序提供根插件定义。
 *
 * @author 季聿阶 j00559309
 * @since 2023-08-07
 */
public interface RootPlugin extends Plugin {
    /**
     * 加载一个指定地址的插件。
     *
     * @param plugin 表示待加载插件的 {@link URL}。
     * @return 表示加载后的插件的 {@link Plugin}。
     */
    Plugin loadPlugin(URL plugin);

    /**
     * 卸载一个指定地址的插件。
     *
     * @param plugin 表示待卸载插件的 {@link URL}。
     * @return 表示卸载后的插件的 {@link Plugin}。
     */
    Plugin unloadPlugin(URL plugin);
}
