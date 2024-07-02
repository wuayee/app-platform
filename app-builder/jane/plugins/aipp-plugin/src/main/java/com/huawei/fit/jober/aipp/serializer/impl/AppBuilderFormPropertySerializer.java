/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.serializer.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderFormProperty;
import com.huawei.fit.jober.aipp.enums.FormPropertyTypeEnum;
import com.huawei.fit.jober.aipp.po.AppBuilderFormPropertyPO;
import com.huawei.fit.jober.aipp.serializer.BaseSerializer;
import com.huawei.fit.jober.aipp.util.JsonUtils;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public class AppBuilderFormPropertySerializer
        implements BaseSerializer<AppBuilderFormProperty, AppBuilderFormPropertyPO> {
    @Override
    public AppBuilderFormPropertyPO serialize(AppBuilderFormProperty appBuilderFormProperty) {
        if (appBuilderFormProperty == null) {
            return null;
        }
        return AppBuilderFormPropertyPO.builder()
                .id(appBuilderFormProperty.getId())
                .formId(appBuilderFormProperty.getFormId())
                .name(appBuilderFormProperty.getName())
                .dataType(appBuilderFormProperty.getDataType())
                .defaultValue(JsonUtils.toJsonString(appBuilderFormProperty.getDefaultValue()))
                .build();
    }

    @Override
    public AppBuilderFormProperty deserialize(AppBuilderFormPropertyPO appBuilderFormPropertyPO) {
        if (appBuilderFormPropertyPO == null) {
            return null;
        }
        return AppBuilderFormProperty.builder()
                .id(appBuilderFormPropertyPO.getId())
                .formId(appBuilderFormPropertyPO.getFormId())
                .name(appBuilderFormPropertyPO.getName())
                .dataType(appBuilderFormPropertyPO.getDataType())
                .defaultValue(this.getDefaultValue(appBuilderFormPropertyPO))
                .build();
    }

    private Object getDefaultValue(AppBuilderFormPropertyPO po) {
        return JsonUtils.parseObject(po.getDefaultValue(), FormPropertyTypeEnum.getClazz(po.getDataType()));
    }
}
