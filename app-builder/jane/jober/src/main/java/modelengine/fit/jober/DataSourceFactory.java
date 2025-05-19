/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober;

import modelengine.fit.jane.task.gateway.DataSourceProvider;

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
