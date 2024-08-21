/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate;

import com.huawei.fit.jane.task.gateway.DataSourceProvider;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.ioc.BeanSupplier;

import javax.sql.DataSource;

/**
 * DataSourceFactory
 *
 * @author 梁济时
 * @since 2023/11/17
 */
@Component
public class DataSourceFactory implements BeanSupplier<DataSource> {
    private final DataSourceProvider provider;

    public DataSourceFactory(DataSourceProvider provider) {
        this.provider = provider;
    }

    @Override
    public DataSource get() {
        return this.provider.getDataSource();
    }
}
