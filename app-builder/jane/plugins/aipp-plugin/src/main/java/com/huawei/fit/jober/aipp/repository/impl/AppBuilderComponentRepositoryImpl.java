/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderComponent;
import com.huawei.fit.jober.aipp.mapper.AppBuilderComponentMapper;
import com.huawei.fit.jober.aipp.repository.AppBuilderComponentRepository;
import com.huawei.fit.jober.aipp.serializer.impl.AppBuilderComponentSerializer;
import com.huawei.fitframework.annotation.Component;

/**
 * 应用组件属性仓库实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@Component
public class AppBuilderComponentRepositoryImpl implements AppBuilderComponentRepository {
    private final AppBuilderComponentMapper appBuilderComponentMapper;
    private final AppBuilderComponentSerializer serializer;

    public AppBuilderComponentRepositoryImpl(AppBuilderComponentMapper appBuilderComponentMapper) {
        this.appBuilderComponentMapper = appBuilderComponentMapper;
        this.serializer = new AppBuilderComponentSerializer();
    }

    @Override
    public AppBuilderComponent selectWithId(String id) {
        return this.serializer.deserialize(this.appBuilderComponentMapper.selectWithId(id));
    }
}
