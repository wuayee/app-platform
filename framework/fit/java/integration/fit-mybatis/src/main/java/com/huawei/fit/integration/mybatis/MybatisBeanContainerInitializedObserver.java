/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.integration.mybatis;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.integration.mybatis.util.SqlSessionFactoryHelper;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Order;
import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.ioc.lifecycle.container.BeanContainerInitializedObserver;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginKey;
import com.huawei.fitframework.transaction.TransactionManager;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.util.Optional;
import java.util.Properties;

/**
 * 为 {@link BeanContainerInitializedObserver} 提供用以整合 MyBatis 的实现。
 *
 * @author 梁济时
 * @since 2022-08-02
 */
@Component
@Order(Order.NEARLY_HIGH)
public class MybatisBeanContainerInitializedObserver implements BeanContainerInitializedObserver {
    private static final String BYTEBUDDY_CONFIG = "mybatis.use-bytebuddy";

    private final BeanContainer container;

    MybatisBeanContainerInitializedObserver(BeanContainer container) {
        this.container = notNull(container, "The bean container cannot be null.");
    }

    @Override
    public void onBeanContainerInitialized(BeanContainer container) {
        if (this.container != container) {
            return;
        }
        Config config = container.beans().get(Config.class);
        Properties properties = SqlSessionFactoryHelper.properties(config);
        if (properties.isEmpty()) {
            return;
        }
        Plugin plugin = container.beans().get(Plugin.class);
        Configuration configuration = container.factory(Configuration.class)
                .map(BeanFactory::<Configuration>get)
                .orElseGet(Configuration::new);
        TransactionManager transactionManager = this.container.beans().get(TransactionManager.class);
        configuration.setEnvironment(new Environment(PluginKey.identify(plugin.metadata()),
                new ManagedTransactionFactory(transactionManager),
                new LazyLoadedDataSource(container)));
        container.factories(Interceptor.class)
                .stream()
                .map(BeanFactory::<Interceptor>get)
                .forEach(configuration::addInterceptor);
        SqlSessionFactoryHelper.loadMappers(properties, plugin, configuration);
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        container.registry().register(sessionFactory);
        boolean shouldUseByteBuddy = Optional.ofNullable(config.get(BYTEBUDDY_CONFIG, Boolean.class)).orElse(false);
        configuration.getMapperRegistry().getMappers().forEach(mapperClass -> {
            Object mapper = MapperInvocationHandler.proxy(sessionFactory, mapperClass, shouldUseByteBuddy);
            container.registry().register(mapper, mapperClass);
        });
    }
}