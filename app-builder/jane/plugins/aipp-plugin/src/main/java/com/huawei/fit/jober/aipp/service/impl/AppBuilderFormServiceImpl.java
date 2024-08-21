/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jober.aipp.domain.AppBuilderForm;
import com.huawei.fit.jober.aipp.dto.AppBuilderFormDto;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fit.jober.aipp.service.AppBuilderFormService;
import modelengine.fitframework.annotation.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表单接口实现类
 *
 * @author 邬涨财
 * @since 2024-04-19
 */
@Component
public class AppBuilderFormServiceImpl implements AppBuilderFormService {
    private final AppBuilderFormRepository formRepository;

    public AppBuilderFormServiceImpl(AppBuilderFormRepository formRepository) {
        this.formRepository = formRepository;
    }

    @Override
    public Rsp<List<AppBuilderFormDto>> queryByType(HttpClassicServerRequest httpRequest, String type,
            String tenantId) {
        return Rsp.ok(this.formRepository.selectWithType(type, tenantId)
                .stream()
                .map(this::buildFormDto)
                .collect(Collectors.toList()));
    }

    @Override
    public AppBuilderForm selectWithId(String id) {
        return this.formRepository.selectWithId(id);
    }

    private AppBuilderFormDto buildFormDto(AppBuilderForm appBuilderForm) {
        return AppBuilderFormDto.builder()
                .id(appBuilderForm.getId())
                .name(appBuilderForm.getName())
                .appearance(appBuilderForm.getAppearance())
                .type(appBuilderForm.getType())
                .build();
    }
}
