/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.domain;

import com.huawei.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
@Slf4j
public class AppBuilderForm extends BaseDomain {
    private String id;
    private String name;
    private String tenantId;
    private String appearance;
    private String type;
    private List<AppBuilderFormProperty> formProperties;
    private AppBuilderFormPropertyRepository formPropertyRepository;

    public AppBuilderForm(AppBuilderFormPropertyRepository formPropertyRepository) {
        this.formPropertyRepository = formPropertyRepository;
    }

    public List<AppBuilderFormProperty> getFormProperties() {
        return lazyGet(this.formProperties, this::loadFormProperties, this::setFormProperties);
    }

    private List<AppBuilderFormProperty> loadFormProperties() {
        return this.formPropertyRepository.selectWithFormId(this.id);
    }
}
