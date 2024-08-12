/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.serializer.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderConfig;
import com.huawei.fit.jober.aipp.po.AppBuilderConfigPo;
import com.huawei.fit.jober.aipp.serializer.BaseSerializer;

/**
 * 应用属性序列化与反序列化实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public class AppBuilderConfigSerializer implements BaseSerializer<AppBuilderConfig, AppBuilderConfigPo> {
    @Override
    public AppBuilderConfigPo serialize(AppBuilderConfig appBuilderConfig) {
        if (appBuilderConfig == null) {
            return null;
        }
        return AppBuilderConfigPo.builder()
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
    public AppBuilderConfig deserialize(AppBuilderConfigPo appBuilderConfigPO) {
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
