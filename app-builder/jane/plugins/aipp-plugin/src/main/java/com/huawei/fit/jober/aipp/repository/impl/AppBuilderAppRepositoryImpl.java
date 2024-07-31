/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository.impl;

import com.huawei.fit.jober.aipp.condition.AppQueryCondition;
import com.huawei.fit.jober.aipp.domain.AppBuilderApp;
import com.huawei.fit.jober.aipp.mapper.AppBuilderAppMapper;
import com.huawei.fit.jober.aipp.repository.AppBuilderAppRepository;
import com.huawei.fit.jober.aipp.serializer.impl.AppBuilderAppSerializer;
import com.huawei.fitframework.annotation.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用创建仓库实现类
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
@Component
public class AppBuilderAppRepositoryImpl implements AppBuilderAppRepository {
    private final AppBuilderAppMapper appBuilderAppMapper;
    private final AppBuilderAppSerializer serializer;

    public AppBuilderAppRepositoryImpl(AppBuilderAppMapper appBuilderAppMapper) {
        this.appBuilderAppMapper = appBuilderAppMapper;
        this.serializer = new AppBuilderAppSerializer();
    }

    @Override
    public AppBuilderApp selectWithId(String id) {
        return this.serializer.deserialize(this.appBuilderAppMapper.selectWithId(id));
    }

    @Override
    public void insertOne(AppBuilderApp appBuilderApp) {
        this.appBuilderAppMapper.insertOne(this.serializer.serialize(appBuilderApp));
    }

    @Override
    public void updateOne(AppBuilderApp appBuilderApp) {
        this.appBuilderAppMapper.updateOne(this.serializer.serialize(appBuilderApp));
    }

    @Override
    public List<AppBuilderApp> selectWithLatestApp(AppQueryCondition cond, String tenantId, long offset, int limit) {
        return this.appBuilderAppMapper.selectByTenantIdWithPage(cond, tenantId, offset, limit)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppBuilderApp> selectWithCondition(AppQueryCondition cond) {
        return this.appBuilderAppMapper.selectWithCondition(cond)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public long countWithLatestApp(String tenantId, AppQueryCondition cond) {
        return this.appBuilderAppMapper.countByTenantId(tenantId, cond);
    }

    @Override
    public void delete(String appId) {
        this.appBuilderAppMapper.delete(appId);
    }
}
