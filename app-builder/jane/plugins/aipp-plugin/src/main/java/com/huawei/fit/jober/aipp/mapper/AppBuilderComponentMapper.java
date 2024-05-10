/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.mapper;

import com.huawei.fit.jober.aipp.po.AppBuilderComponentPO;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-16
 */
public interface AppBuilderComponentMapper {
    AppBuilderComponentPO selectWithId(String id);
}
