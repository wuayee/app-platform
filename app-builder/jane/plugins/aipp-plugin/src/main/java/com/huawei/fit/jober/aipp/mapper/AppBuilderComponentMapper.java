/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.mapper;

import com.huawei.fit.jober.aipp.po.AppBuilderComponentPo;

/**
 * AppBuilder组件映射器
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
public interface AppBuilderComponentMapper {
    /**
     * 通过id查询AppBuilder组件
     *
     * @param id 组件id
     * @return AppBuilder组件信息
     */
    AppBuilderComponentPo selectWithId(String id);
}
