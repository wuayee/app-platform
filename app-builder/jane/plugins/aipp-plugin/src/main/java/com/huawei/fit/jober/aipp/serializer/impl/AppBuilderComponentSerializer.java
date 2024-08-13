/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.serializer.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderComponent;
import com.huawei.fit.jober.aipp.po.AppBuilderComponentPo;
import com.huawei.fit.jober.aipp.serializer.BaseSerializer;

/**
 * 应用属性组件序列化与反序列化实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public class AppBuilderComponentSerializer implements BaseSerializer<AppBuilderComponent, AppBuilderComponentPo> {
    @Override
    public AppBuilderComponentPo serialize(AppBuilderComponent appBuilderComponent) {
        if (appBuilderComponent == null) {
            return null;
        }
        return AppBuilderComponentPo.builder()
                .id(appBuilderComponent.getId())
                .name(appBuilderComponent.getName())
                .type(appBuilderComponent.getType())
                .description(appBuilderComponent.getDescription())
                .formId(appBuilderComponent.getFormId())
                .serviceId(appBuilderComponent.getServiceId())
                .tenantId(appBuilderComponent.getTenantId())
                .createAt(appBuilderComponent.getCreateAt())
                .updateAt(appBuilderComponent.getUpdateAt())
                .createBy(appBuilderComponent.getCreateBy())
                .updateBy(appBuilderComponent.getUpdateBy())
                .build();
    }

    @Override
    public AppBuilderComponent deserialize(AppBuilderComponentPo appBuilderComponentPO) {
        if (appBuilderComponentPO == null) {
            return null;
        }
        return AppBuilderComponent.builder()
                .id(appBuilderComponentPO.getId())
                .name(appBuilderComponentPO.getName())
                .type(appBuilderComponentPO.getType())
                .description(appBuilderComponentPO.getDescription())
                .formId(appBuilderComponentPO.getFormId())
                .serviceId(appBuilderComponentPO.getServiceId())
                .tenantId(appBuilderComponentPO.getTenantId())
                .createAt(appBuilderComponentPO.getCreateAt())
                .updateAt(appBuilderComponentPO.getUpdateAt())
                .createBy(appBuilderComponentPO.getCreateBy())
                .updateBy(appBuilderComponentPO.getUpdateBy())
                .build();
    }
}
