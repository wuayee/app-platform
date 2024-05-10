/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.serializer.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderConfig;
import com.huawei.fit.jober.aipp.po.AppBuilderConfigPO;
import com.huawei.fit.jober.aipp.serializer.BaseSerializer;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public class AppBuilderConfigSerializer implements BaseSerializer<AppBuilderConfig, AppBuilderConfigPO> {
    @Override
    public AppBuilderConfigPO serialize(AppBuilderConfig appBuilderConfig) {
        if (appBuilderConfig == null) {
            return null;
        }
        return AppBuilderConfigPO.builder()
                .id(appBuilderConfig.getId())
                .formId(appBuilderConfig.getFormId())
                .tenantId(appBuilderConfig.getTenantId())
                .appId(appBuilderConfig.getAppId())
                .createAt(appBuilderConfig.getCreateAt())
                .updateAt(appBuilderConfig.getUpdateAt())
                .createBy(appBuilderConfig.getCreateBy())
                .updateBy(appBuilderConfig.getUpdateBy())
                .build();
    }

    @Override
    public AppBuilderConfig deserialize(AppBuilderConfigPO appBuilderConfigPO) {
        if (appBuilderConfigPO == null) {
            return null;
        }
        return AppBuilderConfig.builder()
                .id(appBuilderConfigPO.getId())
                .formId(appBuilderConfigPO.getFormId())
                .tenantId(appBuilderConfigPO.getTenantId())
                .appId(appBuilderConfigPO.getAppId())
                .createAt(appBuilderConfigPO.getCreateAt())
                .updateAt(appBuilderConfigPO.getUpdateAt())
                .createBy(appBuilderConfigPO.getCreateBy())
                .updateBy(appBuilderConfigPO.getUpdateBy())
                .build();
    }
}
