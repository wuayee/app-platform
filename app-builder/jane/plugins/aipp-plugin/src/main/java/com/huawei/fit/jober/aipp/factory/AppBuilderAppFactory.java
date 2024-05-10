/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.factory;

import com.huawei.fit.jober.aipp.domain.AppBuilderApp;
import com.huawei.fit.jober.aipp.repository.AppBuilderAppRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderConfigRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.inspection.Validation;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-16
 */
@Component
public class AppBuilderAppFactory {
    private final AppBuilderFlowGraphRepository flowGraphRepository;
    private final AppBuilderConfigRepository configRepository;
    private final AppBuilderFormRepository formRepository;
    private final AppBuilderConfigPropertyRepository configPropertyRepository;
    private final AppBuilderFormPropertyRepository formPropertyRepository;
    private final AppBuilderAppRepository appRepository;

    public AppBuilderAppFactory(AppBuilderFlowGraphRepository flowGraphRepository, AppBuilderConfigRepository configRepository,
            AppBuilderFormRepository formRepository, AppBuilderConfigPropertyRepository configPropertyRepository,
            AppBuilderFormPropertyRepository formPropertyRepository, AppBuilderAppRepository appRepository) {
        this.flowGraphRepository = flowGraphRepository;
        this.configRepository = configRepository;
        this.configPropertyRepository = configPropertyRepository;
        this.formRepository = formRepository;
        this.formPropertyRepository = formPropertyRepository;
        this.appRepository = appRepository;
    }

    public AppBuilderApp create() {
        return new AppBuilderApp(this.flowGraphRepository,
                this.configRepository,
                this.formRepository,
                this.configPropertyRepository,
                this.formPropertyRepository);
    }

    public AppBuilderApp create(String appId) {
        AppBuilderApp appBuilderApp = this.appRepository.selectWithId(appId);
        Validation.notNull(appBuilderApp, "App builder app can not be null.");
        appBuilderApp.setFlowGraphRepository(this.flowGraphRepository);
        appBuilderApp.setConfigRepository(this.configRepository);
        appBuilderApp.setFormRepository(this.formRepository);
        appBuilderApp.setConfigPropertyRepository(this.configPropertyRepository);
        appBuilderApp.setFormPropertyRepository(this.formPropertyRepository);
        return appBuilderApp;
    }

    public void save(AppBuilderApp appBuilderApp) {
        this.appRepository.insertOne(appBuilderApp);
    }

    public void update(AppBuilderApp appBuilderApp) {
        this.appRepository.updateOne(appBuilderApp);
    }
}
