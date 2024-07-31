/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.datasource.support;

import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.datasource.AccessMode;
import com.huawei.fitframework.datasource.FitDataSource;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.util.LazyLoader;

import java.util.Objects;

import javax.sql.DataSource;

/**
 * 表示 {@link FitDataSource} 的抽象实现。
 *
 * @author 易文渊
 * @author 李金绪
 * @since 2024-07-27
 */
public abstract class AbstractFitDataSource implements FitDataSource {
    private final String name;
    private final AccessMode mode;
    private final LazyLoader<DataSource> dataSource;

    /**
     * 通过容器构造一个 {@link AbstractFitDataSource} 的新实例。
     *
     * @param container 表示容器的 {@link BeanContainer}。
     * @param config 表示配置的 {@link Config}。
     */
    public AbstractFitDataSource(BeanContainer container, Config config) {
        FitDataSourceConfig dataSourceConfig = FitDataSourceConfig.create(config);
        this.name = dataSourceConfig.getName();
        this.mode = dataSourceConfig.getMode();
        this.dataSource = new LazyLoader<>(() -> this.getDataSource(container, config, this.name, this.mode));
    }

    @Override
    public DataSource get() {
        return this.dataSource.get();
    }

    private DataSource getDataSource(BeanContainer container, Config config, String name, AccessMode mode) {
        if (mode == AccessMode.EXCLUSIVE) {
            return this.buildDataSource(config);
        }
        return container.all(FitDataSource.class)
                .stream()
                .map(BeanFactory::<FitDataSource>get)
                .filter(ds -> ds != this && ds.mode() == AccessMode.SHARED && Objects.equals(ds.name(), name))
                .findFirst()
                .map(FitDataSource::get)
                .orElseGet(() -> this.buildDataSource(config));
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public AccessMode mode() {
        return this.mode;
    }

    /**
     * 创建数据源。
     *
     * @param config 表示插件配置的 {@link Config}。
     * @return 表示数据源的 {@link DataSource}。
     */
    protected abstract DataSource buildDataSource(Config config);
}