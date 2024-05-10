/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import com.huawei.fitframework.annotation.Component;

import javax.sql.DataSource;

@Component
public class DefaultDataSourceProvider implements DataSourceProvider {
    private final DataSource dataSource;

    public DefaultDataSourceProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }
}
