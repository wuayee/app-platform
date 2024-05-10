/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderConfig;
import com.huawei.fit.jober.aipp.mapper.AppBuilderConfigMapper;
import com.huawei.fit.jober.aipp.repository.AppBuilderConfigRepository;
import com.huawei.fit.jober.aipp.serializer.impl.AppBuilderConfigSerializer;
import com.huawei.fitframework.annotation.Component;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
@Component
public class AppBuilderConfigRepositoryImpl implements AppBuilderConfigRepository {
    private final AppBuilderConfigMapper appBuilderConfigMapper;
    private final AppBuilderConfigSerializer serializer;

    public AppBuilderConfigRepositoryImpl(AppBuilderConfigMapper appBuilderConfigMapper) {
        this.appBuilderConfigMapper = appBuilderConfigMapper;
        this.serializer = new AppBuilderConfigSerializer();
    }

    @Override
    public AppBuilderConfig selectWithId(String id) {
        return this.serializer.deserialize(this.appBuilderConfigMapper.selectWithId(id));
    }


    @Override
    public void insertOne(AppBuilderConfig appBuilderConfig) {
        this.appBuilderConfigMapper.insertOne(this.serializer.serialize(appBuilderConfig));
    }

    @Override
    public void updateOne(AppBuilderConfig appBuilderConfig) {
        this.appBuilderConfigMapper.updateOne(this.serializer.serialize(appBuilderConfig));
    }

    @Override
    public AppBuilderConfig selectWithAppId(String appId) {
        return this.serializer.deserialize(this.appBuilderConfigMapper.selectWithAppId(appId));
    }
}
