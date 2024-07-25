/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.transaction.transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.aop.AopInterceptor;
import com.huawei.fitframework.aop.interceptor.aspect.interceptor.AspectInterceptorResolver;
import com.huawei.fitframework.aop.proxy.bytebuddy.ByteBuddyAopProxyFactory;
import com.huawei.fitframework.aop.proxy.support.JdkDynamicAopProxyFactory;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.annotation.support.DefaultAnnotationMetadataResolver;
import com.huawei.fitframework.ioc.support.DefaultBeanContainer;
import com.huawei.fitframework.ioc.support.DefaultBeanResolver;
import com.huawei.fitframework.ioc.support.DefaultDependencyResolver;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginCollection;
import com.huawei.fitframework.plugin.PluginMetadata;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.transaction.Transaction;
import com.huawei.fitframework.transaction.TransactionIsolationLevel;
import com.huawei.fitframework.transaction.TransactionManager;
import com.huawei.fitframework.transaction.TransactionPropagationPolicy;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.transaction.aspect.TransactionAspect;
import com.huawei.fitframework.transaction.support.DefaultTransactionManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;

import javax.sql.DataSource;

/**
 * 为 {@link Transactional} 提供单元测试。
 *
 * @author 梁济时 l00815032
 * @since 2022-08-27
 */
@DisplayName("测试 Transactional 注解")
class TransactionalTest {
    @Component
    public static class TestService {
        @Transactional(propagation = TransactionPropagationPolicy.REQUIRES_NEW,
                isolation = TransactionIsolationLevel.READ_COMMITTED)
        public void run(Runnable action) {
            action.run();
        }
    }

    @Test
    @DisplayName("执行被 @Transactional 注解修饰的方法，在事务中执行")
    void should_execute_in_transaction() throws SQLException {
        BeanContainer container = container();
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);

        TransactionManager transactionManager = new DefaultTransactionManager(container);
        container.registry().register(container);

        container.registry().register(new AopInterceptor(container));
        container.registry().register(AspectInterceptorResolver.class);
        container.registry().register(new JdkDynamicAopProxyFactory());
        container.registry().register(new ByteBuddyAopProxyFactory());

        container.registry().register(TransactionAspect.class);
        container.registry().register(dataSource);
        container.registry().register(transactionManager);
        container.registry().register(TestService.class);

        TestService service = container.beans().get(TestService.class);
        assertNull(transactionManager.active());
        service.run(() -> {
            Transaction transaction = transactionManager.active();
            assertNotNull(transaction);
            assertEquals(TransactionPropagationPolicy.REQUIRES_NEW, transaction.metadata().propagation());
            assertEquals(TransactionIsolationLevel.READ_COMMITTED, transaction.metadata().isolation());
        });
        assertNull(transactionManager.active());
    }

    protected static BeanContainer container() {
        FitRuntime runtime = mock(FitRuntime.class);
        when(runtime.resolverOfBeans()).thenReturn(new DefaultBeanResolver());
        when(runtime.resolverOfDependencies()).thenReturn(new DefaultDependencyResolver());
        when(runtime.resolverOfAnnotations()).thenReturn(new DefaultAnnotationMetadataResolver());
        Plugin plugin = mock(Plugin.class);
        PluginMetadata metadata = mock(PluginMetadata.class);
        when(metadata.group()).thenReturn("com.huawei.fitframework");
        when(metadata.name()).thenReturn("test");
        when(metadata.version()).thenReturn("1.0");
        when(plugin.metadata()).thenReturn(metadata);
        when(plugin.runtime()).thenReturn(runtime);
        BeanContainer container = new DefaultBeanContainer(plugin);
        when(plugin.container()).thenReturn(container);
        PluginCollection emptyPluginCollection = mock(PluginCollection.class);
        when(emptyPluginCollection.iterator()).thenReturn(Collections.emptyIterator());
        when(plugin.children()).thenReturn(emptyPluginCollection);
        return container;
    }
}
