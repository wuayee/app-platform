/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.serializer.impl;

import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.domain.AppBuilderApp;
import com.huawei.fit.jober.aipp.po.AppBuilderAppPO;
import com.huawei.fit.jober.aipp.serializer.BaseSerializer;

import java.util.Objects;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public class AppBuilderAppSerializer implements BaseSerializer<AppBuilderApp, AppBuilderAppPO> {
    @Override
    public AppBuilderAppPO serialize(AppBuilderApp appBuilderApp) {
        if (appBuilderApp == null) {
            return null;
        }
        return AppBuilderAppPO.builder()
                .id(appBuilderApp.getId())
                .name(appBuilderApp.getName())
                .tenantId(appBuilderApp.getTenantId())
                .configId(appBuilderApp.getConfigId())
                .flowGraphId(appBuilderApp.getFlowGraphId())
                .type(appBuilderApp.getType())
                .version(appBuilderApp.getVersion())
                .attributes(JsonUtils.toJsonString(appBuilderApp.getAttributes()))
                .state(appBuilderApp.getState())
                .createAt(appBuilderApp.getCreateAt())
                .updateAt(appBuilderApp.getUpdateAt())
                .createBy(appBuilderApp.getCreateBy())
                .updateBy(appBuilderApp.getUpdateBy())
                .build();
    }

    @Override
    public AppBuilderApp deserialize(AppBuilderAppPO appBuilderAppPO) {
        return Objects.isNull(appBuilderAppPO)
                ? AppBuilderApp.builder().build()
                : AppBuilderApp.builder()
                        .id(appBuilderAppPO.getId())
                        .name(appBuilderAppPO.getName())
                        .tenantId(appBuilderAppPO.getTenantId())
                        .configId(appBuilderAppPO.getConfigId())
                        .flowGraphId(appBuilderAppPO.getFlowGraphId())
                        .type(appBuilderAppPO.getType())
                        .version(appBuilderAppPO.getVersion())
                        .attributes(JsonUtils.parseObject(appBuilderAppPO.getAttributes()))
                        .state(appBuilderAppPO.getState())
                        .createAt(appBuilderAppPO.getCreateAt())
                        .updateAt(appBuilderAppPO.getUpdateAt())
                        .createBy(appBuilderAppPO.getCreateBy())
                        .updateBy(appBuilderAppPO.getUpdateBy())
                        .build();
    }
}
