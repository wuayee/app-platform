/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.serializer.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderForm;
import com.huawei.fit.jober.aipp.po.AppBuilderFormPO;
import com.huawei.fit.jober.aipp.serializer.BaseSerializer;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public class AppBuilderFormSerializer implements BaseSerializer<AppBuilderForm, AppBuilderFormPO> {
    @Override
    public AppBuilderFormPO serialize(AppBuilderForm appBuilderForm) {
        if (appBuilderForm == null) {
            return null;
        }
        return AppBuilderFormPO.builder()
                .id(appBuilderForm.getId())
                .name(appBuilderForm.getName())
                .tenantId(appBuilderForm.getTenantId())
                .appearance(appBuilderForm.getAppearance())
                .type(appBuilderForm.getType())
                .createAt(appBuilderForm.getCreateAt())
                .updateAt(appBuilderForm.getUpdateAt())
                .createBy(appBuilderForm.getCreateBy())
                .updateBy(appBuilderForm.getUpdateBy())
                .build();
    }

    @Override
    public AppBuilderForm deserialize(AppBuilderFormPO appBuilderFormPO) {
        if (appBuilderFormPO == null) {
            return null;
        }
        return AppBuilderForm.builder()
                .id(appBuilderFormPO.getId())
                .name(appBuilderFormPO.getName())
                .tenantId(appBuilderFormPO.getTenantId())
                .appearance(appBuilderFormPO.getAppearance())
                .type(appBuilderFormPO.getType())
                .createAt(appBuilderFormPO.getCreateAt())
                .updateAt(appBuilderFormPO.getUpdateAt())
                .createBy(appBuilderFormPO.getCreateBy())
                .updateBy(appBuilderFormPO.getUpdateBy())
                .build();
    }
}
