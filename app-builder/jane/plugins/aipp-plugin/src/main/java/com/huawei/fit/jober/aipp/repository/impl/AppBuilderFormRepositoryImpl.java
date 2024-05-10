/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderForm;
import com.huawei.fit.jober.aipp.mapper.AppBuilderFormMapper;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fit.jober.aipp.serializer.impl.AppBuilderFormSerializer;
import com.huawei.fitframework.annotation.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
@Component
public class AppBuilderFormRepositoryImpl implements AppBuilderFormRepository {
    private final AppBuilderFormMapper appBuilderFormMapper;
    private final AppBuilderFormSerializer serializer;

    public AppBuilderFormRepositoryImpl(AppBuilderFormMapper appBuilderFormMapper) {
        this.appBuilderFormMapper = appBuilderFormMapper;
        this.serializer = new AppBuilderFormSerializer();
    }

    @Override
    public AppBuilderForm selectWithId(String id) {
        return this.serializer.deserialize(this.appBuilderFormMapper.selectWithId(id));
    }

    @Override
    public List<AppBuilderForm> selectWithType(String type) {
        return this.appBuilderFormMapper.selectWithType(type)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public void insertOne(AppBuilderForm appBuilderForm) {
        this.appBuilderFormMapper.insertOne(this.serializer.serialize(appBuilderForm));
    }

    @Override
    public void updateOne(AppBuilderForm appBuilderForm) {
        this.appBuilderFormMapper.updateOne(this.serializer.serialize(appBuilderForm));
    }
}
