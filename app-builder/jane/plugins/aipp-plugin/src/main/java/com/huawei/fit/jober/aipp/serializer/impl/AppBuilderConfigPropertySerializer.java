/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.serializer.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderConfigProperty;
import com.huawei.fit.jober.aipp.po.AppBuilderConfigPropertyPO;
import com.huawei.fit.jober.aipp.serializer.BaseSerializer;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public class AppBuilderConfigPropertySerializer implements BaseSerializer<AppBuilderConfigProperty, AppBuilderConfigPropertyPO> {
    @Override
    public AppBuilderConfigPropertyPO serialize(AppBuilderConfigProperty appBuilderConfigProperty) {
        if (appBuilderConfigProperty == null) {
            return null;
        }
        return AppBuilderConfigPropertyPO.builder()
                .id(appBuilderConfigProperty.getId())
                .nodeId(appBuilderConfigProperty.getNodeId())
                .formPropertyId(appBuilderConfigProperty.getFormPropertyId())
                .configId(appBuilderConfigProperty.getConfigId())
                .build();
    }

    @Override
    public AppBuilderConfigProperty deserialize(AppBuilderConfigPropertyPO appBuilderConfigPropertyPO) {
        if (appBuilderConfigPropertyPO == null) {
            return null;
        }
        return AppBuilderConfigProperty.builder()
                .id(appBuilderConfigPropertyPO.getId())
                .nodeId(appBuilderConfigPropertyPO.getNodeId())
                .formPropertyId(appBuilderConfigPropertyPO.getFormPropertyId())
                .configId(appBuilderConfigPropertyPO.getConfigId())
                .build();
    }
}
