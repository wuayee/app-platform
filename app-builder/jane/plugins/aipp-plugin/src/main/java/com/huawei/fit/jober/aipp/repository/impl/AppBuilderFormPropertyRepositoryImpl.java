/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderFormProperty;
import com.huawei.fit.jober.aipp.mapper.AppBuilderFormPropertyMapper;
import com.huawei.fit.jober.aipp.po.AppBuilderFormPropertyPo;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import com.huawei.fit.jober.aipp.serializer.impl.AppBuilderFormPropertySerializer;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用表单属性仓库功能实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@Component
public class AppBuilderFormPropertyRepositoryImpl implements AppBuilderFormPropertyRepository {
    private final AppBuilderFormPropertyMapper appBuilderFormPropertyMapper;

    private final AppBuilderFormPropertySerializer serializer;

    public AppBuilderFormPropertyRepositoryImpl(AppBuilderFormPropertyMapper appBuilderFormPropertyMapper) {
        this.appBuilderFormPropertyMapper = appBuilderFormPropertyMapper;
        this.serializer = new AppBuilderFormPropertySerializer();
    }

    @Override
    public List<AppBuilderFormProperty> selectWithFormId(String formId) {
        return this.appBuilderFormPropertyMapper.selectWithFormId(formId)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public AppBuilderFormProperty selectWithId(String id) {
        return this.serializer.deserialize(this.appBuilderFormPropertyMapper.selectWithId(id));
    }

    @Override
    public void insertOne(AppBuilderFormProperty appBuilderFormProperty) {
        this.appBuilderFormPropertyMapper.insertOne(this.serializer.serialize(appBuilderFormProperty));
    }

    @Override
    public void insertMore(List<AppBuilderFormProperty> appBuilderFormProperties) {
        List<AppBuilderFormPropertyPo> pos = appBuilderFormProperties.stream()
                .map(this.serializer::serialize)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(pos)) {
            this.appBuilderFormPropertyMapper.insertMore(pos);
        }
    }

    @Override
    public void updateOne(AppBuilderFormProperty appBuilderFormProperty) {
        this.appBuilderFormPropertyMapper.updateOne(this.serializer.serialize(appBuilderFormProperty));
    }

    @Override
    public int deleteMore(List<String> ids) {
        return CollectionUtils.isEmpty(ids) ? 0 : this.appBuilderFormPropertyMapper.deleteMore(ids);
    }

    @Override
    public void deleteByFormId(List<String> formIds) {
        this.appBuilderFormPropertyMapper.deleteByFormIds(formIds);
    }
}
