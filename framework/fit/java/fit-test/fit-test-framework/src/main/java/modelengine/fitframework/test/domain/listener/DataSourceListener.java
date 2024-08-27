/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.test.domain.listener;

import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanNotFoundException;
import modelengine.fitframework.test.annotation.EnableDataSource;
import modelengine.fitframework.test.domain.TestContext;
import modelengine.fitframework.test.domain.util.AnnotationUtils;

import org.h2.jdbcx.JdbcConnectionPool;

import java.util.Optional;

import javax.sql.DataSource;

/**
 * 用于注入 dataSource 的监听器。
 *
 * @author 易文渊
 * @since 2024-07-21
 */
public class DataSourceListener implements TestListener {
    @Override
    public void beforeTestClass(TestContext context) {
        Class<?> clazz = context.testClass();
        Optional<EnableDataSource> annotationOption = AnnotationUtils.getAnnotation(clazz, EnableDataSource.class);
        if (!annotationOption.isPresent()) {
            return;
        }
        BeanContainer beanContainer = context.plugin().container();
        try {
            beanContainer.beans().get(DataSource.class);
        } catch (BeanNotFoundException e) {
            EnableDataSource enableDataSource = annotationOption.get();
            DataSource dataSource = JdbcConnectionPool.create(enableDataSource.model().getUrl(), "sa", "sa");
            beanContainer.registry().register(dataSource);
        }
    }
}