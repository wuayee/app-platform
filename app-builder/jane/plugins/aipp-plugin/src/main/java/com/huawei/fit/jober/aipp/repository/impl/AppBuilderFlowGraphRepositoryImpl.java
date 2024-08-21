/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderFlowGraph;
import com.huawei.fit.jober.aipp.mapper.AppBuilderFlowGraphMapper;
import com.huawei.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import com.huawei.fit.jober.aipp.serializer.impl.AppBuilderFlowGraphSerializer;
import modelengine.fitframework.annotation.Component;

import java.util.List;

/**
 * 应用流程图仓库功能实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@Component
public class AppBuilderFlowGraphRepositoryImpl implements AppBuilderFlowGraphRepository {
    private final AppBuilderFlowGraphMapper appBuilderFlowGraphMapper;
    private final AppBuilderFlowGraphSerializer serializer;

    public AppBuilderFlowGraphRepositoryImpl(AppBuilderFlowGraphMapper appBuilderFlowGraphMapper) {
        this.appBuilderFlowGraphMapper = appBuilderFlowGraphMapper;
        this.serializer = new AppBuilderFlowGraphSerializer();
    }

    @Override
    public AppBuilderFlowGraph selectWithId(String id) {
        return this.serializer.deserialize(this.appBuilderFlowGraphMapper.selectWithId(id));
    }

    @Override
    public void insertOne(AppBuilderFlowGraph appBuilderFlowGraph) {
        this.appBuilderFlowGraphMapper.insertOne(this.serializer.serialize(appBuilderFlowGraph));
    }

    @Override
    public void updateOne(AppBuilderFlowGraph appBuilderFlowGraph) {
        this.appBuilderFlowGraphMapper.updateOne(this.serializer.serialize(appBuilderFlowGraph));
    }

    @Override
    public void delete(List<String> ids) {
        this.appBuilderFlowGraphMapper.delete(ids);
    }
}
