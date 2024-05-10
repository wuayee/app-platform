/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import com.huawei.fitframework.annotation.Component;

import javax.sql.DataSource;

/**
 * {@link DataSourceProvider} 的a3000默认实现。
 *
 * @author s00664640
 * @since 2023/11/28
 */
@Component
public class BaseDataSourceProvider implements DataSourceProvider {
    private final DataSource dataSource;

    public BaseDataSourceProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }
}
