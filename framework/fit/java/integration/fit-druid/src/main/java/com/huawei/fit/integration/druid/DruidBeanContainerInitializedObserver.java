/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.integration.druid;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Order;
import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.lifecycle.container.BeanContainerInitializedObserver;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

/**
 * 提供用以整合 Druid 数据源的 {@link BeanContainerInitializedObserver} 的实现。
 *
 * @author 梁济时 l00815032
 * @since 2022-08-02
 */
@Component
@Order(Order.NEARLY_HIGH)
public class DruidBeanContainerInitializedObserver implements BeanContainerInitializedObserver {
    private static final Logger LOG = Logger.get(DruidBeanContainerInitializedObserver.class);

    private static final String CONFIG_PREFIX = "druid.";

    private final BeanContainer container;

    public DruidBeanContainerInitializedObserver(BeanContainer container) {
        this.container = container;
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

    @Override
    public void onBeanContainerInitialized(BeanContainer container) {
        if (this.container != container) {
            return;
        }
        Config config = container.beans().get(Config.class);
        Properties properties = properties(config);
        if (properties.isEmpty()) {
            LOG.debug("The druid is not configured. Skip to create data source.");
            return;
        }
        container.beans()
                .list(DruidPropertiesHandler.class)
                .forEach((name, bean) -> bean.handleDruidProperties(properties));
        DataSource dataSource;
        try {
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception ex) {
            throw new IllegalStateException(StringUtils.format(
                    "Failed to create druid data source from configurations. [error={0}]",
                    ex.getMessage()), ex);
        }
        container.registry().register(dataSource);
    }
}
