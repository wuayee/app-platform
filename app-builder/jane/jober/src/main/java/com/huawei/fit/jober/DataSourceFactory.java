/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober;

import com.huawei.fit.jane.task.gateway.DataSourceProvider;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.ioc.BeanSupplier;

import javax.sql.DataSource;

/**
 * DataSourceFactory
 *
 * @author l00815032
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
