/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.domain;

import static com.huawei.fit.jober.aipp.domain.BaseDomain.lazyGet;

import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppBuilderFormProperty {
    private String id;
    private String formId;
    private String name;
    private String dataType;
    private Object defaultValue;
    private AppBuilderForm form;
    private AppBuilderFormRepository formRepository;

    public AppBuilderFormProperty(AppBuilderFormRepository formRepository) {
        this.formRepository = formRepository;
    }

    public AppBuilderForm getForm() {
        return lazyGet(this.form, this::loadForm, this::setForm);
    }

    private AppBuilderForm loadForm() {
        return this.formRepository.selectWithId(this.formId);
    }
}
