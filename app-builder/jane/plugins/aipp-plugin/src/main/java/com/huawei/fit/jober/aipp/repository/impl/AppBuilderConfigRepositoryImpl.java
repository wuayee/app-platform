/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderConfig;
import com.huawei.fit.jober.aipp.mapper.AppBuilderConfigMapper;
import com.huawei.fit.jober.aipp.po.AppBuilderConfigPo;
import com.huawei.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderConfigRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fit.jober.aipp.serializer.impl.AppBuilderConfigSerializer;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用属性仓库实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@Component
public class AppBuilderConfigRepositoryImpl implements AppBuilderConfigRepository {
    private final AppBuilderConfigMapper appBuilderConfigMapper;
    private final AppBuilderConfigSerializer serializer;
    private final AppBuilderConfigPropertyRepository appBuilderConfigPropertyRepository;
    private final AppBuilderFormRepository appBuilderFormRepository;

    public AppBuilderConfigRepositoryImpl(AppBuilderConfigMapper appBuilderConfigMapper,
            AppBuilderConfigPropertyRepository appBuilderConfigPropertyRepository,
            AppBuilderFormRepository appBuilderFormRepository) {
        this.appBuilderConfigMapper = appBuilderConfigMapper;
        this.appBuilderConfigPropertyRepository = appBuilderConfigPropertyRepository;
        this.appBuilderFormRepository = appBuilderFormRepository;
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

    @Override
    public void delete(List<String> ids) {
        List<AppBuilderConfigPo> configPos = this.appBuilderConfigMapper.selectWithIds(ids);
        List<String> configIds = configPos.stream().map(AppBuilderConfigPo::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(configIds)) {
            this.appBuilderConfigMapper.delete(configIds);
            this.appBuilderConfigPropertyRepository.deleteByConfigIds(configIds);
        }
        List<String> formIds = configPos.stream().map(AppBuilderConfigPo::getFormId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(formIds)) {
            this.appBuilderFormRepository.delete(formIds);
        }
    }
}
