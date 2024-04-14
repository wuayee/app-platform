/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.starter.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 用于在 IDE 中提示用户关于动态插件的配置项。
 *
 * @author 季聿阶
 * @since 2024-04-14
 */
@ConfigurationProperties(prefix = "plugin.fit.dynamic.plugin")
public class PluginProperties {
    private String directory;

    /**
     * 获取动态插件的自动扫描目录。
     *
     * @return 表示动态插件的自动扫描目录的 {@link String}。
     */
    public String getDirectory() {
        return this.directory;
    }

    /**
     * 设置动态插件的自动扫描目录。
     *
     * @param directory 表示动态插件的自动扫描目录的 {@link String}。
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
