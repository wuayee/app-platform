/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.integration.druid;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.datasource.support.AbstractFitDataSource;
import com.huawei.fitframework.ioc.BeanContainer;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

/**
 * 框架数据源的实现。
 *
 * @author 易文渊
 * @author 李金绪
 * @since 2024-07-27
 */
@Component
public class FitDruidDataSource extends AbstractFitDataSource {
    private static final String CONFIG_PREFIX = "druid.";

    public FitDruidDataSource(BeanContainer beanContainer, Config config) {
        super(beanContainer, config);
    }

    @Override
    protected DataSource buildDataSource(Config config) {
        Properties properties = properties(config);
        if (properties.isEmpty()) {
            throw new IllegalStateException("The druid data source is not configured.");
        }
        try {
            return DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create druid data source from configurations.", e);
        }
    }

    private static Properties properties(Config config) {
        Properties properties = new Properties();
        Set<String> keys =
                config.keys().stream().filter(key -> key.startsWith(CONFIG_PREFIX)).collect(Collectors.toSet());
        for (String key : keys) {
            String actualKey = key.substring(CONFIG_PREFIX.length());
            actualKey = Config.canonicalizeKey(actualKey);
            String value = config.get(key, String.class);
            properties.setProperty(actualKey, value);
        }
        return properties;
    }
}