/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.domain;

import static com.huawei.fit.jober.aipp.domain.BaseDomain.lazyGet;

import com.huawei.fit.jober.aipp.repository.AppBuilderConfigRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fitframework.inspection.Validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应用构建器配置属性类
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-16
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderConfigProperty {
    private String id;
    private String nodeId;
    private String formPropertyId;
    private String configId;
    private AppBuilderFormProperty formProperty;
    private AppBuilderConfig config;
    private AppBuilderConfigRepository configRepository;
    private AppBuilderFormRepository formRepository;
    private AppBuilderFormPropertyRepository formPropertyRepository;

    public AppBuilderConfigProperty(AppBuilderConfigRepository configRepository,
            AppBuilderFormRepository formRepository, AppBuilderFormPropertyRepository formPropertyRepository) {
        this.configRepository = configRepository;
        this.formRepository = formRepository;
        this.formPropertyRepository = formPropertyRepository;
    }

    public AppBuilderFormProperty getFormProperty() {
        return lazyGet(this.formProperty, this::loadFormProperty, this::setFormProperty);
    }

    private AppBuilderFormProperty loadFormProperty() {
        return this.formPropertyRepository.selectWithId(this.formPropertyId);
    }

    private AppBuilderConfig getConfig() {
        return lazyGet(this.config, this::loadConfig, this::setConfig);
    }

    private AppBuilderConfig loadConfig() {
        AppBuilderConfig appBuilderConfig = this.configRepository.selectWithId(this.configId);
        Validation.notNull(appBuilderConfig, "App builder config can not be null.");
        appBuilderConfig.setFormRepository(this.formRepository);
        return appBuilderConfig;
    }
}
