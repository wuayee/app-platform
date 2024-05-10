/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderConfigProperty;
import com.huawei.fit.jober.aipp.mapper.AppBuilderConfigPropertyMapper;
import com.huawei.fit.jober.aipp.po.AppBuilderConfigPropertyPO;
import com.huawei.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import com.huawei.fit.jober.aipp.serializer.impl.AppBuilderConfigPropertySerializer;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
@Component
public class AppBuilderConfigPropertyRepositoryImpl implements AppBuilderConfigPropertyRepository {
    private final AppBuilderConfigPropertyMapper appBuilderConfigPropertyMapper;

    private final AppBuilderConfigPropertySerializer serializer;

    public AppBuilderConfigPropertyRepositoryImpl(AppBuilderConfigPropertyMapper appBuilderConfigPropertyMapper) {
        this.appBuilderConfigPropertyMapper = appBuilderConfigPropertyMapper;
        this.serializer = new AppBuilderConfigPropertySerializer();
    }

    @Override
    public List<AppBuilderConfigProperty> selectWithConfigId(String configId) {
        return this.appBuilderConfigPropertyMapper.selectWithConfigId(configId)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public AppBuilderConfigProperty selectWithId(String id) {
        return this.serializer.deserialize(this.appBuilderConfigPropertyMapper.selectWithId(id));
    }

    @Override
    public void insertOne(AppBuilderConfigProperty appBuilderConfigProperty) {
        this.appBuilderConfigPropertyMapper.insertOne(this.serializer.serialize(appBuilderConfigProperty));
    }

    @Override
    public void insertMore(List<AppBuilderConfigProperty> appBuilderConfigProperties) {
        List<AppBuilderConfigPropertyPO> pos = appBuilderConfigProperties.stream()
                .map(this.serializer::serialize)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(pos)) {
            this.appBuilderConfigPropertyMapper.insertMore(pos);
        }
    }

    @Override
    public void updateOne(AppBuilderConfigProperty appBuilderConfigProperty) {
        this.appBuilderConfigPropertyMapper.updateOne(this.serializer.serialize(appBuilderConfigProperty));
    }

    @Override
    public int deleteMore(List<String> ids) {
        return CollectionUtils.isEmpty(ids) ? 0 : this.appBuilderConfigPropertyMapper.deleteMore(ids);
    }
}
