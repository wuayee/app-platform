/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.datasource.support;

import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.datasource.AccessMode;
import com.huawei.fitframework.datasource.FitDataSource;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

/**
 * 表示 {@link FitDataSource} 的配置。
 *
 * @author 易文渊
 * @since 2024-07-27
 */
public class FitDataSourceConfig {
    private static final String CONFIG_PREFIX = "fit.datasource.";
    private static final String DEFAULT_DATASOURCE_NAME = "master";

    private String name;
    private AccessMode mode;

    /**
     * 根据插件配置创建 {@link FitDataSourceConfig}。
     *
     * @param config 表示插件配置的 {@link Config}。
     * @return 表示数据源配置的 {@link FitDataSourceConfig}。
     */
    public static FitDataSourceConfig create(Config config) {
        FitDataSourceConfig fitDataSourceConfig =
                ObjectUtils.getIfNull(config.get(CONFIG_PREFIX, FitDataSourceConfig.class), FitDataSourceConfig::new);
        if (StringUtils.isBlank(fitDataSourceConfig.getName())) {
            fitDataSourceConfig.setName(DEFAULT_DATASOURCE_NAME);
        }
        if (fitDataSourceConfig.getMode() == null) {
            fitDataSourceConfig.setMode(AccessMode.EXCLUSIVE);
        }
        return fitDataSourceConfig;
    }

    /**
     * 获取数据源名字。
     *
     * @return 表示数据源名字的 {@link String}。
     */
    public String getName() {
        return this.name;
    }

    /**
     * 设置数据源名字。
     *
     * @param name 表示数据源名字的 {@link String}。
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取数据源访问模式。
     *
     * @return 表示数据源访问模式的 {@link AccessMode}。
     */
    public AccessMode getMode() {
        return this.mode;
    }

    /**
     * 设置数据源访问模式。
     *
     * @param mode 表示数据源访问模式的 {@link AccessMode}。
     */
    public void setMode(AccessMode mode) {
        this.mode = mode;
    }
}