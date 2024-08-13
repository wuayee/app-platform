/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository;

import com.huawei.fit.jober.aipp.domain.AppBuilderComponent;

/**
 * AppBuilder组件持久化层
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public interface AppBuilderComponentRepository {
    /**
     * 通过组件id查询组件
     *
     * @param id 要查询的组件的id
     * @return 组件结构体
     */
    AppBuilderComponent selectWithId(String id);
}
