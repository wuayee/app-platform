/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.datasource;

import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.ioc.BeanSupplier;

import javax.sql.DataSource;

/**
 * FIT 框架数据源工厂的 Bean 接口定义。
 *
 * @author 易文渊
 * @since 2024-07-27
 */
public interface FitDataSource extends BeanSupplier<DataSource> {
    /**
     * 获取数据源的名字。
     *
     * @return 表示数据源名字的 {@link String}。
     */
    String name();

    /**
     * 获取数据源的访问模式。
     *
     * @return 表示数据源访问模式的 {@link Scope}。
     */
    AccessMode mode();
}