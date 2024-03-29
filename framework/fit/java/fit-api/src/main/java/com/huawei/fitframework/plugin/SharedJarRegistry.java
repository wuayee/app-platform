/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.plugin;

import java.net.URL;

/**
 * 为公共JAR提供注册入口。
 *
 * @author 梁济时 l00815032
 * @since 2022-06-20
 */
public interface SharedJarRegistry {
    /**
     * 注册公共JAR。
     *
     * @param jar 表示公共JAR的URL的 {@link URL}。
     */
    void register(URL jar);
}
