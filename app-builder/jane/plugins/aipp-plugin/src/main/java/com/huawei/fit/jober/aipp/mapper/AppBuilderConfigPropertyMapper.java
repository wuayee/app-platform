/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.mapper;

import com.huawei.fit.jober.aipp.po.AppBuilderConfigPropertyPO;

import java.util.List;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-16
 */
public interface AppBuilderConfigPropertyMapper {
    List<AppBuilderConfigPropertyPO> selectWithConfigId(String configId);

    AppBuilderConfigPropertyPO selectWithId(String id);

    void insertOne(AppBuilderConfigPropertyPO insert);

    void insertMore(List<AppBuilderConfigPropertyPO> jadeConfigProperties);

    void updateOne(AppBuilderConfigPropertyPO update);

    int deleteMore(List<String> ids);
}
