/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderRuntimeInfo;
import com.huawei.fit.jober.aipp.mapper.AppBuilderRuntimeInfoMapper;
import com.huawei.fit.jober.aipp.repository.AppBuilderRuntimeInfoRepository;
import com.huawei.fit.jober.aipp.serializer.impl.AppBuilderRuntimeInfoSerializer;
import com.huawei.fitframework.annotation.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AppBuilderRuntimeInfoRepository 实现类.
 *
 * @author 张越
 * @since 2024-07-29
 */
@Component
public class AppBuilderRuntimeInfoRepositoryImpl implements AppBuilderRuntimeInfoRepository {
    private final AppBuilderRuntimeInfoMapper mapper;
    private final AppBuilderRuntimeInfoSerializer serializer;

    public AppBuilderRuntimeInfoRepositoryImpl(AppBuilderRuntimeInfoMapper mapper) {
        this.mapper = mapper;
        this.serializer = new AppBuilderRuntimeInfoSerializer();
    }

    @Override
    public List<AppBuilderRuntimeInfo> selectByTraceId(String traceId) {
        return this.mapper.selectByTraceId(traceId)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public void insertOne(AppBuilderRuntimeInfo info) {
        this.mapper.insertOne(this.serializer.serialize(info));
    }
}
