/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderForm;
import com.huawei.fit.jober.aipp.mapper.AppBuilderFormMapper;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fit.jober.aipp.serializer.impl.AppBuilderFormSerializer;
import com.huawei.fitframework.annotation.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
@Component
public class AppBuilderFormRepositoryImpl implements AppBuilderFormRepository {
    private final AppBuilderFormMapper appBuilderFormMapper;
    private final AppBuilderFormSerializer serializer;
    private final AppBuilderFormPropertyRepository formPropertyRepository;

    public AppBuilderFormRepositoryImpl(AppBuilderFormMapper appBuilderFormMapper,
            AppBuilderFormPropertyRepository formPropertyRepository) {
        this.appBuilderFormMapper = appBuilderFormMapper;
        this.formPropertyRepository = formPropertyRepository;
        this.serializer = new AppBuilderFormSerializer();
    }

    @Override
    public AppBuilderForm selectWithId(String id) {
        AppBuilderForm appBuilderForm = this.serializer.deserialize(this.appBuilderFormMapper.selectWithId(id));
        if (appBuilderForm == null) {
            return null;
        }
        appBuilderForm.setFormPropertyRepository(this.formPropertyRepository);
        return appBuilderForm;
    }

    @Override
    public List<AppBuilderForm> selectWithType(String type, String tenantId) {
        return this.appBuilderFormMapper.selectWithType(type, tenantId)
                .stream()
                .filter(Objects::nonNull)
                .map(appBuilderFormPO -> {
                    AppBuilderForm appBuilderForm = this.serializer.deserialize(appBuilderFormPO);
                    appBuilderForm.setFormPropertyRepository(this.formPropertyRepository);
                    return appBuilderForm;
                })
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

    @Override
    public void delete(String id) {
        this.appBuilderFormMapper.delete(id);
        this.formPropertyRepository.deleteByFormId(id);
    }
}
