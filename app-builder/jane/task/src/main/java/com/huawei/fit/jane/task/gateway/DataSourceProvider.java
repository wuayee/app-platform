/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import javax.sql.DataSource;

/**
 * data source提供方
 *
 * @author l00815032
 * @since 2023/11/28
 */
public interface DataSourceProvider {
    /**
     * 获取data source
     *
     * @return data source
     */
    DataSource getDataSource();
}
