/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.serializer.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderForm;
import com.huawei.fit.jober.aipp.po.AppBuilderFormPo;
import com.huawei.fit.jober.aipp.serializer.BaseSerializer;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public class AppBuilderFormSerializer implements BaseSerializer<AppBuilderForm, AppBuilderFormPo> {
    @Override
    public AppBuilderFormPo serialize(AppBuilderForm appBuilderForm) {
        if (appBuilderForm == null) {
            return null;
        }
        return AppBuilderFormPo.builder()
                .id(appBuilderForm.getId())
                .name(appBuilderForm.getName())
                .tenantId(appBuilderForm.getTenantId())
                .appearance(JsonUtils.toJsonString(appBuilderForm.getAppearance()))
                .type(appBuilderForm.getType())
                .createAt(appBuilderForm.getCreateAt())
                .updateAt(appBuilderForm.getUpdateAt())
                .createBy(appBuilderForm.getCreateBy())
                .updateBy(appBuilderForm.getUpdateBy())
                .build();
    }

    @Override
    public AppBuilderForm deserialize(AppBuilderFormPo appBuilderFormPO) {
        if (appBuilderFormPO == null) {
            return null;
        }
        List<Map<String, Object>> appearance = JsonUtils.parseArray(appBuilderFormPO.getAppearance(), Object[].class)
                .stream()
                .map(ObjectUtils::<Map<String, Object>>cast)
                .collect(Collectors.toList());
        return AppBuilderForm.builder()
                .id(appBuilderFormPO.getId())
                .name(appBuilderFormPO.getName())
                .tenantId(appBuilderFormPO.getTenantId())
                .appearance(appearance)
                .type(appBuilderFormPO.getType())
                .createAt(appBuilderFormPO.getCreateAt())
                .updateAt(appBuilderFormPO.getUpdateAt())
                .createBy(appBuilderFormPO.getCreateBy())
                .updateBy(appBuilderFormPO.getUpdateBy())
                .build();
    }
}
