/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import modelengine.fit.jober.aipp.repository.AppBuilderAppRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fitframework.inspection.Validation;

import java.util.List;

/**
 * 应用构建器配置类
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
@Slf4j
public class AppBuilderConfig extends BaseDomain {
    private String id;

    private String formId;

    private String appId;

    private String tenantId;

    private AppBuilderForm form;

    private AppBuilderApp app;

    private List<AppBuilderConfigProperty> configProperties;

    private AppBuilderFormRepository formRepository;

    private AppBuilderFormPropertyRepository formPropertyRepository;

    private AppBuilderConfigPropertyRepository configPropertyRepository;

    private AppBuilderAppRepository appRepository;

    public AppBuilderConfig(AppBuilderFormRepository formRepository,
            AppBuilderFormPropertyRepository formPropertyMapper,
            AppBuilderConfigPropertyRepository configPropertyRepository, AppBuilderAppRepository appRepository) {
        this.formRepository = formRepository;
        this.formPropertyRepository = formPropertyMapper;
        this.configPropertyRepository = configPropertyRepository;
        this.appRepository = appRepository;
    }

    public AppBuilderForm getForm() {
        return lazyGet(this.form, this::loadForm, this::setForm);
    }

    private AppBuilderForm loadForm() {
        Validation.notNull(this.formId, "App builder config can not be null.");
        AppBuilderForm appBuilderForm = this.formRepository.selectWithId(this.formId);
        Validation.notNull(appBuilderForm, "App builder form can not be null.");
        appBuilderForm.setFormPropertyRepository(this.formPropertyRepository);
        return appBuilderForm;
    }

    public List<AppBuilderConfigProperty> getConfigProperties() {
        return lazyGet(this.configProperties, this::loadConfigProperties, this::setConfigProperties);
    }

    private List<AppBuilderConfigProperty> loadConfigProperties() {
        return this.configPropertyRepository.selectWithConfigId(this.id);
    }

    public AppBuilderApp getApp() {
        return lazyGet(this.app, this::loadApp, this::setApp);
    }

    private AppBuilderApp loadApp() {
        Validation.notNull(this.appId, "App builder config can not be null.");
        return this.appRepository.selectWithId(this.appId);
    }
}
